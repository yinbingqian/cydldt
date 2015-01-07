package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lnpdit.stategrid.informatization.adapter.ImageAndTextNewsList;
import lnpdit.stategrid.informatization.adapter.ImageAndTextNewsListAdapter;
import lnpdit.stategrid.informatization.adapter.NewsDeptAdapter;
import lnpdit.stategrid.informatization.data.MessengerService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.lnpdit.chatuidemo.widget.RightMenuLinearLayout;
import com.lnpdit.chatuidemo.widget.RightMenuLinearLayout.OnScrollListener;

public class NewsActivity1 extends Activity implements OnTouchListener,
		GestureDetector.OnGestureListener, OnItemClickListener {

	Context context;
	Button return_bt;
//	Button add_bt;
	

	private boolean hasMeasured = false;// 是否Measured.
	private LinearLayout layout_left;// 左边布局
	private LinearLayout layout_right;// 右边布局
	private TextView iv_set;// 图片
	private ListView lv_set;// 设置菜单
	private ListView lv_news;
//	private TextView dn_text;
	private TextView title_text;
	private ProgressBar progressbar;
	// private RelativeLayout progressbar_layout;

	/** 每次自动展开/收缩的范围 */
	private int MAX_WIDTH = 0;
	/** 每次自动展开/收缩的速度 */
	private final static int SPEED = 30;

	private final static int sleep_time = 5;

	private GestureDetector mGestureDetector;// 手势
	private boolean isScrolling = false;
	private float mScrollX; // 滑块滑动距离
	private int window_width;// 屏幕的宽度

	private String TAG = "jj";

	private View view = null;// 点击的view
	private String dept_id = "0";

	private RightMenuLinearLayout mylaout;
	private RightMenuLinearLayout mywebviewlayout;
	ArrayList<HashMap<String, Object>> remoteWindowItem;
	ArrayList<String> data_array = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newss);

		context = this;

		InitView();

		mGetDisplayDataThread runnable = new mGetDisplayDataThread();
		Thread thread = new Thread(runnable);
		thread.start();
		
