package com.sytm.widget;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
/***
 * 震动
 * @author wyq
 *
 */
public class VibratorWidget {
	public static void Vibrate(final Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
	public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
}
