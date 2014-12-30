/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lnpdit.chatuidemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.OnNotificationClickListener;
import com.lnpdit.chatuidemo.activity.ChatActivity;
import com.lnpdit.chatuidemo.activity.MainActivity;
import com.lnpdit.chatuidemo.db.DbOpenHelper;
import com.lnpdit.chatuidemo.db.UserDao;
import com.lnpdit.chatuidemo.domain.User;
import com.lnpdit.chatuidemo.receiver.VoiceCallReceiver;
import com.lnpdit.chatuidemo.utils.PreferenceUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sytm.bean.CellInfoModel;
import com.sytm.common.Constant;
import com.sytm.db.CellInfoDBManager;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.CellInfoUtil;
import com.sytm.util.StringUtils;
import com.sytm.util.SystemUtils;
import com.sytm.util.WifiUtils;

public class CydlApplication extends Application {

	public static Context applicationContext;
	private static CydlApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	private String userName = null;
	// login password
	private static final String PREF_PWD = "pwd";
	private String password = null;
	private Map<String, User> contactList;
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";

	// 授权是否校验通过
	public boolean m_bKeyRight = true;
	public LocationClient mLocationClient = null;
	// public GeofenceClient mGeofenceClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private ServiceResult sr = new ServiceResult();
	private ServiceContent sc = new ServiceContent();
	public String tager = "1";
	// public boolean autoOpenWifi = false;
	// public boolean autoOpenGPS = false;
	public String fData = "";
	TelephonyManager tm;
	public String Lng = "0.0", Lat = "0.0", GetType = "GPS", imei = "",
			getAddrStr = "";
	public String updatemessage = "";
	public SystemUtils su = null;
	List<NeighboringCellInfo> infos = null;
	// int mcc, mnc;
	CellInfoModel infoModel;

