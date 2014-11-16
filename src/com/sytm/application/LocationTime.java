package com.sytm.application;

import java.util.Date;

import org.videolan.libvlc.VLCApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.PowerManager;
import android.util.Log;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sytm.bean.CellInfoModel;
import com.sytm.db.CellInfoDBManager;
import com.sytm.util.CellInfoUtil;
import com.sytm.util.DateTimeUtils;
import com.sytm.util.SystemUtils;

public class LocationTime {
	// 客户端定位
		private LocationClient mLocClient;
		// 日志tag
		public String TAG = "LocationReceiver";
		int mcc, mnc;
		CellInfoDBManager db;
		SystemUtils su;
		PowerManager.WakeLock wakeLock = null;
		Context contex ;
    public void locatetime(Context context){
    	this.contex = context;
    	Date date = new Date(System.currentTimeMillis());
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
				"TMMTC", Context.MODE_PRIVATE);
		long fortime = sp.getLong("locationlasttime", 0);
		Log.d("tmtest", "当前时间：" + date.getTime());
			long time = date.getTime() - fortime;
			Log.d("tmtest", "之前时间：" + fortime + "差时间：" + time);
			if (Math.abs(time) >= 29.5*1000) {
				Editor edit2 = sp.edit();
				edit2.putLong("locationlasttime", date.getTime());
				edit2.commit();
				PrivacyProtect pp = new PrivacyProtect();
				if (pp.atFreeTime(context) || !pp.isOnTrack(context)) {
					pp = null;
				}else {
							su = new SystemUtils(contex.getApplicationContext());
							mLocClient = ((VLCApplication) contex.getApplicationContext()).mLocationClient;
							((VLCApplication) contex.getApplicationContext()).tager = "1";
							if (su.getNetworkEnabled()) {
								Log.d("tmtest","----在线定位请求！"+ DateTimeUtils.getDate("yyyy-MM-dd HH:mm:ss"));
								if (mLocClient == null || !mLocClient.isStarted()) {
									Log.d(TAG, "----在线定位请求！mLocClient.start();");
									setLocationOption();
									mLocClient.start();
								} else {
									Log.d(TAG, "----在线定位请求！mLocClient.requestLocation();");
									setLocationOption();
									mLocClient.requestLocation();// 在线
								}
							} else {
								db = new CellInfoDBManager(contex.getApplicationContext());
								Log.i("tmtest", "离线定位存储");
								if (db.query("cellinfo").size() < 30) {
									//Log.d(TAG,"----离线定位请求！"+ DateTimeUtils.getDate("yyyy-MM-dd HH:mm:ss"));
									CellInfoModel infoModel = new CellInfoModel();
									infoModel = CellInfoUtil.CellInfo(contex);
									if (infoModel!=null) {
										infoModel.setReceivetime(DateTimeUtils.getDate("yyyy-MM-dd HH:mm:ss"));
										db.addItem(infoModel, "cellinfo");
									}
//									String jsonStr = "";
//									jsonStr = JsonUtils.toJSON(db.query("cellinfo"));
//									Log.d("tmtest", "离线基站:" + jsonStr);
//									infoModel = null;
//									jsonStr = null;
								} else {
//									Log.d("tmtest","----离线定位请求！数据库已满"+ DateTimeUtils.getDate("yyyy-MM-dd HH:mm:ss"));
								}
								db.closeDB();
								db = null;
							}
//						}
//					}, 20000);
					
				pp = null;
				}
			}
    }
    
 // 设置定位相关参数
 	private void setLocationOption() {
 		LocationClientOption option = new LocationClientOption();
 		option.setOpenGps(true);
 		option.setAddrType("all");// 返回的定位结果包含地址信息
 		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
 		option.setScanSpan(500);// 设置发起定位请求的间隔时间为5000ms
 		option.disableCache(true);// 禁止启用缓存定位
 		option.setPoiNumber(0); // 最多返回POI个数
 		option.setPoiDistance(10); // poi查询距离
 		option.setPoiExtraInfo(false); // 是否需要POI的电话和地址等详细信息
 		mLocClient.setLocOption(option);
 	}

}
