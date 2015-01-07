package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lnpdit.stategrid.informatization.adapter.PeixunListAdapter;
import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lnpdit.chatuidemo.R;

public class PeixunListActivity extends Activity {

	Context context;
	ListView listview;
	Button return_bt;

	private ProgressBar progressbar;
	private ProgressDialog dialog;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peixun_list);

		context = this;

		viewInit();

		mGetPeixunListDataThread runnable = new mGetPeixunListDataThread();
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private void viewInit() {
		listview = (ListView) findViewById(R.id.list_view_peixun_dept);
		return_bt = (Button) findViewById(R.id.return_bt);

		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				ArrayList<HashMap<String, Object>> remoteWindowItem = (ArrayList<HashMap<String, Object>>) msg.obj;
				int count = remoteWindowItem.size();
				ArrayList<HashMap<String, Object>> remoteWindowItem1 = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < remoteWindowItem.size(); i++) {
					HashMap<String, Object> hash1 = remoteWindowItem.get(i);
					String id = hash1.get("id").toString();
					String deptid = hash1.get("deptid").toString();
					String title = hash1.get("title").toString();
					String remark1 = hash1.get("remark1").toString();
					String remark2 = hash1.get("remark2").toString();
					String remark3 = hash1.get("remark3").toString();
					String crtime = hash1.get("crtime").toString();
					remoteWindowItem1.add(hash1);
				}

				PeixunListAdapter topicAdapter = new PeixunListAdapter(context,
						remoteWindowItem1, R.layout.list_in_weather,
						new String[] { "id", "deptid", "title", "remark1",
								"remark2", "remark3", "crtime" }, new int[] {
								R.id.temp_tv, R.id.temp_tv, R.id.temp_tv,
								R.id.temp_tv, R.id.temp_tv, R.id.temp_tv,
								R.id.temp_tv });
				listview.setAdapter(topicAdapter);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private class mGetPeixunListDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ToDoDB tdd = new ToDoDB(context);
			// tdd.clearcontact();
			String curl = MessengerService.URL_WITHOUT_WSDL;
			String cmethodname = MessengerService.METHOD_GetTikuColumn;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
			// rpc.addProperty("userid", userId);
			rpc.addProperty("pagesize", 1000);
			rpc.addProperty("pageindex", 1);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(curl);
			ht.debug = true;

			try {
				ht.call(csoapaction, envelope);
				SoapObject peixunlist = (SoapObject) envelope.bodyIn;
				ArrayList<HashMap<String, Object>> remoteWindowItem = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < peixunlist.getPropertyCount(); i++) {
					SoapObject soapchilds = (SoapObject) peixunlist
							.getProperty(i);
					for (int j = 0; j < soapchilds.getPropertyCount(); j++) {
						SoapObject soapchildsson = (SoapObject) soapchilds
								.getProperty(j);

						String id = soapchildsson.getProperty("id").toString();
						String deptid = soapchildsson.getProperty("deptid")
								.toString();
						String title = soapchildsson.getProperty("title")
								.toString();
						String remark1 = soapchildsson.getProperty("remark1")
								.toString();
						String remark2 = soapchildsson.getProperty("remark2")
								.toString();
						String remark3 = soapchildsson.getProperty("remark3")
								.toString();
						String crtime = soapchildsson.getProperty("crtime")
								.toString();

						HashMap<String, Object> mapdevinfo = new HashMap<String, Object>();
						mapdevinfo.put("id", id);
						mapdevinfo.put("deptid", deptid);
						mapdevinfo.put("title", title);
						mapdevinfo.put("remark1", remark1);
						mapdevinfo.put("remark2", remark2);
						mapdevinfo.put("remark3", remark3);
						mapdevinfo.put("crtime", crtime);
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