	@Override
	public void onCreate() {
		super.onCreate();

		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		/**
		 * ——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，为申请的百度Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		mLocationClient.setAK(Constant.BaiDu_Key1);
		SDKInitializer.initialize(getApplicationContext());
		Log.i("BaseApplication", "onCreate");
		// FrontiaApplication.initFrontiaApplication(getApplicationContext());
		tm = (TelephonyManager) getApplicationContext().getSystemService(
				Context.TELEPHONY_SERVICE);

		// // 全局Crash异常捕获
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		// 图片加载器
		initImageLoader(getApplicationContext());
		// Are we using advanced debugging - locale?
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String p = pref.getString("set_locale", "");
		if (p != null && !p.equals("")) {
			Locale locale;
			// workaround due to region code
			if (p.equals("zh-TW")) {
				locale = Locale.TRADITIONAL_CHINESE;
			} else if (p.startsWith("zh")) {
				locale = Locale.CHINA;
			} else if (p.equals("pt-BR")) {
				locale = new Locale("pt", "BR");
			} else if (p.equals("bn-IN") || p.startsWith("bn")) {
				locale = new Locale("bn", "IN");
			} else {
				/**
				 * Avoid a crash of java.lang.AssertionError: couldn't
				 * initialize LocaleData for locale if the user enters
				 * nonsensical region codes.
				 */
				if (p.contains("-"))
					p = p.substring(0, p.indexOf('-'));
				locale = new Locale(p);
			}
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}

		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
		if (processAppName == null || processAppName.equals("")) {
			// workaround for baidu location sdk
			// 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
			// 创建新的进程。
			// 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
			// processName，
			// 则此application::onCreate 是被service 调用的，直接返回
			return;
		}

		applicationContext = this;
		instance = this;
		// 初始化环信SDK,一定要先调用init()
		EMChat.getInstance().init(applicationContext);
		EMChat.getInstance().setDebugMode(true);
		Log.d("EMChat Demo", "initialize EMChat SDK");
		// debugmode设为true后，就能看到sdk打印的log了

		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
		options.setUseRoster(true);
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 设置收到消息是否有新消息通知，默认为true
		options.setNotifyBySoundAndVibrate(PreferenceUtils.getInstance(
				applicationContext).getSettingMsgNotification());
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(PreferenceUtils
				.getInstance(applicationContext).getSettingMsgSound());
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(PreferenceUtils.getInstance(
				applicationContext).getSettingMsgVibrate());
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(PreferenceUtils.getInstance(applicationContext)
				.getSettingMsgSpeaker());
		// 设置notification消息点击时，跳转的intent为自定义的intent
		options.setOnNotificationClickListener(new OnNotificationClickListener() {

			@Override
			public Intent onNotificationClick(EMMessage message) {
				Intent intent = new Intent(applicationContext,
						ChatActivity.class);
				ChatType chatType = message.getChatType();
				if (chatType == ChatType.Chat) { // 单聊信息
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
				} else { // 群聊信息
							// message.getTo()为群聊id
					intent.putExtra("groupId", message.getTo());
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				}
				return intent;
			}
		});
		// 设置一个connectionlistener监听账户重复登陆
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		// // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
		// options.setNotifyText(new OnMessageNotifyListener() {
		//
		// @Override
		// public String onNewMessageNotify(EMMessage message) {
		// // 可以根据message的类型提示不同文字(可参考微信或qq)，demo简单的覆盖了原来的提示
		// return "你的好基友" + message.getFrom() + "发来了一条消息哦";
		// }
		//
		// @Override
		// public String onLatestMessageNotify(EMMessage message, int
		// fromUsersNum, int messageNum) {
		// return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
		// }
		//
		// @Override
		// public String onSetNotificationTitle(EMMessage message) {
		// //修改标题
		// return "环信notification";
		// }
		//
		//
		// });

		// 注册一个语言电话的广播接收者
		IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance()
				.getIncomingVoiceCallBroadcastAction());
		registerReceiver(new VoiceCallReceiver(), callFilter);

	}

	public static CydlApplication getInstance() {
		return instance;
	}

	// List<String> list = new ArrayList<String>();
	// list.add("1406713081205");
	// options.setReceiveNotNoifyGroup(list);
	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		if (getUserName() != null && contactList == null) {
			UserDao dao = new UserDao(applicationContext);
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = dao.getContactList();
		}
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		this.contactList = contactList;
	}

	public void setStrangerList(Map<String, User> List) {

	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		if (userName == null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			userName = preferences.getString(PREF_USERNAME, null);
		}
		return userName;
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		if (password == null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			password = preferences.getString(PREF_PWD, null);
		}
		return password;
	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		if (username != null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = preferences.edit();
			if (editor.putString(PREF_USERNAME, username).commit()) {
				userName = username;
			}
		}
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_PWD, pwd).commit()) {
			password = pwd;
		}
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout() {
		// 先调用sdk logout，在清理app中自己的数据
		EMChatManager.getInstance().logout();
		DbOpenHelper.getInstance(applicationContext).closeDB();
		// reset password to null
		setPassword(null);
		setContactList(null);

	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName,
									PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

	class MyConnectionListener implements ConnectionListener {
		@Override
		public void onReConnecting() {
		}

		@Override
		public void onReConnected() {
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				Intent intent = new Intent(applicationContext,
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("conflict", true);
				startActivity(intent);
			}

		}

		@Override
		public void onConnecting(String progress) {

		}

		@Override
		public void onConnected() {
		}
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging()
				.threadPoolSize(4).build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * Called when the overall system is running low on memory
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();

	}

	/**
	 * @return the main resources from the Application
	 */
	public static Resources getAppResources() {
		if (instance == null)
			return null;
		return instance.getResources();
	}

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (mLocationClient != null) {
				if (mLocationClient.isStarted()) {
					mLocationClient.stop();
				}
			}
			if (location == null) {
				return;
			}
			Lng = "0.0";
			Lat = "0.0";
			GetType = "";
			tm = (TelephonyManager) getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);
			if (tm == null) {
				return;
			}
			imei = tm.getDeviceId();
			if (imei == null || imei.equals("")) {
				imei = "0";

			}
			// 成功在线定位的时候
			if (location.getLocType() == 61 || location.getLocType() == 65
					|| location.getLocType() == 161
					|| location.getLocType() == 66
					|| location.getLocType() == 68) {
				sc.reset();
				Lng = StringUtils.convertScienceNum(location.getLongitude());
				Lat = StringUtils.convertScienceNum(location.getLatitude());
				getAddrStr = location.getAddrStr();
				sc.addParameter("imei", imei);
				sc.addParameter("longitude", Lng);
				sc.addParameter("latitude", Lat);
				sc.setClassname("LocationHanlder");
				sc.addParameter("data", fData);
				su = new SystemUtils(getApplicationContext());
				if (tager.equals("1")) {
					if (su.getNetworkEnabled()) {
						infoModel = new CellInfoModel();
						infoModel = CellInfoUtil
								.CellInfo(getApplicationContext());
						if (infoModel != null) {
							new Task().execute("cellinfo");
						}
					}
				}
				if (tager.equals("3")) {
					sc.setMethodname("CollectLocation2");
				} else {
					sc.setMethodname("CollectLocation");
				}
				sc.addParameter("getstyle", tager);
				if (location.getLocType() == BDLocation.TypeGpsLocation) {
					sc.addParameter("loctype", "GPS");
					GetType = "GPS";
				} else {
					sc.addParameter("loctype", "NetWork");
					GetType = "NetWork";
				}
				if (tager.equals("2")) {
					Intent intent2 = new Intent("leftmenu");
					intent2.putExtra("TAGS", 1);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					WifiUtils wifi = new WifiUtils(
							CydlApplication.this.getApplicationContext());
					if (wifi != null) {
						if (wifi.wifiEnabled()) {
							String wifibssid = wifi.getBSSID();
							intent2.putExtra("wifimac", wifibssid);
						} else {
							intent2.putExtra("wifimac", " ");
						}
					}
					sendBroadcast(intent2);
					su = null;
					return;
				} else if (tager.equals("12")) {
					Intent intent2 = new Intent("pagemain");
					intent2.putExtra("TAGS", 3);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					intent2.putExtra("getAddrStr", getAddrStr);
					WifiUtils wifi = new WifiUtils(
							CydlApplication.this.getApplicationContext());
					if (wifi != null) {
						if (wifi.wifiEnabled()) {
							String wifibssid = wifi.getBSSID();
							intent2.putExtra("wifimac", wifibssid);
						} else {
							intent2.putExtra("wifimac", " ");
						}
					}
					sendBroadcast(intent2);
					su = null;
					return;
				} else if (tager.equals("10")) {
					Intent intent2 = new Intent("report");
					intent2.putExtra("TAGS", 1);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					sendBroadcast(intent2);
					su = null;
					return;
				} else if (tager.equals("11")) {
					Intent intent2 = new Intent("upload");
					intent2.putExtra("TAGS", 1);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					sendBroadcast(intent2);
					su = null;
					return;
				}else if (tager.equals("20")) {
					Intent intent2 = new Intent("pagemain");
					intent2.putExtra("TAGS", 20);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					intent2.putExtra("getAddrStr", location.getCity());
					sendBroadcast(intent2);
					Log.d("getAddrStr", "9999");
					su=null;
					return;
					
					
				}
				// 发送数据
				new Task().execute("SEND");
			} else {
				if (tager.equals("1")) {
					// // 关闭wifi
					// WifiUtils wifi = new WifiUtils(BaseApplication.this);
					// if (wifi.wifiEnabled() && autoOpenWifi) {
					// wifi.closeNetCard();
					// autoOpenWifi=false;
					// }
					infoModel = new CellInfoModel();
					infoModel = CellInfoUtil.CellInfo(getApplicationContext());
					if (infoModel != null) {
						CellInfoDBManager db = new CellInfoDBManager(
								getApplicationContext());
						SimpleDateFormat formatBuilder = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						infoModel.setReceivetime(formatBuilder
								.format(new Date()));
						db.addItem(infoModel, "cellinfo");
						db.closeDB();
					}
				} else if (tager.equals("2")) {
					Intent intent2 = new Intent("leftmenu");
					intent2.putExtra("TAGS", 0);
					sendBroadcast(intent2);
				} else if (tager.equals("12")) {
					Intent intent2 = new Intent("pagemain");
					intent2.putExtra("TAGS", 4);
					sendBroadcast(intent2);
				} else if (tager.equals("10")) {
					Intent intent2 = new Intent("report");
					intent2.putExtra("TAGS", 0);
					sendBroadcast(intent2);
				} else if (tager.equals("11")) {
					Intent intent2 = new Intent("upload");
					intent2.putExtra("TAGS", 0);
					sendBroadcast(intent2);
				}
			}
			if (mLocationClient != null) {
				mLocationClient.stop();
			}
			su = null;
		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
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
				if (exeParam.equals("SEND")) {
					sr = Network.postDataService(sc);
					Log.i("tmtest", "imei:" + imei + ",Lng:" + Lng + ",Lat:"
							+ Lat + ",fData:" + fData + ",getstyle:" + tager
							+ ",GetType:" + GetType);
				} else if (exeParam.equals("cellinfo")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MtbsHandler");
					sc.setMethodname("Collection");
					sc.addParameter("longitude", Lng);
					sc.addParameter("latitude", Lat);
					sc.addParameter("mcc", String.valueOf(infoModel.getMcc()));
					sc.addParameter("mnc", String.valueOf(infoModel.getMnc()));
					if (!infoModel.getCid1().equals("")) {
						sc.addParameter("lac1", infoModel.getLac1());
						sc.addParameter("cid1", infoModel.getCid1());
						sc.addParameter("bass1", infoModel.getBass1());
					}
					if (!infoModel.getCid2().equals("")) {
						sc.addParameter("lac2", infoModel.getLac2());
						sc.addParameter("cid2", infoModel.getCid2());
						sc.addParameter("bass2", infoModel.getBass2());
					}
					if (!infoModel.getCid3().equals("")) {
						sc.addParameter("lac3", infoModel.getLac3());
						sc.addParameter("cid3", infoModel.getCid3());
						sc.addParameter("bass3", infoModel.getBass3());
					}
					if (!infoModel.getMacaddress1().equals("")) {
						sc.addParameter("ssid1", infoModel.getSsid1());
						sc.addParameter("macaddress1",
								infoModel.getMacaddress1());
						sc.addParameter("capabilities1",
								infoModel.getCapabilities1());
						sc.addParameter("frequency1", infoModel.getFrequency1());
						sc.addParameter("level1", infoModel.getLevel1());
					}
					if (!infoModel.getMacaddress2().equals("")) {
						sc.addParameter("ssid2", infoModel.getSsid2());
						sc.addParameter("macaddress2",
								infoModel.getMacaddress2());
						sc.addParameter("capabilities2",
								infoModel.getCapabilities2());
						sc.addParameter("frequency2", infoModel.getFrequency2());
						sc.addParameter("level2", infoModel.getLevel2());
					}
					if (!infoModel.getMacaddress3().equals("")) {
						sc.addParameter("ssid3", infoModel.getSsid3());
						sc.addParameter("macaddress3",
								infoModel.getMacaddress3());
						sc.addParameter("capabilities3",
								infoModel.getCapabilities3());
						sc.addParameter("frequency3", infoModel.getFrequency3());
						sc.addParameter("level3", infoModel.getLevel3());
					}
					sr = Network.postDataService(Constant.CellInfo_Url, sc);
				}

			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {

			// 关闭wifi
			// WifiUtils wifi = new WifiUtils(BaseApplication.this);
			if (result.equals("SEND")) {
				// openStatus1 = true;
				// if (openStatus1&&openStatus2) {
				// if (wifi.wifiEnabled() && autoOpenWifi) {
				// wifi.closeNetCard();
				// autoOpenWifi=false;
				// }
				// }
				if (sr.GetIsError()) {
					// Log.e(TAG, "服务端处理失败!" + sr.getMessage());
				} else {
					// Log.i(TAG, "传输成功！");
				}
			} else if (result.equals("cellinfo")) {
				// openStatus2 = true;
				// if (openStatus1&&openStatus2) {
				// if (wifi.wifiEnabled() && autoOpenWifi) {
				// wifi.closeNetCard();
				// }
				// }
				if (sr.GetIsError()) {
					// Log.e(TAG, "服务端处理失败!" + sr.getMessage());
				} else {
					// Log.i(TAG, "离线传输成功！");
				}
			} else if (result.equals("ERROR")) {
				// Log.e(TAG, "网络错误！发送失败！");
			} else {
				// Log.w(TAG, "未知方式！");
			}
		}
	}
}
