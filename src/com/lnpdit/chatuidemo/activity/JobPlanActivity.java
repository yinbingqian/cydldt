package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import lnpdit.stategrid.informatization.adapter.JobPlanAdapter;
import lnpdit.stategrid.informatization.adapter.WeatherAdapter;
import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.tools.PullDownView;
import lnpdit.stategrid.informatization.tools.PullDownView.OnPullDownListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class JobPlanActivity extends Activity implements OnPullDownListener {

	Context context;
	Button return_bt;
	Button add_bt;

	Resources resources;
	ProgressBar progressbar;
	ListView listview;
	private PullDownView mPullDownView;
	public boolean sync_state;
	int pagesize;
	JobPlanAdapter topicAdapter;
	String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobplan);

		context = this;

		SharedPreferences sp = this.getSharedPreferences("user_info",
				MODE_APPEND);
		userid = sp.getString("Id", "");

		viewInit();
		pagesize = 1;

	}

	private void viewInit() {
		return_bt = (Button) this.findViewById(R.id.return_bt);
		add_bt = (Button) this.findViewById(R.id.plan_add);

		return_bt.setOnClickListener(btnListener);
		add_bt.setOnClickListener(btnListener);

		progressbar = (ProgressBar) this.findViewById(R.id.progressbar);

		mPullDownView = (PullDownView) this.findViewById(R.id.plan_list);
		mPullDownView.setOnPullDownListener(this);
		listview = mPullDownView.getListView();
		// listview.setDivider(resources
		// .getDrawable(R.drawable.listview_line_blank));
		Animation listview_anim = AnimationUtils.loadAnimation(this,
				R.anim.fade);
		listview.setAnimation(listview_anim);

		mPullDownView.setHideHeader();
		mPullDownView.setShowHeader();
	}

	private void getNewData() {
		try {
			sync_state = false;
			final Thread thread = new Thread(new mGetTopicDataThread());
			thread.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.arg1) {
				case 0:
					ArrayList<HashMap<String, Object>> remoteWindowItem = (ArrayList<HashMap<String, Object>>) msg.obj;
					int i = remoteWindowItem.size();
					if (pagesize == 1) {
						topicAdapter = new JobPlanAdapter(context,
								remoteWindowItem, R.layout.list_in_weather,
								new String[] { "FromName", "ToName", "Id",
										"FromUser", "ToUser", "Content",
										"Type", "Crtime" }, new int[] {
										R.id.temp_tv, R.id.temp_tv,
										R.id.temp_tv, R.id.temp_tv,
										R.id.temp_tv, R.id.temp_tv,
										R.id.temp_tv, R.id.temp_tv });
						listview.setAdapter(topicAdapter);
						mPullDownView.setShowFooter();
					} else {
						topicAdapter.addItem(remoteWindowItem);
						topicAdapter.notifyDataSetChanged();
					}
					progressbar.setVisibility(8);
					break;
				case 1:
					// listview.setVisibility(MessengerService.VISIBILITY_FALSE);
					break;
				case 2:
					mPullDownView.setHideFooter();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("Topic threadMessageHandler error : ", e.toString());
			}
		}
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					pagesize = 1;
					getNewData();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mPullDownView.RefreshComplete();
			}
		}).start();
	}

	@Override
	public void onMore() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					pagesize = pagesize + 1;
					getNewData();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mPullDownView.notifyDidMore();
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private class mGetTopicDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String comurl = MessengerService.URL;
			String commethodname = MessengerService.METHOD_GetJobPlan;
			String comnamespace = MessengerService.NAMESPACE;
			String comsoapaction = comnamespace + "/" + commethodname;

			SoapObject rpc = new SoapObject(comnamespace, commethodname);
			rpc.addProperty("pagesize", 10);
			rpc.addProperty("pageindex", pagesize);
			rpc.addProperty("toUser", userid);
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
				// SoapObject soapchildtemp = (SoapObject) soapchilds
				// .getProperty(0);
				sync_state = true;
				int _item_count = soapchilds.getPropertyCount();

				for (int j = 0; j < _item_count; j++) {
					SoapObject soapchildsson = (SoapObject) soapchilds
							.getProperty(j);

					String FromName = soapchildsson.getProperty("FromName")
							.toString();
					String ToName = soapchildsson.getProperty("ToName")
							.toString();
					String Id = soapchildsson.getProperty("Id").toString();
					String FromUser = soapchildsson.getProperty("FromUser")
							.toString();
					String ToUser = soapchildsson.getProperty("ToUser")
							.toString();
					String Content = soapchildsson.getProperty("Content")
							.toString();
					String Type = soapchildsson.getProperty("Type").toString();
					String Crtime = soapchildsson.getProperty("Crtime")
							.toString();

					HashMap<String, Object> mapdevinfo = new HashMap<String, Object>();
					mapdevinfo.put("FromName", FromName);
					mapdevinfo.put("ToName", ToName);
					mapdevinfo.put("Id", Id);
					mapdevinfo.put("FromUser", FromUser);
					mapdevinfo.put("ToUser", ToUser);
					mapdevinfo.put("Content", Content);
					mapdevinfo.put("Type", Type);
					mapdevinfo.put("Crtime", Crtime);
					remoteWindowItem.add(mapdevinfo);
				}
				Message msg = new Message();
				msg.arg1 = 0;
				msg.obj = remoteWindowItem;
				threadMessageHandler.sendMessage(msg);

				if (_item_count < 10) {
					Message msg_remove_foot = new Message();
					msg_remove_foot.arg1 = 2;
					threadMessageHandler.sendMessage(msg_remove_foot);
				}
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getNewData();
	}

	private android.view.View.OnClickListener btnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.return_bt:
				finish();
				break;
			case R.id.plan_add:
				Intent intent = new Intent();
				intent.setClass(JobPlanActivity.this, JobPlanAddActivity.class);
				intent.putExtra("userid", userid);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};

}
