package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;
import com.sytm.bean.TelBookModel;
import com.sytm.tmkq.ReportDepContactsActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherAddActivity extends Activity {

	Context context;
	String userid;

	TextView rcv_text;
	TextView rcv_id_text;
	Button choose_bt;
	EditText riqi_edit;
	EditText wendu_edit;
	EditText shidu_edit;
	EditText fengxiang_edit;
	EditText fengli_edit;
	RadioGroup yujing_group;
	RadioButton yellowButton;
	RadioButton orangeButton;
	RadioButton redButton;
	RadioButton blueButton;

	Button send_bt;
	Button return_bt;

	String alarmLevel = "";

	private final int MUTI_CHOICE_DIALOG = 1;
	boolean[] selected;
	String[] contact_array;
	String[] contact_id_array;
	int contact_num = 0;
	private int SCAN = 1;
	private List<TelBookModel> list = new ArrayList<TelBookModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_add);

		context = this;
		Intent intent = this.getIntent();
		userid = intent.getStringExtra("userid");

		viewInit();

		ToDoDB tdd = new ToDoDB(context);
		Cursor cursor = tdd.selectcontact();
		contact_num = cursor.getCount();
		if (contact_num > 0) {
			cursor.moveToFirst();
			selected = new boolean[contact_num];
			contact_array = new String[contact_num];
			contact_id_array = new String[contact_num];
			for (int i = 0; i < contact_num; i++) {
				selected[i] = false;
				contact_array[i] = cursor.getString(2);// name
				contact_id_array[i] = cursor.getString(1);// id

				cursor.moveToNext();
			}
		}
	}

	private void viewInit() {
		rcv_text = (TextView) this.findViewById(R.id.rcv_text);
		rcv_id_text = (TextView) this.findViewById(R.id.rcv_id_text);
		choose_bt = (Button) this.findViewById(R.id.choose_contact);
		riqi_edit = (EditText) this.findViewById(R.id.riqi_edit);
		wendu_edit = (EditText) this.findViewById(R.id.wendu_edit);
		shidu_edit = (EditText) this.findViewById(R.id.shidu_edit);
		fengxiang_edit = (EditText) this.findViewById(R.id.fengxiang_edit);
		fengli_edit = (EditText) this.findViewById(R.id.fengli_edit);
		yujing_group = (RadioGroup) this.findViewById(R.id.yujing_edit);

		yellowButton = (RadioButton) this.findViewById(R.id.radio0);
		orangeButton = (RadioButton) this.findViewById(R.id.radio1);
		redButton = (RadioButton) this.findViewById(R.id.radio2);
		blueButton = (RadioButton) this.findViewById(R.id.radio3);

		send_bt = (Button) this.findViewById(R.id.send_bt);
		return_bt = (Button) this.findViewById(R.id.return_bt);

		choose_bt.setOnClickListener(btnListener);
		send_bt.setOnClickListener(btnListener);
		return_bt.setOnClickListener(btnListener);

		yujing_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (yellowButton.getId() == checkedId) {
					alarmLevel = "1";
				} else if (orangeButton.getId() == checkedId) {
					alarmLevel = "2";
				} else if (redButton.getId() == checkedId) {
					alarmLevel = "3";
				} else if (blueButton.getId() == checkedId) {
					alarmLevel = "4";
				}
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		list.clear();
		list = (List<TelBookModel>) data.getSerializableExtra("list");

		String selectedStr = "";
		String selectedIdStr = "";
		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i).getName();
			int id = list.get(i).getId();

			if(i!=0){				
				selectedStr = selectedStr + "," + name;
				selectedIdStr = selectedIdStr + "," + id;
			}else{
				selectedStr = name;
				selectedIdStr = String.valueOf(id);
			}
		}

		rcv_text.setText("收件人：" + selectedStr);
		rcv_id_text.setText(selectedIdStr);
	}

	private void sendMsg() {
		String riqi = riqi_edit.getText().toString();
		String wendu = wendu_edit.getText().toString();
		String shidu = shidu_edit.getText().toString();
		String fengxiang = fengxiang_edit.getText().toString();
		String fengli = fengli_edit.getText().toString();
		String yujing = fengli_edit.getText().toString();
		if (riqi.equals("")) {
			Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else if (wendu.equals("")) {
			Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else if (shidu.equals("")) {
			Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else if (fengxiang.equals("")) {
			Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else if (fengli.equals("")) {
			Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else if (yujing.equals("")) {
			Toast.makeText(context, "请选择预警等级", Toast.LENGTH_SHORT).show();
			return;
		} else {
			mSendDataThread runnable = new mSendDataThread();
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}

	@SuppressWarnings("deprecation")
	private void showContact() {
		showDialog(MUTI_CHOICE_DIALOG);
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.arg1 == 1) {
					Toast.makeText(context, "信息发送成功", Toast.LENGTH_SHORT)
							.show();
					finish();
				} else {
					Toast.makeText(context, "信息发送失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private android.view.View.OnClickListener btnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.choose_contact:
				// showContact();
				Intent intent = new Intent(WeatherAddActivity.this,
						ReportDepContactsActivity.class);
				startActivityForResult(intent, 10);
				break;
			case R.id.send_bt:
				// Toast.makeText(context, rcv_id_text.getText().toString(),
				// Toast.LENGTH_SHORT).show();
				sendMsg();
				break;
			case R.id.return_bt:
				finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case MUTI_CHOICE_DIALOG:

			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请选择联系人");
			DialogInterface.OnMultiChoiceClickListener mutiListener = new DialogInterface.OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialogInterface, int which,
						boolean isChecked) {
					selected[which] = isChecked;
				}
			};
			builder.setMultiChoiceItems(contact_array, selected, mutiListener);
			DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					String selectedStr = "";
					String selectedIdStr = "";
					for (int i = 0; i < selected.length; i++) {
						if (selected[i] == true) {
							if (selectedStr.trim().equals("")) {
								selectedStr = contact_array[i];
							} else {
								selectedStr = selectedStr + ","
										+ contact_array[i];
							}
							if (selectedIdStr.trim().equals("")) {
								selectedIdStr = contact_id_array[i];
							} else {
								selectedIdStr = selectedIdStr + ","
										+ contact_id_array[i];
							}
						}
					}
					rcv_text.setText("收件人：" + selectedStr);
					rcv_id_text.setText(selectedIdStr);
				}
			};
			builder.setPositiveButton("确定", btnListener);
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	@SuppressLint("HandlerLeak")
	private class mSendDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// tdd.clearcontact();
			String curl = MessengerService.URL_WITHOUT_WSDL;
			String cmethodname = MessengerService.METHOD_WeatherAdd;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
			rpc.addProperty("Wdate", riqi_edit.getText().toString());
			rpc.addProperty("Temperature", wendu_edit.getText().toString());
			rpc.addProperty("Humidity", shidu_edit.getText().toString());
			rpc.addProperty("WindDIR", fengxiang_edit.getText().toString());
			rpc.addProperty("WindSpeed", fengli_edit.getText().toString());
			rpc.addProperty("AlarmLevel", alarmLevel);
			// rpc.addProperty("AlarmLevel",
			// yujing_edit.getContext().toString());
			rpc.addProperty("Userid", userid);
			rpc.addProperty("toUser", rcv_id_text.getText().toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(curl);
			ht.debug = true;
			String res = "";
			try {
				ht.call(csoapaction, envelope);
				// SoapFault contactlist = (SoapFault) envelope.bodyIn;
				SoapObject contactlist = (SoapObject) envelope.bodyIn;
				res = contactlist.getProperty("WeatherAddResult").toString();
				// for (int i = 0; i < contactlist.getPropertyCount(); i++) {
				// SoapObject soapchilds = (SoapObject) contactlist
				// .getProperty(i);
				// res = soapchilds.getProperty("MASResult").toString();
				// res = contactlist.toString();
				// }

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("send weather error", e.toString());
			}
			Message msg = new Message();
			if (!res.equals("0")) {
				msg.arg1 = 1;
			} else {
				msg.arg1 = 0;
			}
			threadMessageHandler.sendMessage(msg);
		}
	}

}
