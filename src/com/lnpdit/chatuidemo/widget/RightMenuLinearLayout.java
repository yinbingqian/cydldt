package com.lnpdit.chatuidemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.LinearLayout;
import android.widget.ListView;

/***
 * �Զ��岼���ļ�.
 * 
 * @author wangyuanshi
 * 
 */
public class RightMenuLinearLayout extends LinearLayout {
	private GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;

	private boolean isLock = false;// �����ƶ���.

	public OnScrollListener onScrollListener;// �Զ��廬���ӿ�

	private boolean b;// 9��touch��ʶ

	public RightMenuLinearLayout(Context context) {
		super(context);
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	public RightMenuLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(new MySimpleGesture());

	}

	/***
	 * �¼��ַ�
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		b = mGestureDetector.onTouchEvent(ev);// ��ȡ���Ʒ���ֵ.
		/***
		 * �ɿ�ʱ�ǵô������...
		 */
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			onScrollListener.doLoosen();
		}
		return super.dispatchTouchEvent(ev);
	}

	/***
	 * �¼�9�ش���
	 * 
	 * Ҫ��׻��ƣ�����ture�Ļ����Ǿ��ǽ���9�أ������Լ���ontouch. ����false�Ļ�����ô�ͻ����´���...
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		super.onInterceptTouchEvent(ev);
		return b;
	}

	/***
	 * �¼�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isLock = false;
		return super.onTouchEvent(event);
	}

	/***
	 * �Զ�������ִ��
	 * 
	 * @author zhangjia
	 * 
	 * 
	 */
	class MySimpleGesture extends SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			isLock = true;
			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (!isLock)
				onScrollListener.doScroll(distanceX);

			// ��ֱ����ˮƽ
			if (Math.abs(distanceY) > Math.abs(distanceX)) {
				return false;
			} else {
				return true;
			}

		}
	}

	/***
	 * �Զ���ӿ� ʵ�ֻ���...
	 * 
	 * @author zhangjia
	 * 
	 */
	public interface OnScrollListener {
		void doScroll(float distanceX);// ����...

		void doLoosen();// ��ָ�ɿ���ִ��...
	}

}
