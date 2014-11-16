package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lnpdit.stategrid.informatization.adapter.ImageAndTextListVideolAdapter;
import lnpdit.stategrid.informatization.adapter.ImageAndTextVideo;
import lnpdit.stategrid.informatization.adapter.PicAdapter;
import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class VideoActivity extends Activity {

	private Context context;

	ListView listview;
	Button return_bt;
	private ProgressDialog dialog;
	public boolean sync_state;
	int CLEAR_INFO = 2;
	int REFRESH_RATE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.journal_tabhost_video);
		context = this;
		viewInit();
		if (listview.getCount() == 0) {
			sync_state = false;
			dialog = new ProgressDialog(this);
			dialog.setMessage("正在加载视频列表,请稍等.");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}
		Thread thread = new Thread(new mGetJournalDataThread());
		thread.start();
	}

	private void viewInit() {
		listview = (ListView) findViewById(R.id.journal_list_video);
		return_bt = (Button) this.findViewById(R.id.return_bt);
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
			GetData();
			dialog.dismiss();
		}
	};

	private void GetData() {

		List<ImageAndTextVideo> imageAndTexts = new ArrayList<ImageAndTextVideo>();
		ToDoDB tdd = new ToDoDB(context);
		Cursor cursor = tdd.selectvideo();
		int a = cursor.getCount();
		cursor.moveToFirst();
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount() / 2; i++) {
				String webid1 = cursor.getString(1);
				String title1 = cursor.getString(2);
				String pic1 = cursor.getString(3);
				String video1 = cursor.getString(4);
				String extension1 = cursor.getString(5);
				String size1 = cursor.getString(6);
				String content1 = cursor.getString(7);
				String crtime1 = cursor.getString(8);
				cursor.moveToNext();
				String webid2 = cursor.getString(1);
				String title2 = cursor.getString(2);
				String pic2 = cursor.getString(3);
				String video2 = cursor.getString(4);
				String extension2 = cursor.getString(5);
				String size2 = cursor.getString(6);
				String content2 = cursor.getString(7);
				String crtime2 = cursor.getString(8);
				cursor.moveToNext();
				ImageAndTextVideo itv = new ImageAndTextVideo(
						MessengerService.VIDEO_PATH + pic1, webid1, title1,
						pic1, MessengerService.VIDEO_PATH + video1, extension1,
						size1, content1, crtime1, MessengerService.VIDEO_PATH
								+ pic2, webid2, title2, pic2,
						MessengerService.VIDEO_PATH + video2, extension2,
						size2, content2, crtime2);
				imageAndTexts.add(itv);
			}
			if (cursor.getCount() % 2 != 0) {
				String webid = cursor.getString(1);
				String title = cursor.getString(2);
				String pic = cursor.getString(3);
				String video = cursor.getString(4);
				String extension = cursor.getString(5);
				String size = cursor.getString(6);
				String content = cursor.getString(7);
				String crtime = cursor.getString(8);
				ImageAndTextVideo itj = new ImageAndTextVideo(
						MessengerService.VIDEO_PATH + pic, webid, title, pic,
						MessengerService.VIDEO_PATH + video, extension, size,
						content, crtime, "none", "none", "none", "none",
						"none", "none", "none", "none", "none");
				imageAndTexts.add(itj);
			}
			ImageAndTextListVideolAdapter itj_adapter = new ImageAndTextListVideolAdapter(
					VideoActivity.this, imageAndTexts, listview,
					context);
			listview.setAdapter(itj_adapter);
		}
	}

	@SuppressLint("HandlerLeak")
	private class mGetJournalDataThread implements Runnable {

		Context _context;
		PicAdapter lvbt;
		ListView _listview;

		public void getSyncState(Context c) {
			this._context = c;
		}

		public void setListAdapter(ListView l) {
			this._listview = l;
			_listview.setAdapter(lvbt);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ToDoDB tdd = new ToDoDB(context);
			tdd.clearvideo();
			String qurl = MessengerService.URL;
			String qmethodname = MessengerService.METHOD_GetVideoInfo;
			String qnamespace = MessengerService.NAMESPACE;
			String qsoapaction = qnamespace + "/" + qmethodname;

			SoapObject rpc = new SoapObject(qnamespace, qmethodname);
			rpc.addProperty("pagesize", 10);
			rpc.addProperty("pageindex", 1);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(qurl);
			ht.debug = true;
			try {
				ht.call(qsoapaction, envelope);
				SoapObject journal = (SoapObject) envelope.bodyIn;
				for (int i = 0; i < journal.getPropertyCount(); i++) {
					SoapObject soapchilds = (SoapObject) journal.getProperty(i);
					for (int j = 0; j < soapchilds.getPropertyCount(); j++) {
						SoapObject soapchildsson = (SoapObject) soapchilds
								.getProperty(j);

						String webid = soapchildsson.getProperty("Id")
								.toString();
						String title = soapchildsson.getProperty("Title")
								.toString();
						String pic = soapchildsson.getProperty("Pic")
								.toString();
						String video = soapchildsson.getProperty("Video")
								.toString();
						String extension = soapchildsson.getProperty(
								"Extension").toString();
						String size = soapchildsson.getProperty("Size")
								.toString();
						String content = soapchildsson.getProperty("Content")
								.toString();
						String crtime = soapchildsson.getProperty("Crtime")
								.toString();

						ContentValues values = new ContentValues();
						values.put(tdd.VIDEO_WEBID, webid);
						values.put(tdd.VIDEO_TITLE, title);
						values.put(tdd.VIDEO_PIC, pic);
						values.put(tdd.VIDEO_VIDEO, video);
						values.put(tdd.VIDEO_EXTENSION, extension);
						values.put(tdd.VIDEO_SIZE, size);
						values.put(tdd.VIDEO_CONTENT, content);
						values.put(tdd.VIDEO_CRTIME, crtime);
						long aaa = tdd.insertvideo(values);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = new Message();
			msg.obj = lvbt;
			threadMessageHandler.sendMessage(msg);
		}
	}
}
