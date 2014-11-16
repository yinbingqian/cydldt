package com.sytm.crash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lnpdit.chatuidemo.R;
import com.sytm.common.Constant;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.DateTimeUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author wyq
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	private SharedPreferences sp;

	// 用于格式化日期,作为日志文件名的一部分
	// private DateFormat formatter = new DateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, mContext.getResources().getString(R.string.Sorry_abnormal_procedures_will_now_exit), Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}
		}.start();
		// 收集并上传异常信息
		new Thread() {
			@Override
			public void run() {
				// 收集设备参数信息
				collectDeviceInfo(mContext);
				// 保存日志文件
			   String filename=saveCrashInfo2File(ex);
			   //上传日志文件到服务器
			   uploadLogFile(filename);
			}
		}.start();		
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			sp = ctx.getSharedPreferences("TMMTC", Context.MODE_PRIVATE);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
				infos.put("userName", sp.getString("myname", " "));
				infos.put("empNum", sp.getString("empnum", " "));
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "收集程序包信息是发生了一个错误！", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				//Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "收集程序信息是发生了一个错误！", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + ":" + value + "\n");
		}
		sb.append("-----------------------------------------\n");
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		Log.e(TAG,"TMTL has crash error:\n"+result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = DateTimeUtils.getDate("yyMMddHHmmss");
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File dir = new File(Constant.CrashLogPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(
						Constant.CrashLogPath + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();

			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "写入文件时发生了一个错误...", e);
		}
		return null;
	}

	/***
	 * 上传日志文件
	 * 
	 * @param filename
	 * @return true/false
	 */
	private boolean uploadLogFile(String filename) {
		File file = new File(Constant.CrashLogPath + filename);
		if (file.exists()) {
			ServiceContent sc = new ServiceContent();
			sc.setMultipost(true);
			sc.setClassname("ClientCrashHanlder");
			sc.setMethodname("CollectCrashLog");
			sc.addStringPart("devicetype", "3");
			sc.addFilePart("file", file);
			ServiceResult sr = Network.postDataService(sc);
			if (sr.GetIsError()) {
				Log.e(TAG, sr.getMessage());
				return false;
			} else {
				Log.d(TAG, "日志收集成功！");
				//清理日志
				file.delete();
				return true;

			}
		} else {
			Log.w(TAG, "Crash log file no found!--" + Constant.CrashLogPath
					+ filename);
			return false;
		}
	}
}
