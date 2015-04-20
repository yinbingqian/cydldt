package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lnpdit.stategrid.informatization.data.MessengerService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lnpdit.chatuidemo.R;

public class PeixunQuestionActivity extends Activity {

	Context context;
	Resources resources;

//	String type = "";
//	String pian = "";
//	String zhang = "";
//	String jie = "";
//	String id = "";

//	TextView title_text;
	TextView content_text;
	RadioButton rb_a;
	RadioButton rb_b;
	RadioButton rb_c;
	RadioButton rb_d;
	RadioButton rb_e;
	RadioGroup rg;
	Button answer_bt;
	Button next_bt;
	Button return_bt;
	LinearLayout cb_layout;
	CheckBox cb_a;
	CheckBox cb_b;
	CheckBox cb_c;
	CheckBox cb_d;
	CheckBox cb_e;

	String answer_str = "";
	String type_str = "";

//	private static final String Type = "Type", Title = "Title",ChooseA = "ChooseA",
//			ChooseB = "ChooseB",ChooseC = "ChooseC",ChooseD = "ChooseD",Answer = "Answer",
//					Colid = "Colid",Id = "Id";
	String list_id;
	String list_deptid;
	String list_title;
	String list_remark1;
	String list_remark2;
	String list_remark3;
	String list_crtime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peixun_question);

		context = this;
		resources = this.getResources();

