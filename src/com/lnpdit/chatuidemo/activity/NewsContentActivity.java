package com.lnpdit.chatuidemo.activity;

import java.io.IOException;

import lnpdit.stategrid.informatization.data.MessengerService;

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
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;

public class NewsContentActivity extends Activity {

	Context context;

	String id;
	String title;
	String time;

	Button return_bt;
	TextView toptitle_tv;
	ProgressBar progressbar;
	TextView title_tv;
	TextView time_tv;
	WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_display);

		Intent intent = this.getIntent();
		id = intent.getStringExtra("id");
		title = intent.getStringExtra("title");
		time = intent.getStringExtra("time");

		viewInit();
		mGetNewsData content_runnable = new mGetNewsData();
		Thread thread = new Thread(content_runnable);
		thread.start();
	}

	private void viewInit() {
		return_bt = (Button) this.findViewById(R.id.return_bt);
		// progressbar = (ProgressBar) this.findViewById(R.id.progressbar);
		title_tv = (TextView) this.findViewById(R.id.content_title_tv);
		time_tv = (TextView) this.findViewById(R.id.a_time);
		webview = (WebView) this.findViewById(R.id.content_web);

		title_tv.setText(title);
		time_tv.setText(time);

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
				// progressbar.setVisibility(8);

				webview.getSettings().setSupportZoom(true);
				webview.clearCache(true);
				webview.getSettings().setDefaultTextEncodingName("utf-8");
				webview.getSettings().setLayoutAlgorithm(
						LayoutAlgorithm.SINGLE_COLUMN);
				webview.loadDataWithBaseURL(null, msg.obj.toString(),
						"text/html", "utf-8", null);
				// webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				// webview.setBackgroundColor(0);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	public class mGetNewsData implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String url = MessengerService.URL;
			String methodname = MessengerService.METHOD_GetNewsContent;
			String namespace = MessengerService.NAMESPACE;
			String soapaction = namespace + "/" + methodname;
			SoapObject rpc = new SoapObject(namespace, methodname);
			rpc.addProperty("id", id);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(url);
			ht.debug = true;
			try {
				ht.call(soapaction, envelope);
				SoapObject newslist = (SoapObject) envelope.bodyIn;
				SoapObject soapchilds = (SoapObject) newslist.getProperty(0);
				String newscontent = soapchilds.getProperty("Content")
						.toString();
				Message msg = new Message();
				msg.obj = newscontent;
				threadMessageHandler.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

}
