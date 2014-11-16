package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lnpdit.stategrid.informatization.adapter.ImageAndTextNewsList;
import lnpdit.stategrid.informatization.adapter.ImageAndTextNewsListAdapter;
import lnpdit.stategrid.informatization.data.MessengerService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class NewsActivity extends Activity{

	Context context;
	
	Button return_bt;
	ListView listview;
	ProgressBar progressbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		context = this;
		viewInit();
	}
	
	private void viewInit(){
		return_bt = (Button) this.findViewById(R.id.return_bt);
		listview = (ListView) this.findViewById(R.id.listview);
		progressbar = (ProgressBar) this.findViewById(R.id.progressbar);
		
		return_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mGetTopicDataThread datarunnable = new mGetTopicDataThread();
		Thread thread = new Thread(datarunnable);
		thread.start();
	}
	
	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				progressbar.setVisibility(8);
				
				ArrayList<HashMap<String, Object>> remoteWindowItem = (ArrayList<HashMap<String, Object>>) msg.obj;
				List<ImageAndTextNewsList> imageAndTexts = new ArrayList<ImageAndTextNewsList>();
				for (int i = 0; i < remoteWindowItem.size(); i++) {
					HashMap<String, Object> data_hash = remoteWindowItem.get(i);
					String Id = data_hash.get("Id").toString();
					String Title = data_hash.get("Title").toString();
					String Thumbnail = data_hash.get("Thumbnail").toString();
					String Crtime = data_hash.get("Crtime").toString();
					ImageAndTextNewsList iat = new ImageAndTextNewsList(Id, Thumbnail, "0", Title, Title, Crtime);
					imageAndTexts.add(iat);
				}

				ImageAndTextNewsListAdapter ia = new ImageAndTextNewsListAdapter(NewsActivity.this, imageAndTexts, listview, context);
				listview.setAdapter(ia);
				
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("Topic threadMessageHandler error : ", e.toString());
			}
		}
	};
	
	@SuppressLint("HandlerLeak")
	private class mGetTopicDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String comurl = MessengerService.URL;
			String commethodname = MessengerService.METHOD_GetNewsTitlePageSize;
			String comnamespace = MessengerService.NAMESPACE;
			String comsoapaction = comnamespace + "/" + commethodname;

			SoapObject rpc = new SoapObject(comnamespace, commethodname);
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
//				SoapObject soapchildss = (SoapObject) soapchilds.getProperty(1);
//				SoapObject soapchildtemp = (SoapObject) soapchildss
//						.getProperty(0);
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
				msg.arg1 = 0;
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
	
}
