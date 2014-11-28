package com.sytm.tmkq;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lnpdit.chatuidemo.R;
import com.sytm.application.LocationUtils;
import com.sytm.bean.CountModel;
import com.sytm.bean.PersonalInformationMode;
import com.sytm.bean.PunchStateModel;
import com.sytm.common.Constant;
import com.sytm.netcore.ImageURL;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.DataCleanManager;
import com.sytm.util.DateTimeUtils;
import com.sytm.util.JsonUtils;
import com.sytm.util.SystemUtils;
import com.sytm.view.LoadingDialog;

@SuppressLint("HandlerLeak")
public class KqPageMain extends Activity {
	private String empid = "";
	private SharedPreferences sp;
	private TextView year, time;
	private ServiceResult sr = new ServiceResult();
	private ServiceResult srlogin = new ServiceResult();
	private ServiceResult srpun = new ServiceResult();
	private Button left, right,dk_btn;
	private RelativeLayout rel33;
	private String mYear;
	private String mMonth;
	private String mDay;
	private String mWay;
	private CountModel countModel;
	private String lng = "", lat = "", tishi = "", GetType = "",getAddrStr="",
			wifiaddress = "";
	private Calendar c;
	private Dialog dialog2;
	private boolean running = true, running2 = true, open = true;
	private int threadtime = 0;
	private PopupWindow popupWindow;
	private boolean isshow = true;
	private String timeloc, city, district;
	private PunchStateModel punchStateModel;
	private BroadcastReceiver myReceiver;
	private Dialog dialog;
	private Thread thread;
	private String imei;
	private SystemUtils su = new SystemUtils(this);
	Runnable sendable = new Runnable() {
		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler2.sendEmptyMessage(0);
			}
		}
	};

	Handler handler2 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (running) {
				Configuration();
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		iniOnCreate();
		running = true;
		thread = new Thread(sendable);
		thread.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		running = false;
		thread = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pagemian);

		initialize();

		sp = this.getSharedPreferences("TMMTC", Context.MODE_PRIVATE);
		dialog2 = new LoadingDialog().createLoadingDialog(this, getResources()
				.getString(R.string.punch_waiting), true);
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		imei = tm.getDeviceId();
		IntentFilter filter = new IntentFilter();
		filter.addAction("pagemain");
		filter.setPriority(Integer.MAX_VALUE);
		myReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getIntExtra("TAGS", 0) == 2) {
					iniOnCreate();
				} else if (intent.getIntExtra("TAGS", 0) == 3) {
					lng = intent.getStringExtra("Lng");
					lat = intent.getStringExtra("Lat");
					GetType = intent.getStringExtra("GetType");
					getAddrStr = intent.getStringExtra("getAddrStr");
					wifiaddress = intent.getStringExtra("wifimac");
					if (wifiaddress.equals("") || wifiaddress.equals("null")) {
						wifiaddress = " ";
					}
					running2 = false;
					new Task().execute("pagePunch");
				} else if (intent.getIntExtra("TAGS", 0) == 4) {
					if (dialog2.isShowing()) {
						dialog2.dismiss();
					}
					running2 = false;
					tishi = getResources().getString(
							R.string.Please_check_network_try_again);
					popuWidow(3);
				} else if (intent.getIntExtra("TAGS", 0) == 5) {
					if (dialog2.isShowing()) {
						dialog2.dismiss();
					}
					running2 = false;
					tishi = getResources().getString(
							R.string.Please_check_network_try_again);
					popuWidow(3);
				} else if (intent.getIntExtra("TAGS", 0) == 10) {
					new Task().execute("count");
				}
			}

		};
		this.registerReceiver(myReceiver, filter);
	}

