package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lnpdit.chatuidemo.R;

public class PeixunDataContentActivity extends Activity {

	Context context;
	Button return_bt;
	WebView webview;

	String title;
	String id;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peixun_materials_content);

		context = this;
		Intent intent = this.getIntent();
		id = intent.getStringExtra("id");
		title = intent.getStringExtra("title");
		
		mGetPeixunListDataThread runnable = new mGetPeixunListDataThread();
		Thread thread = new Thread(runnable);
		thread.start();
		
		viewInit();

	}
	
	private void viewInit(){
		TextView title_tv = (TextView)this.findViewById(R.id.title_tv);
		webview = (WebView) this.findViewById(R.id.content_tv);
		title_tv.setText(title);
		
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
				webview.getSettings().setSupportZoom(true);
				webview.clearCache(true);
				webview.getSettings().setDefaultTextEncodingName("utf-8");
				webview.getSettings().setLayoutAlgorithm(
						LayoutAlgorithm.SINGLE_COLUMN);
				webview.loadDataWithBaseURL(null, msg.obj.toString(),
						"text/html", "utf-8", null);
//				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("peixun error:", e.toString());
			}
		}
	};
	private class mGetPeixunListDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String curl = MessengerService.URL_WITHOUT_WSDL;
			String cmethodname = MessengerService.METHOD_GetReaderItems;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
			 rpc.addProperty("id", id);
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
				
				SoapObject soapchilds = (SoapObject) peixunlist
						.getProperty(0);
				
//				SoapObject soapchildsson = (SoapObject) soapchilds
//						.getProperty(0);
				
				String content = soapchilds.getProperty("content")
						.toString();
				
//				ArrayList<HashMap<String, Object>> remoteWindowItem = new ArrayList<HashMap<String, Object>>();
//				for (int i = 0; i < peixunlist.getPropertyCount(); i++) {
//					SoapObject soapchilds = (SoapObject) peixunlist
//							.getProperty(i);
//					for (int j = 0; j < soapchilds.getPropertyCount(); j++) {
//						SoapObject soapchildsson = (SoapObject) soapchilds
//								.getProperty(j);
//
//						String id = soapchildsson.getProperty("id").toString();
//						String colid = soapchildsson.getProperty("colid")
//								.toString();
//						String title = soapchildsson.getProperty("title")
//								.toString();
//						String content = soapchildsson.getProperty("content")
//								.toString();
//						String orders = soapchildsson.getProperty("orders")
//								.toString();
//						String important = soapchildsson.getProperty("important")
//								.toString();
//						String remark1 = soapchildsson.getProperty("remark1")
//								.toString();
//						String remark2 = soapchildsson.getProperty("remark2")
//								.toString();
//						String remark3 = soapchildsson.getProperty("remark3")
//								.toString();
//						String crtime = soapchildsson.getProperty("crtime")
//								.toString();
//
//						HashMap<String, Object> mapdevinfo = new HashMap<String, Object>();
//						mapdevinfo.put("id", id);
//						mapdevinfo.put("colid", colid);
//						mapdevinfo.put("title", title);
//						mapdevinfo.put("content", content);
//						mapdevinfo.put("orders", orders);
//						mapdevinfo.put("important", important);
//						mapdevinfo.put("remark1", remark1);
//						mapdevinfo.put("remark2", remark2);
//						mapdevinfo.put("remark3", remark3);
//						mapdevinfo.put("crtime", crtime);
//						remoteWindowItem.add(mapdevinfo);
//
//					}
//				}

				Message msg = new Message();
				msg.arg1 = 0;
				msg.obj = content;
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
