package com.sytm.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;
/**
 * 自定义toast时间
 * @author Administrator
 *
 */
public class CustomToast { 
	public static final int LENGTH_MAX = -1; 
	private boolean mCanceled = true;
	private Handler mHandler; 
	private Context mContext; 
	private Toast mToast; 

	public CustomToast(Context context) { 
		this(context,new Handler()); 
	} 


	@SuppressLint("ShowToast")
	public CustomToast(Context context,Handler h) { 
		mContext = context; 
		mHandler = h; 
		mToast = Toast.makeText(mContext,"",Toast.LENGTH_LONG); 
		mToast.setGravity(Gravity.BOTTOM, 0, 0); 
	} 

	public void show(int resId,int duration) { 
		mToast.setText(resId); 
		if(duration != LENGTH_MAX) { 
			mToast.setDuration(duration); 
			mToast.show(); 
		 } else if(mCanceled) { 
			 mToast.setDuration(Toast.LENGTH_LONG);
			 mCanceled = false;
			 showUntilCancel(); 
		 } 
	}
	
	/**
	 * @param text 要显示的内容
	 * @param duration 显示的时间长
	 * 根据LENGTH_MAX进行判断
	 * 如果不匹配，进行系统显示
	 * 如果匹配，永久显示，直到调用hide()
	 */
	public void show(final String text,int duration) { 
		/*final Toast toast;
		final Timer timer1=new Timer();   //必须实例化，不然空指针异常

		//<初始化不能删！删了空指针异常~>
		toast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 0);

		Handler handler=new Handler(Looper.getMainLooper());       	
		         handler.post(new Runnable() {						
				@Override
				public void run() {
					toast=Toast.makeText(mContext, text, Toast.LENGTH_LONG);					
					toast.setGravity(Gravity.TOP, 0, 0);
					timer1 = new Timer();
					timer1.schedule(new RemindTask(toast), 0,duration);					   	    	 	}					
			});  */
		} 

	/**
	 * 隐藏Toast
	 */
	public void hide(){
		mToast.cancel();
		mCanceled = true;
	}
	
	public boolean isShowing() {
		return !mCanceled;
	}
	
	private void showUntilCancel() { 
		if(mCanceled) 
			return; 
		mToast.show();
		mHandler.postDelayed(new Runnable() {
			public void run() { 
				showUntilCancel(); 
			}
		},3000); 
	} 
} 