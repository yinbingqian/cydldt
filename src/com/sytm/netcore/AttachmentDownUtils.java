package com.sytm.netcore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lnpdit.chatuidemo.R;
import com.sytm.common.Constant;

public class AttachmentDownUtils {
	private Context mContext;
	private Handler mHandler;
	private String mDownloadUrl; // 文件下载url，已做非空检查
	private String mFileName;
	private Message msg;
//	private final String APP_FOLDER = "sytm"; // sd卡应用目录
//	private final String APK_FOLDER = "downloadFile"; // 下载apk文件目录
	public static final int MSG_UNDOWN = 0; // 未开始下载
	public static final int MSG_DOWNING = 1; // 下载中
	public static final int MSG_FINISH = 1; // 下载完成
	public static final int MSG_FAILURE = 2;// 下载失败
	private NotificationManager mNotifManager;
	private Notification mDownNotification;
	private RemoteViews mContentView; // 下载进度View
	private PendingIntent mDownPendingIntent;
	private boolean downSuc ;
	private File saveFilePath ;
	public AttachmentDownUtils(Context context, Handler handler,
			String downloadUrl) {
		mContext = context;
		mHandler = handler;
		mFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);

		downloadUrl = downloadUrl.substring(0,downloadUrl.lastIndexOf("/")+1);
		try {
			mDownloadUrl =Constant.Service_Domain+downloadUrl+URLEncoder.encode(mFileName, "UTF-8");
			Log.i("下载文件地址", ""+mDownloadUrl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mNotifManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		msg = new Message();
	}

	@SuppressWarnings("deprecation")
	public void run() {
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Message downingMsg = new Message();
				downingMsg.what = MSG_DOWNING;
				mHandler.sendMessage(downingMsg);
				// SD卡准备好
//				File sdcardDir = Environment.getExternalStorageDirectory();
				// 文件存放路径：sdcard/DownDemo/apkFile
				File folder = new File(Constant.DownLoadPath);
				Log.i("文件存放路径：", folder.getPath());
				if (!folder.exists()) {
					// 创建存放目录
					folder.mkdirs();
				}
				saveFilePath = new File(folder, mFileName);
				mDownNotification = new Notification(
						android.R.drawable.stat_sys_download, "开始下载附件！",
						System.currentTimeMillis());
				mDownNotification.flags = Notification.FLAG_ONGOING_EVENT;
				mDownNotification.flags = Notification.FLAG_AUTO_CANCEL;
				mContentView = new RemoteViews(mContext.getPackageName(),R.layout.notification_downapp);
			    mContentView.setImageViewResource(R.id.downLoadIcon,android.R.drawable.stat_sys_download);
				mDownPendingIntent = PendingIntent.getActivity(mContext, 0,new Intent(), 0);
				//boolean downSuc = downloadFile(mDownloadUrl, saveFilePath);
				new Task().execute("DOWNLOAD");
			
			} else {
				Toast.makeText(mContext, "SD卡未准备好",
						Toast.LENGTH_SHORT).show();
				msg.what = MSG_FAILURE;
			}
		} catch (Exception e) {
			msg.what = MSG_FAILURE;
		} finally {
			mHandler.sendMessage(msg);
		}

	}
	/**
	 * 异步线程
	 * 
	 * @author wyq
	 * 
	 */
	protected class Task extends AsyncTask<String, String, String> {
		// 开启另外一个线程执行任务
		@Override
		protected String doInBackground(String... params) {
			String exeParam = params[0];
			try {
				downSuc = downloadFile(mDownloadUrl, saveFilePath);
			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		@SuppressWarnings("deprecation")
		protected void onPostExecute(String result) {
			if (result.equals("DOWNLOAD")){
				if (downSuc) {
					msg.what = MSG_FINISH;
					Notification notification = new Notification(
							android.R.drawable.stat_sys_download_done, "附件"+mFileName+"下载完成！",
							System.currentTimeMillis());
					notification.flags = Notification.FLAG_ONGOING_EVENT;
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String type = getMIMEType(mFileName); 
					intent.setDataAndType(Uri.fromFile(saveFilePath), type); 
					PendingIntent contentIntent = PendingIntent.getActivity(
							mContext, 0, intent, 0);
					notification.setLatestEventInfo(mContext, "附件"+mFileName+"下载完成！", null,
							contentIntent);
					mNotifManager.notify(R.drawable.download, notification);
				} else {
					msg.what = MSG_FAILURE;
					Notification notification = new Notification(
							android.R.drawable.stat_sys_download_done, "附件"+mFileName+"下载失败！",
							System.currentTimeMillis());
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					PendingIntent contentIntent = PendingIntent.getActivity(
							mContext, 0, new Intent(), 0);
					notification.setLatestEventInfo(mContext, "附件"+mFileName+"下载失败！", null,
							contentIntent);

					 mNotifManager.notify(R.drawable.download, notification);

				}
			}
		
		}
	}
	/**
	 * 
	 * Desc:文件下载
	 * 
	 * @param downloadUrl
	 *            下载URL
	 * @param saveFilePath
	 *            保存文件路径
	 * @return ture:下载成功false:下载失败
	 */
	public boolean downloadFile(String downloadUrl, File saveFilePath) {
		int fileSize = -1;
		int downFileSize = 0;
		boolean result = false;
		int progress = 0;
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (null == conn) {
				return false;
			}
			// 读取超时时间 毫秒级
			conn.setReadTimeout(10000);
			conn.setRequestMethod("GET");
			conn .setRequestProperty("Accept-Encoding", "identity"); 
			conn.setDoInput(true);
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				fileSize = conn.getContentLength();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(saveFilePath);
				byte[] buffer = new byte[1024];
				int i = 0;
				int tempProgress = -1;
				while ((i = is.read(buffer)) != -1) {
					downFileSize = downFileSize + i;
					// 下载进度
					progress = (int) (downFileSize * 100.0 / fileSize);
					fos.write(buffer, 0, i);
					synchronized (this) {
						if (downFileSize == fileSize) {
							// 下载完成
							mNotifManager.cancel(0);
						} else if (tempProgress != progress) {
							// 下载进度发生改变，则发送Message							
							 mContentView.setTextViewText(R.id.progressPercent,			 
							 progress + "%");
							 mContentView.setTextViewText(R.id.progressName, mFileName);
							 mContentView.setProgressBar(R.id.downLoadProgress,							 
							 100, progress, false);							 
							mDownNotification.contentView = mContentView;
							mDownNotification.contentIntent = mDownPendingIntent;
							mNotifManager.notify(0, mDownNotification);
							tempProgress = progress;
						}
					}
				}
				fos.flush();
				fos.close();
				is.close();
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;

	}
	
	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * @param file
	 */ 
	@SuppressLint("DefaultLocale")
	private String getMIMEType(String FileName) { 
	     
		String type="*/*"; 
	    int dotIndex = FileName.lastIndexOf("."); 
	    if(dotIndex < 0){ 
	        return type; 
	    } 
	    /* 获取文件的后缀名*/ 
	    String end=FileName.substring(dotIndex,FileName.length()).toLowerCase(); 
	    if(end=="")return type; 
	    //在MIME和文件类型的匹配表中找到对应的MIME类型。 
	    for(int i=0;i<Constant.MIME_MapTable.length;i++){ 
	        if(end.equals(Constant.MIME_MapTable[i][0])) 
	            type = Constant.MIME_MapTable[i][1]; 
	    }        
	    return type; 
	} 
}
