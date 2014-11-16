package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;
import lnpdit.stategrid.informatization.tools.MyLetterListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MASActivity extends Activity implements OnClickListener{

	Context context;

	TextView rcv_text;
	TextView rcv_id_text;
	Button choose_bt;
	EditText content_edit;
	Button send_bt;
	Button return_bt;

	private final int MUTI_CHOICE_DIALOG = 1;
	boolean[] selected;
	String[] contact_array;
	String[] contact_id_array;
	int contact_num = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);

		context = this;

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
		content_edit = (EditText) this.findViewById(R.id.content_edit);
		send_bt = (Button) this.findViewById(R.id.send_bt);
		return_bt = (Button) this.findViewById(R.id.return_bt);

		choose_bt.setOnClickListener(this);
		send_bt.setOnClickListener(this);
		return_bt.setOnClickListener(this);
	}

	private void sendMsg() {
		String content = content_edit.getText().toString();
		if (content.equals("")) {
			Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else {
			mSendDataThread runnable = new mSendDataThread();
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}

	private void showContact() {
		showDialog(MUTI_CHOICE_DIALOG);
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if(msg.arg1 == 1){
					Toast.makeText(context, "信息发送成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "信息发送失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.choose_contact:
				showContact();
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
			String cmethodname = MessengerService.METHOD_MAS;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
			rpc.addProperty("content", content_edit.getText().toString());
			rpc.addProperty("tele", rcv_id_text.getText().toString());
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
				SoapObject contactlist = (SoapObject) envelope.bodyIn;
				res = contactlist.getProperty("MASResult").toString();
//				for (int i = 0; i < contactlist.getPropertyCount(); i++) {
//					SoapObject soapchilds = (SoapObject) contactlist
//							.getProperty(i);
//					res = soapchilds.getProperty("MASResult").toString();
//				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
			}
			Message msg = new Message();
			if (res.equals("true")) {
				msg.arg1 = 1;
			} else {
				msg.arg1 = 0;
			}
			threadMessageHandler.sendMessage(msg);
		}
	}

}
