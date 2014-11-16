package com.lnpdit.chatuidemo.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class JobBackAddActivity extends Activity{
	
	Context context;
	String userid;
	
	TextView rcv_text;
	TextView rcv_id_text;
	Button choose_bt;
	EditText content_edit;
	Button send_bt;
	Button return_bt;

	Button camera_bt;
	ImageView camera_img;

	private final int MUTI_CHOICE_DIALOG = 1;
	boolean[] selected;
	String[] contact_array;
	String[] contact_id_array;
	int contact_num = 0;
	Bitmap bitmap;
	String picPath = "";
	String uploadBuffer = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobbackadd);
		
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
		content_edit = (EditText) this.findViewById(R.id.content_edit);
		send_bt = (Button) this.findViewById(R.id.send_bt);
		return_bt = (Button) this.findViewById(R.id.return_bt);
		
		camera_bt = (Button) this.findViewById(R.id.camera_bt);
		camera_img = (ImageView) this.findViewById(R.id.camera_img);

		choose_bt.setOnClickListener(btnListener);
		send_bt.setOnClickListener(btnListener);
		return_bt.setOnClickListener(btnListener);
		camera_bt.setOnClickListener(btnListener);
	}

	private void sendMsg() {
		String content = content_edit.getText().toString();
		if (content.equals("")) {
			Toast.makeText(context, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else {
			uploadBuffer = null;
			try {
				FileInputStream fis = new FileInputStream(picPath);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = fis.read(buffer)) >= 0) {
					baos.write(buffer, 0, count);
				}
				uploadBuffer = new String(Base64.encode(baos.toByteArray(),
						Base64.DEFAULT));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
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
				if(msg.arg1 == 1){
					Toast.makeText(context, "信息发送成功", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(context, "信息发送失败", Toast.LENGTH_SHORT).show();
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
			case R.id.camera_bt:
				try {
					if (Environment.MEDIA_MOUNTED.equals(Environment
							.getExternalStorageState())) {
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(intent, 1);
					} else {
						Toast.makeText(context, "未检测到SD卡，无法使用此功能。",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "未检测到相机，无法使用此功能",
							Toast.LENGTH_SHORT).show();
				}
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
			String cmethodname = MessengerService.METHOD_JobBackAdd;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
			rpc.addProperty("Content", content_edit.getText().toString());
			rpc.addProperty("ToUser", rcv_id_text.getText().toString());
			rpc.addProperty("FromUser", userid);
			rpc.addProperty("photo", uploadBuffer);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(curl);
			ht.debug = true;
			String res = "";
			try {
				ht.call(csoapaction, envelope);
				SoapObject contactlist = (SoapObject) envelope.bodyIn;
				res = contactlist.getProperty("JobBackAddResult").toString();
//				for (int i = 0; i < contactlist.getPropertyCount(); i++) {
//					SoapObject soapchilds = (SoapObject) contactlist.getProperty(i);
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
				e.printStackTrace(); 
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
 
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.v("TestFile", "SD card is not avaiable/writeable right now.");
				return;
			}
			camera_img.setVisibility(MessengerService.VISIBILITY_TRUE);
			camera_bt.setVisibility(MessengerService.VISIBILITY_FALSE);

			Bundle bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			FileOutputStream b = null;
			File file = new File("/sdcard/newsmobileImg/");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			file.mkdirs();// 创建文件夹

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			Long time = new Long(445555555);
			String d = format.format(time);
			picPath = "/sdcard/newsmobileImg/" + d + ".jpg";

			try {
				b = new FileOutputStream(picPath);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			camera_img.setImageBitmap(bitmap);// 将图片显示在ImageView里
		}
	}


}