//		Intent intent = this.getIntent();
//		type = intent.getStringExtra("type");
//		pian = intent.getStringExtra("pian");
//		zhang = intent.getStringExtra("zhang");
//		jie = intent.getStringExtra("jie");
//		id = intent.getStringExtra("id");
		Intent intent = this.getIntent();
		list_id = intent.getStringExtra("id");
		list_deptid = intent.getStringExtra("deptid");
		list_title = intent.getStringExtra("title");
		list_remark1 = intent.getStringExtra("remark1");
		list_remark2 = intent.getStringExtra("remark2");
		list_remark3 = intent.getStringExtra("remark3");
		list_crtime = intent.getStringExtra("crtime");
		
		viewInit();
		mGetQuestionDataThread questionRunnable = new mGetQuestionDataThread();
		Thread thread = new Thread(questionRunnable);
		thread.start();
		// Toast.makeText(context, "type = " + type + "\npian = " + pian +
		// "\nzhang = " + zhang + "\njie = " + jie, Toast.LENGTH_LONG).show();

	}

	private void viewInit() {
//		title_text = (TextView) this.findViewById(R.id.question_title_text);
		content_text = (TextView) this.findViewById(R.id.question_content_text);
		answer_bt = (Button) this.findViewById(R.id.question_answer_bt);
		next_bt = (Button) this.findViewById(R.id.question_next_bt);
		return_bt = (Button) this.findViewById(R.id.return_bt);
		
		rg = (RadioGroup) this.findViewById(R.id.question_rg);
		rb_a = (RadioButton) this.findViewById(R.id.question_rb_a);
		rb_b = (RadioButton) this.findViewById(R.id.question_rb_b);
		rb_c = (RadioButton) this.findViewById(R.id.question_rb_c);
		rb_d = (RadioButton) this.findViewById(R.id.question_rb_d);
		rb_e = (RadioButton) this.findViewById(R.id.question_rb_e);

		cb_layout = (LinearLayout) this
				.findViewById(R.id.question_check_layout);
		cb_a = (CheckBox) this.findViewById(R.id.question_cb_a);
		cb_b = (CheckBox) this.findViewById(R.id.question_cb_b);
		cb_c = (CheckBox) this.findViewById(R.id.question_cb_c);
		cb_d = (CheckBox) this.findViewById(R.id.question_cb_d);
		cb_e = (CheckBox) this.findViewById(R.id.question_cb_e);
		
		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		next_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rg.clearCheck();

				cb_a.setChecked(false);
				cb_b.setChecked(false);
				cb_c.setChecked(false);
				cb_d.setChecked(false);
				cb_e.setChecked(false);
				cb_a.setTextColor(Color.rgb(0, 0, 0));
				cb_b.setTextColor(Color.rgb(0, 0, 0));
				cb_c.setTextColor(Color.rgb(0, 0, 0));
				cb_d.setTextColor(Color.rgb(0, 0, 0));
				cb_e.setTextColor(Color.rgb(0, 0, 0));
				rb_a.setTextColor(Color.rgb(0, 0, 0));
				rb_b.setTextColor(Color.rgb(0, 0, 0));
				rb_c.setTextColor(Color.rgb(0, 0, 0));
				rb_d.setTextColor(Color.rgb(0, 0, 0));
				rb_e.setTextColor(Color.rgb(0, 0, 0));
				mGetQuestionDataThread questionRunnable = new mGetQuestionDataThread();
				Thread thread = new Thread(questionRunnable);
				thread.start();
			}
		});

		answer_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(context, answer_str,
				// Toast.LENGTH_SHORT).show();
				if (type_str.equals("1")) {
					String[] answer_array = answer_str.split("");
					cb_a.setTextColor(Color.rgb(255, 0, 0));
					cb_b.setTextColor(Color.rgb(255, 0, 0));
					cb_c.setTextColor(Color.rgb(255, 0, 0));
					cb_d.setTextColor(Color.rgb(255, 0, 0));
					cb_e.setTextColor(Color.rgb(255, 0, 0));
					for (int i = 0; i < answer_array.length; i++) {
						if (answer_array[i].equals("A")) {
							cb_a.setTextColor(Color.rgb(0, 255, 0));
						} else if (answer_array[i].equals("B")) {
							cb_b.setTextColor(Color.rgb(0, 255, 0));
						} else if (answer_array[i].equals("C")) {
							cb_c.setTextColor(Color.rgb(0, 255, 0));
						} else if (answer_array[i].equals("D")) {
							cb_d.setTextColor(Color.rgb(0, 255, 0));
						} else if (answer_array[i].equals("E")) {
							cb_e.setTextColor(Color.rgb(0, 255, 0));
						}
					}
					Toast.makeText(context, "正确答案 " + answer_str,
							Toast.LENGTH_SHORT).show();
				} else if (type_str.equals("0")) {
					rb_a.setTextColor(Color.rgb(255, 0, 0));
					rb_b.setTextColor(Color.rgb(255, 0, 0));
					rb_c.setTextColor(Color.rgb(255, 0, 0));
					rb_d.setTextColor(Color.rgb(255, 0, 0));
					rb_e.setTextColor(Color.rgb(255, 0, 0));
					if (answer_str.equals("A")) {
						rb_a.setTextColor(Color.rgb(0, 255, 0));
					} else if (answer_str.equals("B")) {
						rb_b.setTextColor(Color.rgb(0, 255, 0));
					} else if (answer_str.equals("C")) {
						rb_c.setTextColor(Color.rgb(0, 255, 0));
					} else if (answer_str.equals("D")) {
						rb_d.setTextColor(Color.rgb(0, 255, 0));
					} else if (answer_str.equals("E")) {
						rb_e.setTextColor(Color.rgb(0, 255, 0));
					}
					Toast.makeText(context, "正确答案 " + answer_str,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				ArrayList<HashMap<String, Object>> remoteWindowItem = (ArrayList<HashMap<String, Object>>) msg.obj;
				HashMap<String, Object> res_hash = remoteWindowItem.get(0);
				String title_type = "";
				if(res_hash.get("Type").toString().equals("0")){
					title_type = "单选题";
				}else{
					title_type = "多选题";
				}
				content_text.setText(title_type + "\n\n" + res_hash.get("Title").toString());
				answer_str = res_hash.get("Answer").toString();

				try {
					String[] choose_array = res_hash.get("Answer").toString()
							.split(",");
					type_str = res_hash.get("Type").toString();
					if (type_str.equals("0")) {
						rg.setVisibility(1);
						cb_layout.setVisibility(8);
						rb_a.setText("A. " + res_hash.get("ChooseA").toString());
						rb_b.setText("B. " + res_hash.get("ChooseB").toString());
						rb_c.setText("C. " + res_hash.get("ChooseC").toString());
						rb_d.setText("D. " + res_hash.get("ChooseD").toString());

//						if (choose_array.length == 4) {
							rb_e.setVisibility(8);
//						} else {
//							rb_e.setVisibility(1);
//							rb_e.setText("E. " + choose_array[4]);
//						}
					} else if (type_str.equals("1")) {
						rg.setVisibility(8);
						cb_layout.setVisibility(1);
						cb_a.setText("A. " + res_hash.get("ChooseA").toString());
						cb_b.setText("B. " + res_hash.get("ChooseB").toString());
						cb_c.setText("C. " + res_hash.get("ChooseC").toString());
						cb_d.setText("D. " + res_hash.get("ChooseD").toString());
//						if (choose_array.length == 4) {
							cb_e.setVisibility(8);
//						} else {
//							cb_e.setVisibility(1);
//							cb_e.setText("E. " + choose_array[4]);
//						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			} catch (Exception e) {
				// TODO: handle exception
				Log.e("Question threadMessageHandler error : ", e.toString());
			}
		}
	};

	private class mGetQuestionDataThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String qurl = MessengerService.URL;
			String qmethodname = "";
			qmethodname = MessengerService.METHOD_GetTikuRandomById;
			String qnamespace = MessengerService.NAMESPACE;
			String qsoapaction = qnamespace + "/" + qmethodname;

			SoapObject rpc = new SoapObject(qnamespace, qmethodname);
//			rpc.addProperty("title", title);
			rpc.addProperty("colid", list_id);
//			if (type.equals("pian")) {
//				rpc.addProperty("c_id", id);
//				rpc.addProperty("pian", pian);
//			} else if (type.equals("zhang")) {
//				rpc.addProperty("c_id", id);
//				rpc.addProperty("pian", pian);
//				rpc.addProperty("zhang", zhang);
//			} else if (type.equals("jie")) {
//				rpc.addProperty("c_id", id);
//				rpc.addProperty("pian", pian);
//				rpc.addProperty("zhang", zhang);
//				rpc.addProperty("jie", jie);
//			}
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(qurl);
			ht.debug = true;
			try {
				ht.call(qsoapaction, envelope);
				ArrayList<HashMap<String, Object>> remoteWindowItem = new ArrayList<HashMap<String, Object>>();
				SoapObject journal = (SoapObject) envelope.bodyIn;

				SoapObject soapchilds = (SoapObject) journal.getProperty(0);
				SoapObject soapchildsson = (SoapObject) soapchilds
						.getProperty(0);
				String Type = soapchildsson.getProperty("Type").toString();
				String Title = soapchildsson.getProperty("Title").toString();
				String ChooseA = soapchildsson.getProperty("ChooseA").toString();
				String ChooseB = soapchildsson.getProperty("ChooseB").toString();
				String ChooseC = soapchildsson.getProperty("ChooseC").toString();
				String ChooseD = soapchildsson.getProperty("ChooseD").toString();
				String Answer = soapchildsson.getProperty("Answer").toString();
				String Colid = soapchildsson.getProperty("Colid").toString();
				String Id = soapchildsson.getProperty("Id").toString();

				HashMap<String, Object> mapdevinfo = new HashMap<String, Object>();
				mapdevinfo.put("Type", Type);
				mapdevinfo.put("Title", Title);
				mapdevinfo.put("ChooseA", ChooseA);
				mapdevinfo.put("ChooseB", ChooseB);
				mapdevinfo.put("ChooseC", ChooseC);
				mapdevinfo.put("ChooseD", ChooseD);
				mapdevinfo.put("Answer", Answer);
				mapdevinfo.put("Colid", Colid);
				mapdevinfo.put("Id", Id);
				remoteWindowItem.add(mapdevinfo);

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
