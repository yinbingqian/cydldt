package com.sytm.application;

import org.videolan.libvlc.VLCApplication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationUtils {
	// 客户端定位
	private LocationClient mLocClient;
	// 是否打开GPS
	private boolean mGpsOpen = false;
	// 定位坐标类型
	private String coolType = "bd09ll";
	// 设置发起定位请求的间隔(ms) 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
	private Integer timeSpan = 500;
	// 是否需要地址信息
	private boolean mIsAddrInfo = true;
	// 日志tag
	public static String TAG = "LocationUtils";

	public void GetLocationNow(Context context, String tager, String data) {
		mLocClient = ((VLCApplication) context).mLocationClient;
		((VLCApplication) context).fData = data;
		((VLCApplication) context).tager = tager;
		if (mLocClient == null || !mLocClient.isStarted()) {
			setLocationOption();
			mLocClient.start();
		Log.i(TAG, "mLocClient.start()");
			// mLocClient.requestLocation();//在线
		} else {
			//setLocationOption();
			mLocClient.requestLocation();// 在线
			Log.i(TAG, "mLocClient.requestLocation()");
		}
		mLocClient.stop();

	}

	// 设置定位相关参数
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(mGpsOpen); // 打开gps
		option.setCoorType(coolType); // 设置坐标类型
		option.setScanSpan(timeSpan); // 定时定位
		option.setPoiExtraInfo(mIsAddrInfo);// 需要地址信息
		// option.set
		if (mIsAddrInfo) {
			option.setAddrType("all");
		}
		option.setPoiNumber(0);
		option.disableCache(false);
		mLocClient.setLocOption(option);
	}

}
