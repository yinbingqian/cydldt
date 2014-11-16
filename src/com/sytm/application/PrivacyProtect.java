package com.sytm.application;

import java.util.Date;

import com.sytm.util.DateTimeUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/***
 * 隐身保护
 * 
 * @author wyq
 * 
 */
public class PrivacyProtect {
	private String TAG = "PrivacyProtect";
	@SuppressWarnings("deprecation")
	public boolean atFreeTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences("TMMTC",
				Context.MODE_PRIVATE);	
		String intime = sp.getString("signintime", "");
		String outtime = sp.getString("signouttime", "");
		if (intime.length() > 0 && outtime.length() > 0) {
			try {
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				Date indate = new Date(intime);
				Date outdate = new Date(outtime);
				int inhouer = Integer.parseInt(DateTimeUtils.getHouer(indate)
						+ DateTimeUtils.getMinutes(indate));
				int outhouer = Integer.parseInt(DateTimeUtils.getHouer(outdate)
						+ DateTimeUtils.getMinutes(outdate));
				int houer = Integer.parseInt(DateTimeUtils.getHouer(curDate)
						+ DateTimeUtils.getMinutes(curDate));
				Log.d(TAG, "----inhouer:" + inhouer + ",outhouer:" + outhouer
						+ ",houer:" + houer);
				if (houer < inhouer || houer > outhouer) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return false;
			}
		} else {
			return false;

		}

	}
   /*
    * 是否跟踪定位
    */
	public boolean isOnTrack(Context context) {
		SharedPreferences sp = context.getSharedPreferences("TMMTC",
				Context.MODE_PRIVATE);		
		String ontrack = sp.getString("ontrack", "1");
		Log.d(TAG, "ontrack::"+ontrack);
		if (ontrack.length() > 0) {
			if (ontrack.equals("1")) {				
				return true;
			} else {
			
				
				return false;
			}
		} else {		
			return true;
		}

	}

}