//		dn_text.setText("按部门浏览");
		
		dept_id="0";
		mGetTopicDataThread runnable_news = new mGetTopicDataThread();
		Thread thread_news = new Thread(runnable_news);
		thread_news.start();
		
		
	}

	void InitView() {
		return_bt = (Button) this.findViewById(R.id.return_bt);
//		add_bt = (Button) this.findViewById(R.id.add_bt);
		layout_left = (LinearLayout) findViewById(R.id.layout_left);
		layout_right = (LinearLayout) findViewById(R.id.layout_right);
		lv_set = (ListView) findViewById(R.id.lv_set);
		lv_news = (ListView) findViewById(R.id.listview);
		iv_set = (TextView) findViewById(R.id.iv_set);
//		dn_text = (TextView) this.findViewById(R.id.dn_content_text);
		title_text = (TextView) this.findViewById(R.id.item_text);
		mylaout = (RightMenuLinearLayout) findViewById(R.id.mylaout);
		mywebviewlayout = (RightMenuLinearLayout) findViewById(R.id.mywebviewlaout);
		progressbar = (ProgressBar) this
				.findViewById(R.id.fengcai_display_progressbar);
		// progressbar_layout = (RelativeLayout)
		// this.findViewById(R.id.fengcai_display_progressbar_layout);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		progressbar.setAnimation(animation);
		/***
		 * 实现该接口
		 */
		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
//		add_bt.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent1 = new Intent();
//				intent1.setClass(NewsActivity1.this,NewsPhotoActivity.class);
//				startActivity(intent1);
//			}
//		});
		mylaout.setOnScrollListener(new OnScrollListener() {
			@Override
			public void doScroll(float distanceX) {
				doScrolling(distanceX);
			}

			@Override
			public void doLoosen() {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
						.getLayoutParams();
				Log.e("jj", "layoutParams.leftMargin="
						+ layoutParams.leftMargin);
				// 缩回去
				if (layoutParams.leftMargin < -window_width / 3) {
					new AsynMove().execute(-SPEED);
				} else {
					new AsynMove().execute(SPEED);
				}
			}
		});

		mywebviewlayout.setOnScrollListener(new OnScrollListener() {
			@Override
			public void doScroll(float distanceX) {
				doScrolling(distanceX);
			}

			@Override
			public void doLoosen() {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
						.getLayoutParams();
				Log.e("jj", "layoutParams.leftMargin="
						+ layoutParams.leftMargin);
				// 缩回去
				if (layoutParams.leftMargin < -window_width / 3) {
					new AsynMove().execute(-SPEED);
				} else {
					new AsynMove().execute(SPEED);
				}
			}
		});

		// 点击监听
		lv_set.setOnItemClickListener(this);
		iv_set.setOnTouchListener(this);
		layout_right.setOnTouchListener(this);
		layout_left.setOnTouchListener(this);
		mGestureDetector = new GestureDetector(this);
		// 禁用长按监听
		mGestureDetector.setIsLongpressEnabled(false);
		getMAX_WIDTH();
	}

	/***
	 * listview 正在滑动时执行.
	 */
	void doScrolling(float distanceX) {
		isScrolling = true;
		mScrollX += distanceX;// distanceX:向左为正，右为负

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
				.getLayoutParams();
		RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) layout_right
				.getLayoutParams();
		layoutParams.leftMargin -= mScrollX;
		layoutParams_1.leftMargin = window_width + layoutParams.leftMargin;
		if (layoutParams.leftMargin >= 0) {
			isScrolling = false;// 拖过头了不需要再执行AsynMove了
			layoutParams.leftMargin = 0;
			layoutParams_1.leftMargin = window_width;

		} else if (layoutParams.leftMargin <= -MAX_WIDTH) {
			// 拖过头了不需要再执行AsynMove了
			isScrolling = false;
			layoutParams.leftMargin = -MAX_WIDTH;
			layoutParams_1.leftMargin = window_width - MAX_WIDTH;
		}
		Log.v(TAG, "layoutParams.leftMargin=" + layoutParams.leftMargin
				+ ",layoutParams_1.leftMargin =" + layoutParams_1.leftMargin);

		layout_left.setLayoutParams(layoutParams);
		layout_right.setLayoutParams(layoutParams_1);
	}

	void getMAX_WIDTH() {
		ViewTreeObserver viewTreeObserver = layout_left.getViewTreeObserver();
		// 获取控件宽度
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (!hasMeasured) {
					window_width = getWindowManager().getDefaultDisplay()
							.getWidth();
					MAX_WIDTH = layout_right.getWidth();
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
							.getLayoutParams();
					RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) layout_right
							.getLayoutParams();
					ViewGroup.LayoutParams layoutParams_2 = mylaout
							.getLayoutParams();
					// 注意： 设置layout_left的宽度。防止被在移动的时候控件被挤压
					layoutParams.width = window_width;
					layout_left.setLayoutParams(layoutParams);

					// 设置layout_right的初始位置.
					layoutParams_1.leftMargin = window_width;
					layout_right.setLayoutParams(layoutParams_1);
					// 注意：设置lv_set的宽度防止被在移动的时候控件被挤压
					layoutParams_2.width = MAX_WIDTH;
					mylaout.setLayoutParams(layoutParams_2);

					Log.v(TAG, "MAX_WIDTH=" + MAX_WIDTH + "width="
							+ window_width);
					hasMeasured = true;
				}
				return true;
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
			if (layoutParams.leftMargin < 0) {
				new AsynMove().execute(SPEED);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		view = v;// 记录点击的控件

		// 松开的时候要判断，如果不到半屏幕位子则缩回去，
		if (MotionEvent.ACTION_UP == event.getAction() && isScrolling == true) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
			// 缩回去
			if (layoutParams.leftMargin < -window_width / 3) {
				new AsynMove().execute(-SPEED);
			} else {
				new AsynMove().execute(SPEED);
			}
		}

		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {

		int position = lv_set.pointToPosition((int) e.getX(), (int) e.getY());
		if (position != ListView.INVALID_POSITION) {
			View child = lv_set.getChildAt(position
					- lv_set.getFirstVisiblePosition());
			if (child != null)
				child.setPressed(true);
		}

		mScrollX = 0;
		isScrolling = false;
		// 将之改为true，才会传递给onSingleTapUp,不然事件不会向下传递.
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	/***
	 * 点击松开执行
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// 点击的不是layout_left
		if (view != null && view == iv_set) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
			// 左移动
			if (layoutParams.leftMargin >= 0) {
				new AsynMove().execute(-SPEED);
				lv_set.setSelection(0);// 设置为首位.
			} else {
				// 右移动
				new AsynMove().execute(SPEED);
			}
		} else if (view != null && view == layout_left) {
			RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
			if (layoutParams.leftMargin < 0) {
				// 说明layout_left处于移动最左端状态，这个时候如果点击layout_left应该直接所以原有状态.(更人性化)
				// 右移动
				new AsynMove().execute(SPEED);
			}
		}

		return true;
	}

	/***
	 * 滑动监听 就是一个点移动到另外一个点. distanceX=后面点x-前面点x，如果大于0，说明后面点在前面点的右边及向右滑动
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// 执行滑动.
		doScrolling(distanceX);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	class AsynMove extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int times = 0;
			if (MAX_WIDTH % Math.abs(params[0]) == 0)// 整除
				times = MAX_WIDTH / Math.abs(params[0]);
			else
				times = MAX_WIDTH / Math.abs(params[0]) + 1;// 有余数

			for (int i = 0; i < times; i++) {
				publishProgress(params[0]);
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		/**
		 * update UI
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
			RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) layout_right
					.getLayoutParams();
			// 右移动
			if (values[0] > 0) {
				layoutParams.leftMargin = Math.min(layoutParams.leftMargin
						+ values[0], 0);
				layoutParams_1.leftMargin = Math.min(layoutParams_1.leftMargin
						+ values[0], window_width);
				Log.v(TAG, "layout_left右" + layoutParams.leftMargin
						+ ",layout_right右" + layoutParams_1.leftMargin);
			} else {
				// 左移动
				layoutParams.leftMargin = Math.max(layoutParams.leftMargin
						+ values[0], -MAX_WIDTH);
				layoutParams_1.leftMargin = Math.max(layoutParams_1.leftMargin
						+ values[0], window_width - MAX_WIDTH);
				Log.v(TAG, "layout_left左" + layoutParams.leftMargin
						+ ",layout_right左" + layoutParams_1.leftMargin);
			}
			layout_right.setLayoutParams(layoutParams_1);
			layout_left.setLayoutParams(layoutParams);

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_left
				.getLayoutParams();
		// 只要没有滑动则都属于点击
		// Toast.makeText(context, title[position], 1).show();

//		dn_text.setText(remoteWindowItem.get(position).get("Class").toString());
		dept_id = remoteWindowItem.get(position).get("Id").toString();
		mGetTopicDataThread runnable = new mGetTopicDataThread();
		Thread thread = new Thread(runnable);
		thread.start();

		if (layoutParams.leftMargin < 0) {
			new AsynMove().execute(SPEED);
		}
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 0) {
				progressbar.clearAnimation();
				progressbar.setVisibility(View.INVISIBLE);
				ArrayList<HashMap<String, Object>> remoteWindowItem = (ArrayList<HashMap<String, Object>>) msg.obj;
				NewsDeptAdapter topicAdapter = new NewsDeptAdapter(context,
						remoteWindowItem, R.layout.list_in_news_dept,
						new String[] { "Id", "Grade", "Class", "Remark" },
						new int[] { R.id.temp_tv, R.id.temp_tv, R.id.temp_tv,
								R.id.temp_tv });
				lv_set.setAdapter(topicAdapter);
			} else if (msg.arg1 == 1) {
				ArrayList<HashMap<String, Object>> remoteWindowItem = (ArrayList<HashMap<String, Object>>) msg.obj;
				List<ImageAndTextNewsList> imageAndTexts = new ArrayList<ImageAndTextNewsList>();
				for (int i = 0; i < remoteWindowItem.size(); i++) {
					HashMap<String, Object> data_hash = remoteWindowItem.get(i);
					String Id = data_hash.get("Id").toString();
					String Title = data_hash.get("Title").toString();
					String Thumbnail = data_hash.get("Thumbnail").toString();
					String Crtime = data_hash.get("Crtime").toString();
					ImageAndTextNewsList iat = new ImageAndTextNewsList(Id,
							Thumbnail, "0", Title, Title, Crtime);
					imageAndTexts.add(iat);
				}

				ImageAndTextNewsListAdapter ia = new ImageAndTextNewsListAdapter(
						NewsActivity1.this, imageAndTexts, lv_set, context);
				lv_news.setAdapter(ia);
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private class mGetTopicDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String comurl = MessengerService.URL;
			String commethodname = MessengerService.METHOD_GetNewsByDepartmentId;
			String comnamespace = MessengerService.NAMESPACE;
			String comsoapaction = comnamespace + "/" + commethodname;

			SoapObject rpc = new SoapObject(comnamespace, commethodname);
			rpc.addProperty("deptid", dept_id);
			rpc.addProperty("pagesize", 10);
			rpc.addProperty("pageindex", 1);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(comurl);
			ht.debug = true;
			try {
				ht.call(comsoapaction, envelope);
				SoapObject commu = (SoapObject) envelope.bodyIn;
				ArrayList<HashMap<String, Object>> remoteWindowItem = new ArrayList<HashMap<String, Object>>();
				SoapObject soapchilds = (SoapObject) commu.getProperty(0);
				// SoapObject soapchildss = (SoapObject)
				// soapchilds.getProperty(1);
				// SoapObject soapchildtemp = (SoapObject) soapchildss
				// .getProperty(0);
				int _item_count = soapchilds.getPropertyCount();
				for (int j = 0; j < _item_count; j++) {
					SoapObject soapchildsson = (SoapObject) soapchilds
							.getProperty(j);

					String Id = soapchildsson.getProperty("Id").toString();
					String Title = soapchildsson.getProperty("Title")
							.toString();
					String Thumbnail = soapchildsson.getProperty("Thumbnail")
							.toString();
					String Crtime = soapchildsson.getProperty("Crtime")
							.toString();

					HashMap<String, Object> mapdevinfo = new HashMap<String, Object>();
					mapdevinfo.put("Id", Id);
					mapdevinfo.put("Title", Title);
					mapdevinfo.put("Thumbnail", Thumbnail);
					mapdevinfo.put("Crtime", Crtime);
					remoteWindowItem.add(mapdevinfo);
				}
				Message msg = new Message();
				msg.arg1 = 1;
				msg.obj = remoteWindowItem;
				threadMessageHandler.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("TOPIC GET DATA ERROR : ", e.toString());
			}
		}
	}

	private class mGetDisplayDataThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String qurl = MessengerService.URL;
			String qmethodname = MessengerService.METHOD_GetDeptList;
			String qnamespace = MessengerService.NAMESPACE;
			String qsoapaction = qnamespace + "/" + qmethodname;

			SoapObject rpc = new SoapObject(qnamespace, qmethodname);
			// rpc.addProperty("id", id);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(qurl);
			ht.debug = true;
			try {
				ht.call(qsoapaction, envelope);
				remoteWindowItem = new ArrayList<HashMap<String, Object>>();

				HashMap<String, Object> mapdevinfo0 = new HashMap<String, Object>();
				mapdevinfo0.put("Id", "0");
				mapdevinfo0.put("Grade", "");
				mapdevinfo0.put("Class", "全部风采");
				mapdevinfo0.put("Remark", "");
				remoteWindowItem.add(mapdevinfo0);

				SharedPreferences sharedPreferences = getSharedPreferences(
						"user_info", Activity.MODE_PRIVATE);
				String DeptId = sharedPreferences.getString("DeptId", "");

				HashMap<String, Object> mapdevinfo1 = new HashMap<String, Object>();
				mapdevinfo1.put("Id", DeptId);
				mapdevinfo1.put("Grade", "");
				mapdevinfo1.put("Class", "我的部门");
				mapdevinfo1.put("Remark", "");
				remoteWindowItem.add(mapdevinfo1);

				SoapObject journal = (SoapObject) envelope.bodyIn;
				for (int i = 0; i < journal.getPropertyCount(); i++) {
					SoapObject soapchilds = (SoapObject) journal.getProperty(i);
					for (int j = 0; j < soapchilds.getPropertyCount(); j++) {
						SoapObject soapchildsson = (SoapObject) soapchilds
								.getProperty(j);

						String Id = soapchildsson.getProperty("Id").toString();
						String Grade = soapchildsson.getProperty("Grade")
								.toString();
						String Class = soapchildsson.getProperty("Class")
								.toString();
						String Remark = soapchildsson.getProperty("Remark")
								.toString();

						HashMap<String, Object> mapdevinfo = new HashMap<String, Object>();
						mapdevinfo.put("Id", Id);
						mapdevinfo.put("Grade", Grade);
						mapdevinfo.put("Class", Class);
						mapdevinfo.put("Remark", Remark);
						remoteWindowItem.add(mapdevinfo);

					}
				}
				Message msg = new Message();
				msg.arg1 = 0;
				msg.obj = remoteWindowItem;
				threadMessageHandler.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