//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		// if (!su.getNetworkEnabled()) {
//		// // myDialog(getResources().getString(R.string.network_error));
//		// } else {
//		new Task().execute("restart");
//		// }
//
//	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public void popuWidow(int tager) {
		if (isshow) {
			isshow = false;
			// 加载PopupWindow的布局文件
			LayoutInflater layoutInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.punch_view, null);
			// 加载PopupWindow的媒介布局文件
			Button tishi_txt = (Button) view.findViewById(R.id.punch_tishi);
			TextView time_txt = (TextView) view.findViewById(R.id.punch_time);
			TextView city_txt = (TextView) view.findViewById(R.id.punch_city);
			TextView district_txt = (TextView) view
					.findViewById(R.id.punch_district);
			TextView punch_time_title = (TextView) view
					.findViewById(R.id.punch_time_title);
			LinearLayout lin_time = (LinearLayout) view
					.findViewById(R.id.punch_time_t);
			LinearLayout lin_city = (LinearLayout) view
					.findViewById(R.id.punch_city_t);
			if (tager == 0) {
				tishi_txt.setText(getResources().getString(R.string.Prompt));
				tishi_txt.setBackgroundResource(R.drawable.greenround2_03);
				time_txt.setText(tishi);
				lin_time.setVisibility(View.VISIBLE);
				punch_time_title.setVisibility(View.GONE);
				lin_city.setVisibility(View.GONE);
			} else if (tager == 1) {
				lin_time.setVisibility(View.VISIBLE);
				lin_city.setVisibility(View.VISIBLE);
				time_txt.setText(timeloc);
				city_txt.setText(city);
				district_txt.setText(district);
			} else {
				tishi_txt.setText(getResources().getString(
						R.string.Daka_failure));
				tishi_txt.setBackgroundResource(R.drawable.cuo_03);
				lin_time.setVisibility(View.VISIBLE);
				punch_time_title.setVisibility(View.GONE);
				lin_city.setVisibility(View.GONE);
				time_txt.setText(tishi);
			}
			tishi_txt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					isshow = true;
					Intent intent = new Intent(KqPageMain.this,MapActivity.class);
					intent.putExtra("Lng", lng);
					intent.putExtra("Lat", lat);
					intent.putExtra("GetType", GetType);
					intent.putExtra("Addr", getAddrStr);
					startActivity(intent);
				}
			});
			// 实例化PopupWindow中的组件
			popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			if (dialog2.isShowing()) {
				dialog2.dismiss();
			}
			popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		}
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				isshow = true;
			}
		});
	}

	private void iniOnCreate() {
		Configuration();
//		new Task().execute("restart");
	}

	public void Configuration() {
		StringData();
		empid = sp.getString("empid", "");
		year.setText(mYear + "-" + mMonth
				+ "-"+mDay+" "+getResources().getString(R.string.way) + mWay);
		time.setText(getTimeTypeHms());
//		day.setText(mDay);
//		way.setText(getResources().getString(R.string.way) + mWay);
	}
	public  String getTimeTypeHms() {
		SimpleDateFormat formatBuilder = new SimpleDateFormat("HH:mm:ss");
		return formatBuilder.format(new Date());
	}
	public void initialize() {
		left = (Button) this.findViewById(R.id.main_left);
		right = (Button) this.findViewById(R.id.main_right);
		year = (TextView) this.findViewById(R.id.main_year);
		//dk_btn = (Button) this.findViewById(R.id.dk_btn);
//		day = (TextView) this.findViewById(R.id.main_day);
//		way = (TextView) this.findViewById(R.id.main_way);
		time = (TextView) this.findViewById(R.id.main_time);
		// main_company = (LinearLayout) this.findViewById(R.id.main_company);
//		main_xinxigeshu = (TextView) this.findViewById(R.id.main_xinxigeshu);
//		main_huibao = (TextView) this.findViewById(R.id.main_huibao);
		// main_myreport = (LinearLayout) this.findViewById(R.id.main_myreport);
		// main_dakarecord = (LinearLayout) this
		// .findViewById(R.id.main_dakarecord);
		rel33 = (RelativeLayout) this.findViewById(R.id.rel33);
		left.setOnClickListener(new MyOnClickListener());
		right.setOnClickListener(new MyOnClickListener());
		//dk_btn.setOnClickListener(new MyOnClickListener());
//		main_xinxigeshu.setOnClickListener(new MyOnClickListener());
//		main_huibao.setOnClickListener(new MyOnClickListener());
		MyOnClickListener listener = new MyOnClickListener();
		rel33.setOnClickListener(listener);
		rel33.setOnTouchListener(listener);
		// main_myreport.setOnClickListener(new MyOnClickListener());
		// main_company.setOnClickListener(new MyOnClickListener());
		// main_dakarecord.setOnClickListener(new MyOnClickListener());
		// right.setVisibility(Button.GONE);
	}

	class MyOnClickListener implements OnClickListener, OnTouchListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_left:
				finish();
				break;
			//case R.id.dk_btn:
			case R.id.main_right:
				// Intent intent2 = new Intent("main");
				// intent2.putExtra("TAGS", 0);
				// sendBroadcast(intent2);
				Intent intentdaka = new Intent(KqPageMain.this,
						NowPunchWorkActivity.class);
				startActivity(intentdaka);
				break;
			
			// case R.id.main_company:
			// Intent intentcompany = new Intent(KqPageMain.this,
			// CompanyAnnouncementsActivity.class);
			// startActivity(intentcompany);
			// break;
