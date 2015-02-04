package com.lnpdit.chatuidemo.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import lnpdit.stategrid.informatization.data.MessengerService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;
import com.sytm.bean.TelBookModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewsPhotoActivity extends Activity {

	Context context;
	Button return_bt;
	Button send_bt;
	EditText title_edit;
	// String titleString;
	EditText content_edit;
	// String conString;
	ImageView camera_img;
	TextView txt;
	String uploadBuffer = "";
	String picPath = "";
	Bitmap bitmap;
	public static String picName;

	public static final int TO_SELECT_PHOTO = 3;
	// 上传初始化
	private static final String TAG = "uploadImage";
	
	String userid;
	String deptid;
	String colid;

	// 去上传文件
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_photo);
		context = this;
		// Intent intent = this.getIntent();

		viewInit();
		SharedPreferences sharedPreferences = getSharedPreferences(
				"user_info", Activity.MODE_PRIVATE);
		userid = sharedPreferences.getString("Id", "");
		deptid = sharedPreferences.getString("DeptId", "");

	}

	private void viewInit() {
		return_bt = (Button) this.findViewById(R.id.return_bt);
		send_bt = (Button) this.findViewById(R.id.send_bt);
		txt = (TextView) this.findViewById(R.id.camera_txt);
		camera_img = (ImageView) this.findViewById(R.id.camera_img);
		// camera_img.setClickable(true);
		camera_img.isClickable();
		title_edit = (EditText) this.findViewById(R.id.report_title);
		content_edit = (EditText) this.findViewById(R.id.report_content);
		// progressbar = (ProgressBar) this.findViewById(R.id.progressbar);

		send_bt.setOnClickListener(btnListener);
		return_bt.setOnClickListener(btnListener);
		camera_img.setOnClickListener(btnListener);

	}

	private void sendMsg() {
		String title = title_edit.getText().toString();
		String content = content_edit.getText().toString();
		if (title.equals("")) {
			Toast.makeText(context, "发送标题不能为空", Toast.LENGTH_SHORT).show();
			return;
		} else if (content.equals("")) {
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
//				uploadBuffer = new String(Base64.encode(baos.toByteArray(),
//						Base64.DEFAULT));
				uploadBuffer = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			mSendDataThread runnable = new mSendDataThread();
			Thread thread = new Thread(runnable);
			thread.start();
		}
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
			case R.id.send_bt:
				// Toast.makeText(context, rcv_id_text.getText().toString(),
				// Toast.LENGTH_SHORT).show();
				sendMsg();
				break;
			case R.id.return_bt:
				finish();
				break;
			case R.id.camera_img:
				Intent intent1 = new Intent(NewsPhotoActivity.this,
						SelectPicActivity.class);
				startActivityForResult(intent1, TO_SELECT_PHOTO);
				// startActivity(intent1);
				break;
			default:
				break;
			}
		}
	};

	private class mSendDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// tdd.clearcontact();
			String curl = MessengerService.URL_WITHOUT_WSDL;
			String cmethodname = MessengerService.METHOD_NewsAdd;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
//			rpc.addProperty("Title", title_edit.getText().toString());
//			rpc.addProperty("Content", content_edit.getText().toString());
//			rpc.addProperty("PhotoPath", txt.getText().toString());
//			rpc.addProperty("Photo", uploadBuffer);
			rpc.addProperty("title", title_edit.getText().toString());
			rpc.addProperty("userid", userid);
			rpc.addProperty("deptid", deptid);
			rpc.addProperty("colid", 1);
			rpc.addProperty("content", content_edit.getText().toString());
			rpc.addProperty("images", uploadBuffer);
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
				res = contactlist.getProperty("NewsAddResult").toString();
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
			if (res.equals("success")) {
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

		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
			// imageView不设null, 第一次上传成功后，第二次在选择上传的时候会报错。
			camera_img.setImageBitmap(null);
			picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "最终选择的图片=" + picPath);
			txt.setText("文件路径" + picPath);
			String[] str = picPath.split("/");
			String strr = str[str.length - 1];
//			title_edit.setText(picPath);
			picName = strr;
			Options ops = new Options();
			// ops.in
			ops.inPreferredConfig = Bitmap.Config.RGB_565;

			ops.inPurgeable = true;

			ops.inInputShareable = true;
			Bitmap bm = BitmapFactory.decodeFile(picPath, ops);

			camera_img.setImageBitmap(bm);// 将图片显示在ImageView里
		}
		super.onActivityResult(requestCode, resultCode, data);

	}
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// Toast.makeText(context, "img return", Toast.LENGTH_SHORT).show();
	// if (resultCode == Activity.RESULT_OK) {
	//
	// String sdStatus = Environment.getExternalStorageState();
	// if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
	// Log.v("TestFile", "SD card is not avaiable/writeable right now.");
	// return;
	// }
	// // list.clear();
	// // list = (List<TelBookModel>) data.getSerializableExtra("list");
	// //
	// // String selectedStr = "";
	// // String selectedIdStr = "";
	// // for (int i = 0; i < list.size(); i++) {
	// // String name = list.get(i).getName();
	// // int id = list.get(i).getId();
	// //
	// // selectedStr = selectedStr + "," + name;
	// // selectedIdStr = selectedIdStr + "," + id;
	// // }
	//
	// // rcv_text.setText("收件人：" + selectedStr);
	// // rcv_id_text.setText(selectedIdStr);
	//
	// camera_img.setVisibility(MessengerService.VISIBILITY_TRUE);
	// // camera_bt.setVisibility(MessengerService.VISIBILITY_FALSE);
	//
	// Bundle bundle = data.getExtras();
	// bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
	// FileOutputStream b = null;
	// File file = new File("/sdcard/newsmobileImg/");
	// file.mkdirs();// 创建文件夹
	//
	// SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	// Long time = new Long(445555555);
	// String d = format.format(time);
	// picPath = "/sdcard/newsmobileImg/" + d + ".jpg";
	//
	// try {
	// b = new FileOutputStream(picPath);
	// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// b.flush();
	// b.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// camera_img.setImageBitmap(bitmap);// 将图片显示在ImageView里
	// }
	// }

}
