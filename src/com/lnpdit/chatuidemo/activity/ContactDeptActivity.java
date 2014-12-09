package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lnpdit.stategrid.informatization.adapter.ContactDeptAdapter;
import lnpdit.stategrid.informatization.adapter.JobPlanAdapter;
import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;
import lnpdit.stategrid.informatization.tools.MyLetterListView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ContactDeptActivity extends Activity {

	Context context;

	ListView listview;
	Button return_bt;
	Button search_bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_dept);

		context = this;

		viewInit();

		mGetContactDataThread runnable = new mGetContactDataThread();
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private void viewInit() {
		listview = (ListView) this.findViewById(R.id.listview);
		return_bt = (Button) this.findViewById(R.id.return_bt);
		search_bt = (Button) this.findViewById(R.id.search_bt);

		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		search_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			Intent intent = new Intent();
			intent.setClass(ContactDeptActivity.this,ContactListSearchActivity.class);
			
			SharedPreferences sharedPreferences = getSharedPreferences(
					"user_info", Activity.MODE_PRIVATE);
			String DeptId = sharedPreferences.getString("DeptId", "");
			
			intent.putExtra("Id", DeptId);
			intent.putExtra("Grade", "");
			intent.putExtra("Class", "我的部门");
			intent.putExtra("Remark", "");
			
			startActivity(intent);
			}
		});
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				ArrayList<HashMap<String, Object>> remoteWindowItem = (ArrayList<HashMap<String, Object>>) msg.obj;
				int count = remoteWindowItem.size();
				ArrayList<HashMap<String, Object>> remoteWindowItem_double = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < remoteWindowItem.size() / 2; i++) {
					HashMap<String, Object> hash1 = remoteWindowItem.get(i * 2);
					String id1 = hash1.get("Id").toString();
					String grade1 = hash1.get("Grade").toString();
					String class1 = hash1.get("Class").toString();
					String remark1 = hash1.get("Remark").toString();

					HashMap<String, Object> hash2 = remoteWindowItem.get(i * 2 + 1);
					String id2 = hash2.get("Id").toString();
					String grade2 = hash2.get("Grade").toString();
					String class2 = hash2.get("Class").toString();
					String remark2 = hash2.get("Remark").toString();

					HashMap<String, Object> hash_double = new HashMap<String, Object>();
					hash_double.put("id1", id1);
					hash_double.put("grade1", grade1);
					hash_double.put("class1", class1);
					hash_double.put("remark1", remark1);
					hash_double.put("id2", id2);
					hash_double.put("grade2", grade2);
					hash_double.put("class2", class2);
					hash_double.put("remark2", remark2);
					remoteWindowItem_double.add(hash_double);
				}
				if (remoteWindowItem.size() % 1 != 0) {
					HashMap<String, Object> hash1 = remoteWindowItem.get(count - 1);
					String id1 = hash1.get("Id").toString();
					String grade1 = hash1.get("Grade").toString();
					String class1 = hash1.get("Class").toString();
					String remark1 = hash1.get("Remark").toString();

					HashMap<String, Object> hash_double = new HashMap<String, Object>();
					hash_double.put("id1", id1);
					hash_double.put("grade1", grade1);
					hash_double.put("class1", class1);
					hash_double.put("remark1", remark1);
					hash_double.put("id2", "");
					hash_double.put("grade2", "");
					hash_double.put("class2", "");
					hash_double.put("remark2", "");
					remoteWindowItem_double.add(hash_double);
				}
				ContactDeptAdapter topicAdapter = new ContactDeptAdapter(
						context, remoteWindowItem_double, R.layout.list_in_weather,
						new String[] { "id1", "grade1", "class1", "remark1",
								"id2", "grade2", "class2", "remark2" },
						new int[] { R.id.temp_tv, R.id.temp_tv, R.id.temp_tv,
								R.id.temp_tv });
				listview.setAdapter(topicAdapter);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private class mGetContactDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ToDoDB tdd = new ToDoDB(context);
			// tdd.clearcontact();
			String curl = MessengerService.URL_WITHOUT_WSDL;
			String cmethodname = MessengerService.METHOD_GetDeptList;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
			// rpc.addProperty("userid", userId);
			// rpc.addProperty("pagesize", 1000);
			// rpc.addProperty("pageindex", 1);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(curl);
			ht.debug = true;

			try {
				ht.call(csoapaction, envelope);
				SoapObject contactlist = (SoapObject) envelope.bodyIn;
				ArrayList<HashMap<String, Object>> remoteWindowItem = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < contactlist.getPropertyCount(); i++) {
					SoapObject soapchilds = (SoapObject) contactlist
							.getProperty(i);
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