//			case R.id.main_xinxigeshu:
//				Intent intent = new Intent(KqPageMain.this, NewsActivity.class);
//				intent.putExtra("an", gong);
//				intent.putExtra("report", hui);
//				startActivityForResult(intent, 1);
//				break;
//			case R.id.main_huibao:
//				Intent intent8 = new Intent(KqPageMain.this,
//						NowReportActivity.class);
//				startActivity(intent8);
//				break;
			// case R.id.main_myreport:
			// Intent intents = new Intent(KqPageMain.this,
			// MyReportActivity.class);
			// startActivity(intents);
			// break;
			// case R.id.main_dakarecord:
			// Intent intentdaka1 = new Intent(KqPageMain.this,
			// NowPunchWorkActivity.class);
			// startActivity(intentdaka1);
			// break;
			case R.id.rel33:
				// if (!ServiceUtils.isServiceRunning(this,
				// Constant.Locationservice)) {
				// myDialog(getResources().getString(R.string.servicesclosetips));
				// return;
				// }
				dialog2.show();
				running2 = true;
				threadtime = 0;
				LocationUtils lu = new LocationUtils();
				lu.GetLocationNow(KqPageMain.this, "12", "");
				new Thread() {

					@Override
					public void run() {
						super.run();
						try {
							while (running2) {
								sleep(1000);
								threadtime++;
								if (threadtime >= 20) {
									Intent intent2 = new Intent("pagemain");
									intent2.putExtra("TAGS", 5);
									sendBroadcast(intent2);
									break;
								}
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}.start();
				break;
			default:
				break;
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.rel33:
				if (event.getAction() == MotionEvent.ACTION_UP) {
					rel33.setBackgroundResource(R.drawable.dk_11);
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					rel33.setBackgroundResource(R.drawable.dk_12);
				}
				if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					rel33.setBackgroundResource(R.drawable.dk_11);
				}
				break;

			}
			return false;
		}

	}

	public void StringData() {
		c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = getResources().getString(R.string.way7);
		} else if ("2".equals(mWay)) {
			mWay = getResources().getString(R.string.way1);
		} else if ("3".equals(mWay)) {
			mWay = getResources().getString(R.string.way2);
		} else if ("4".equals(mWay)) {
			mWay = getResources().getString(R.string.way3);
		} else if ("5".equals(mWay)) {
			mWay = getResources().getString(R.string.way4);
		} else if ("6".equals(mWay)) {
			mWay = getResources().getString(R.string.way5);
		} else if ("7".equals(mWay)) {
			mWay = getResources().getString(R.string.way6);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running2 = false;
		running = false;
		thread = null;
		open = false;
		this.unregisterReceiver(myReceiver);
	}

	/**
	 * 异步线程
	 * 
	 * @author wyq
	 * 
	 */
	class Task extends AsyncTask<String, String, String> {
		// 开启另外一个线程执行任务
		@Override
		protected String doInBackground(String... params) {
			String exeParam = params[0];
			try {
				if (exeParam.equals("restart")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("EmployeeHandler");
					sc.setMethodname("EmployeeLogin");
					sc.addParameter("imei", imei);
					sc.addParameter("empnum", "13940399517");
					sc.addParameter("password", "123456");
					Log.i("登录接口",
							"imei=" + imei + "&empnum="
									+ sp.getString("name", "") + "&password="
									+ sp.getString("password", ""));
					srlogin = Network.postDataService(sc);
				} else if (exeParam.equals("count")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("EmployeeHandler");
					sc.setMethodname("GetNoReadCount");
					sc.addParameter("empid", "1");
					sr = Network.postDataService(sc);
				} else if (exeParam.equals("pagePunch")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("AttenceHandler");
					sc.setMethodname("CheckIn");
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.addParameter("lng", lng);
					sc.addParameter("lat", lat);
					sc.addParameter("wifimac", wifiaddress);
					sc.addParameter("loctype", GetType);
					srpun = Network.postDataService(sc);
				}
			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {
			if (result.equals("restart")) {
				if (srlogin.GetIsError()) {
					if (srlogin.getMessage().contains("|")) {
						String str0 = srlogin.getMessage().substring(0,
								srlogin.getMessage().lastIndexOf("|"));
						String str1 = srlogin.getMessage().substring(
								srlogin.getMessage().lastIndexOf("|") + 1);
						myDialog(str1 + "", str0);
					}
				} else {
					PersonalInformationMode loginModel = new PersonalInformationMode();
					try {
						loginModel = (PersonalInformationMode) JsonUtils
								.parseObject(srlogin.getData(),
										PersonalInformationMode.class);
						Editor edit;
						edit = sp.edit();
						edit.putString("empid", loginModel.getId());
						edit.putString("empnum", loginModel.getEmpnum());
						edit.putString("myname", loginModel.getName());
						edit.putString("head", loginModel.getHead());
						edit.putString("gender", loginModel.getGender());
						edit.putString("channel_id", loginModel.getChannelid());
						edit.putString("signintime", loginModel.getSignintime());
						edit.putString("signouttime",
								loginModel.getSignouttime());
						edit.putString("ontrack", loginModel.getOntrack());
						edit.putString("TAG", "YES");
						edit.putString("isboss", loginModel.getIsboss());
						edit.putString("isdephead", loginModel.getIsdephead());
						edit.putString("post", loginModel.getPost());
						edit.putString("isaskleaveaudit",
								loginModel.getIsaskleaveaudit());
						edit.putString("isasktravelaudit",
								loginModel.getIsasktravelaudit());
						edit.commit();
						new Task().execute("count");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}  else if (result.equals("pagePunch")) {
				if (dialog2.isShowing()) {
					dialog2.dismiss();
				}
				if (srpun.GetIsError()) {
					if (srpun.getMessage().contains("|")) {
						String str0 = srpun.getMessage().substring(0,
								srpun.getMessage().lastIndexOf("|"));
						String str1 = srpun.getMessage().substring(
								srpun.getMessage().lastIndexOf("|") + 1);
						myDialog(str1 + "", str0);
					} else {
						myDialog(srpun.getMessage());
					}
				} else {
					int tager = 0;
					if (srpun.getData().equals("null")) {
						tishi = srpun.getMessage();
						tager = 0;
					} else {
						tager = 1;
						try {
							punchStateModel = new PunchStateModel();
							punchStateModel = (PunchStateModel) JsonUtils
									.parseObject(srpun.getData(),
											PunchStateModel.class);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						timeloc = punchStateModel.getTime();
						punchStateModel.getProvince();
						city = punchStateModel.getCity();
						district = punchStateModel.getDistrict();
					}
					popuWidow(tager);
				}
			} else if (result.equals("ERROR")) {
				myDialog(getResources().getString(R.string.please_error));
			} else {
				myDialog(getResources().getString(R.string.no_thing));
			}
		}
	}

	public void myDialog(String msg) {
		View v = View.inflate(this, R.layout.dialog_alone, null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		dialog = new Dialog(this, R.style.dialog_style);
		dialog.setContentView(v);
		dialog.setCancelable(true);
		textView.setText(msg);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				running2 = true;
			}
		});
		if (dialog.isShowing()) {
			dialog.dismiss();
			running2 = true;
		} else {
			running2 = true;
		}
		if (running2) {
			dialog2.dismiss();
			dialog.show();
			running2 = false;
		}
	}

	Dialog dialog3;

	public void myDialog(String msg, final String str) {
		View v = View.inflate(this, R.layout.dialog_alone, null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		if (dialog3 != null) {
			if (dialog3.isShowing()) {
				dialog3.dismiss();
			}
		}
		dialog3 = new Dialog(this, R.style.dialog_style);
		dialog3.setContentView(v);
		dialog3.setCancelable(false);
		textView.setText(msg);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog3.dismiss();
			}
		});
		if (dialog3.isShowing()) {
			dialog3.dismiss();
		}
		if (open) {
			dialog3.show();
		}
	}
}
