/*****************************************************************************
 * VLCApplication.java
 *****************************************************************************
 * Copyright 漏 2010-2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
package org.videolan.libvlc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sytm.bean.CellInfoModel;
import com.sytm.common.Constant;
import com.sytm.crash.CrashHandler;
import com.sytm.db.CellInfoDBManager;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.CellInfoUtil;
import com.sytm.util.StringUtils;
import com.sytm.util.SystemUtils;
import com.sytm.util.WifiUtils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class VLCApplication extends Application {
    public final static String TAG = "VLC/VLCApplication";
    private static VLCApplication instance;
	// 授权是否校验通过
	public boolean m_bKeyRight = true;
	public LocationClient mLocationClient = null;
	// public GeofenceClient mGeofenceClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private ServiceResult sr = new ServiceResult();
	private ServiceContent sc = new ServiceContent();
	public String tager = "1";
//	public boolean autoOpenWifi = false;
//	public boolean autoOpenGPS = false;
	public String fData = "";
	TelephonyManager tm;
	public String Lng = "0.0", Lat = "0.0", GetType = "GPS",imei="",getAddrStr="";
	public String updatemessage = "";
	public SystemUtils su=null;
	List<NeighboringCellInfo> infos = null;
//	int mcc, mnc;
	CellInfoModel infoModel;
    public final static String SLEEP_INTENT = "org.videolan.vlc.SleepIntent";

    @Override
    public void onCreate() {
        mLocationClient = new LocationClient(getApplicationContext()); 
		mLocationClient.registerLocationListener(myListener);
		/**
		 * ——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，为申请的百度Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		mLocationClient.setAK(Constant.BaiDu_Key1);
		SDKInitializer.initialize(getApplicationContext());  
		super.onCreate();
		Log.i("BaseApplication", "onCreate");
//		FrontiaApplication.initFrontiaApplication(getApplicationContext());
		tm = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

//		// 全局Crash异常捕获
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());
		instance = this;
		// 图片加载器
		initImageLoader(getApplicationContext());
        // Are we using advanced debugging - locale?
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if(p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if(p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if(p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if(p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if(p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        instance = this;

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
        Log.w(TAG, "System is running low on memory");

    }

    /**
     * @return the main context of the Application
     */
    public static Context getAppContext()
    {
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources()
    {
        if(instance == null) return null;
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
			tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			if (tm == null) {
				return;
			}
			imei = tm.getDeviceId();
			if (imei == null || imei.equals("")) {
				imei = "0";

			}
			// 成功在线定位的时候
			if (location.getLocType() == 61 || location.getLocType() == 65 || location.getLocType() == 161||location.getLocType() == 66 || location.getLocType() == 68) {
				sc.reset();
				Lng = StringUtils.convertScienceNum(location.getLongitude());
				Lat = StringUtils.convertScienceNum(location.getLatitude());
				getAddrStr = location.getAddrStr();
				sc.addParameter("imei", imei);
				sc.addParameter("longitude", Lng);
				sc.addParameter("latitude", Lat);
				sc.setClassname("LocationHanlder");
				sc.addParameter("data", fData);
				su= new SystemUtils(getApplicationContext());
				if (tager.equals("1")) {
					if (su.getNetworkEnabled()) {
						infoModel = new CellInfoModel();
						infoModel = CellInfoUtil.CellInfo(getApplicationContext());
						if (infoModel!=null) {
							new Task().execute("cellinfo");
						}
					}
				}
				if (tager.equals("3")) {
					sc.setMethodname("CollectLocation2");
				}else {
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
					WifiUtils wifi = new WifiUtils(VLCApplication.this.getApplicationContext());
					if (wifi != null) {
					if (wifi.wifiEnabled()) {
						String wifibssid = wifi.getBSSID();
						intent2.putExtra("wifimac", wifibssid);
					}else {
						intent2.putExtra("wifimac", " ");
					}
					}
					sendBroadcast(intent2);
					su=null;
					return;
				}else if (tager.equals("12")) {
					Intent intent2 = new Intent("pagemain");
					intent2.putExtra("TAGS", 3);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					intent2.putExtra("getAddrStr", getAddrStr);
					WifiUtils wifi = new WifiUtils(VLCApplication.this.getApplicationContext());
					if (wifi != null) {
						if (wifi.wifiEnabled()) {
							String wifibssid = wifi.getBSSID();
							intent2.putExtra("wifimac", wifibssid);
						}else {
							intent2.putExtra("wifimac", " ");
						}
					}
					sendBroadcast(intent2);
					su=null;
					return;
				}else if (tager.equals("10")) {
					Intent intent2 = new Intent("report");
					intent2.putExtra("TAGS", 1);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					sendBroadcast(intent2);
					su=null;
					return;
				}else if (tager.equals("11")) {
					Intent intent2 = new Intent("upload");
					intent2.putExtra("TAGS", 1);
					intent2.putExtra("Lng", Lng);
					intent2.putExtra("Lat", Lat);
					intent2.putExtra("GetType", GetType);
					sendBroadcast(intent2);
					su=null;
					return;
				}
				// 发送数据
				new Task().execute("SEND");
			}else {
				if (tager.equals("1")) {
//					// 关闭wifi
//					WifiUtils wifi = new WifiUtils(BaseApplication.this);
//					if (wifi.wifiEnabled() && autoOpenWifi) {
//						wifi.closeNetCard();
//						autoOpenWifi=false;
//					}
					infoModel = new CellInfoModel();
					infoModel = CellInfoUtil.CellInfo(getApplicationContext());
					if (infoModel!=null) {
					CellInfoDBManager db = new CellInfoDBManager(getApplicationContext());
					SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					infoModel.setReceivetime(formatBuilder.format(new Date()));
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
				}else if (tager.equals("11")) {
					Intent intent2 = new Intent("upload");
					intent2.putExtra("TAGS", 0);
					sendBroadcast(intent2);
				}
			}
			if (mLocationClient != null) {
				mLocationClient.stop();
			}
			su=null;
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
					Log.i("tmtest", "imei:"+imei+",Lng:"+Lng+",Lat:"+Lat+",fData:"+fData+",getstyle:"+tager+",GetType:"+GetType);
				} else if (exeParam.equals("cellinfo")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MtbsHandler");
					sc.setMethodname("Collection");
					sc.addParameter("longitude", Lng);
					sc.addParameter("latitude", Lat);
					sc.addParameter("mcc", String.valueOf(infoModel.getMcc()));
					sc.addParameter("mnc", String.valueOf(infoModel.getMnc()));
					if (!infoModel.getCid1().equals("")) {
						sc.addParameter("lac1",infoModel.getLac1());
						sc.addParameter("cid1",infoModel.getCid1());
						sc.addParameter("bass1",infoModel.getBass1());
						}
						if (!infoModel.getCid2().equals("")) {		
							sc.addParameter("lac2",infoModel.getLac2());						
							sc.addParameter("cid2",infoModel.getCid2());					
							sc.addParameter("bass2",infoModel.getBass2());					
						} 
	                    if (!infoModel.getCid3().equals("")) {
	                    	sc.addParameter("lac3",infoModel.getLac3());
	    					sc.addParameter("cid3",infoModel.getCid3());
	    					sc.addParameter("bass3",infoModel.getBass3());
						}
	                    if(!infoModel.getMacaddress1().equals("")){
	                    	sc.addParameter("ssid1",infoModel.getSsid1());
	    					sc.addParameter("macaddress1",infoModel.getMacaddress1());
	    					sc.addParameter("capabilities1",infoModel.getCapabilities1());
	    					sc.addParameter("frequency1",infoModel.getFrequency1());
	    					sc.addParameter("level1",infoModel.getLevel1());
	                    }
	                    if(!infoModel.getMacaddress2().equals(""))
	                    {
	                    	sc.addParameter("ssid2",infoModel.getSsid2());
	    					sc.addParameter("macaddress2",infoModel.getMacaddress2());
	    					sc.addParameter("capabilities2",infoModel.getCapabilities2());
	    					sc.addParameter("frequency2",infoModel.getFrequency2());
	    					sc.addParameter("level2",infoModel.getLevel2());
	                    }
	                    if(!infoModel.getMacaddress3().equals(""))
	                    {
	                    	sc.addParameter("ssid3",infoModel.getSsid3());
	    					sc.addParameter("macaddress3",infoModel.getMacaddress3());
	    					sc.addParameter("capabilities3",infoModel.getCapabilities3());
	    					sc.addParameter("frequency3",infoModel.getFrequency3());
	    					sc.addParameter("level3",infoModel.getLevel3());
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
//			WifiUtils wifi = new WifiUtils(BaseApplication.this);
			if (result.equals("SEND")) {
//				openStatus1 = true;
//				if (openStatus1&&openStatus2) {
//					if (wifi.wifiEnabled() && autoOpenWifi) {
//						wifi.closeNetCard();
//						autoOpenWifi=false;
//					}
//				}
				if (sr.GetIsError()) {
//					Log.e(TAG, "服务端处理失败!" + sr.getMessage());
				} else {
//					Log.i(TAG, "传输成功！");
				}
			} else if (result.equals("cellinfo")) {
//				openStatus2 = true;
//				if (openStatus1&&openStatus2) {
//					if (wifi.wifiEnabled() && autoOpenWifi) {
//						wifi.closeNetCard();
//					}
//				}
				if (sr.GetIsError()) {
//					Log.e(TAG, "服务端处理失败!" + sr.getMessage());
				} else {
//					Log.i(TAG, "离线传输成功！");
				}
			} else if (result.equals("ERROR")) {
//				Log.e(TAG, "网络错误！发送失败！");
			} else {
//				Log.w(TAG, "未知方式！");
			}
		}
	}
}
