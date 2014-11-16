package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;
import lnpdit.stategrid.informatization.tools.MyLetterListView;
import lnpdit.stategrid.informatization.tools.MyLetterListView.OnTouchingLetterChangedListener;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContactActivity extends Activity {

	Context context;
	Resources resources;
	private BaseAdapter adapter;
	private ListView personList;
	private Button return_bt;
	private TextView overlay;
	private ProgressBar progressbar;
	private MyLetterListView letterListView;
	private AsyncQueryHandler asyncQuery;
	private static final String ID = "id", NAME = "name",
			MOBILEPHONE = "mobilephone", PHONE = "phone", DEPTID = "deptid",
			MAIL = "mail", DEPTNAME = "deptname", SORT_KEY = "sort_key";
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Handler handler;
	private OverlayThread overlayThread;
	String userId;
	private ProgressDialog dialog;
	
	String dept_Id;
	String dept_Grade;
	String dept_Class;
	String dept_Remark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		context = this;
		resources = this.getResources();

		Intent intent = this.getIntent();
		dept_Id = intent.getStringExtra("Id");
		dept_Grade = intent.getStringExtra("Grade");
		dept_Class = intent.getStringExtra("Class");
		dept_Remark = intent.getStringExtra("Remark");

		personList = (ListView) findViewById(R.id.list_view_contact);
		return_bt = (Button) this.findViewById(R.id.return_bt);
		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		progressbar = (ProgressBar) this.findViewById(R.id.progressbar);
		progressbar.setVisibility(8);
		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
		
		ToDoDB tdd = new ToDoDB(context);
		Cursor cursor = tdd.selectcontact();
		if(cursor.getCount()==0){
			dialog = new ProgressDialog(ContactActivity.this);
			dialog.setMessage("正在更新联系人列表,请稍等.");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
			tdd.clearcontact();
			mGetContactDataThread mThread = new mGetContactDataThread();
			Thread thread = new Thread(mThread);
			thread.start();
			cursor.close();
			tdd.close();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { "_id", "display_name", "data1", "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				dialog.dismiss();
				letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
				letterListView
						.setOnTouchingLetterChangedListener(new LetterListViewListener());

				asyncQuery = new MyAsyncQueryHandler(getContentResolver());
				alphaIndexer = new HashMap<String, Integer>();
				handler = new Handler();
				overlayThread = new OverlayThread();
				initOverlay();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			List<ContentValues> list = new ArrayList<ContentValues>();
			ToDoDB tdd = new ToDoDB(context);
			Cursor cursor_contact = tdd.selectcontact();
			try {
				if (cursor_contact.getCount() != 0) {
					cursor_contact.moveToFirst();
					for (int i = 0; i < cursor_contact.getCount(); i++) {
						if(cursor_contact.getString(3).equals(dept_Id)){
							String name = cursor_contact.getString(2);
							String name_pinyin = getPingYin(name);
							ContentValues cv = new ContentValues();
							cv.put(ID, cursor_contact.getString(1));
							cv.put(NAME, cursor_contact.getString(2));
							cv.put(DEPTID, cursor_contact.getString(3));
							cv.put(MOBILEPHONE, cursor_contact.getString(4));
							cv.put(PHONE, cursor_contact.getString(5));
							cv.put(MAIL, cursor_contact.getString(6));
							cv.put(DEPTNAME, cursor_contact.getString(7));
							cv.put(SORT_KEY, name_pinyin);
							list.add(cv);
						}
						cursor_contact.moveToNext();
					}
					if (list.size() > 0) {
						setAdapter(list);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void setAdapter(List<ContentValues> list) {
		adapter = new ListAdapter(this, list);
		personList.setAdapter(adapter);
		personList.notify();
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<ContentValues> list;

		public ListAdapter(Context context, List<ContentValues> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {

				String currentStr = getAlpha(list.get(i).getAsString(SORT_KEY));

				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getAsString(SORT_KEY)) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getAsString(SORT_KEY));
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.list_item_contact, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number = (TextView) convertView
						.findViewById(R.id.number);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ContentValues cv = list.get(position);
			String user_name = cv.getAsString(NAME);
			String user_number = cv.getAsString(PHONE);
			holder.name.setText(user_name);
			holder.number.setText(cv.getAsString(MOBILEPHONE));
			String user_id_list = cv.getAsString(ID);
			String currentStr = getAlpha(list.get(position).getAsString(
					SORT_KEY));
			String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
					position - 1).getAsString(SORT_KEY)) : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			convertView.setOnClickListener(new mmContactAdapterListener(
					position, user_id_list, user_name, cv.getAsString(DEPTID),
					cv.getAsString(MOBILEPHONE), cv.getAsString(PHONE), cv
							.getAsString(MAIL), cv.getAsString(DEPTNAME)));
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
			TextView number;
		}

		class mmContactAdapterListener implements OnClickListener {
			private int position;
			private String mm_id;
			private String mm_name;
			private String mm_deptid;
			private String mm_mobilephone;
			private String mm_phone;
			private String mm_mail;
			private String mm_deptname;

			public mmContactAdapterListener(int pos, String _id, String _name,
					String _deptid, String _mobilephone, String _phone,
					String _mail, String _deptname) {
				// TODO Auto-generated constructor stub
				position = pos;
				this.mm_id = _id;
				this.mm_name = _name;
				this.mm_deptid = _deptid;
				this.mm_mobilephone = _mobilephone;
				this.mm_phone = _phone;
				this.mm_mail = _mail;
				this.mm_deptname = _deptname;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent_contact = new Intent();
				// intent_contact
				// .setAction(MessengerService.CONTACT_CHOOSE_CONTACT);
				// intent_contact.putExtra("USRID", mm_id);
				// intent_contact.putExtra("USRNAME", mm_name);
				// intent_contact.putExtra("USRTEL", mm_tel);
				// intent_contact.putExtra("USRTYPE", mm_type);
				// context.sendBroadcast(intent_contact);
				// finish();
				Intent intent = new Intent();
				intent.setClass(context, ContactDisplayActivity.class);
				intent.putExtra("id", mm_id);
				intent.putExtra("name", mm_name);
				intent.putExtra("deptid", mm_deptid);
				intent.putExtra("mobilephone", mm_mobilephone);
				intent.putExtra("phone", mm_phone);
				intent.putExtra("mail", mm_mail);
				intent.putExtra("deptname", mm_deptname);
				startActivity(intent);
			}

		}

	}

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	public void refreshContact(View v) {
		try {

			dialog = new ProgressDialog(ContactActivity.this);
			dialog.setMessage("正在更新联系人列表,请稍等...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
			ToDoDB tdd = new ToDoDB(context);
			tdd.clearcontact();
			mGetContactDataThread mThread = new mGetContactDataThread();
			Thread thread = new Thread(mThread);
			thread.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressLint("HandlerLeak")
	private class mGetContactDataThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ToDoDB tdd = new ToDoDB(context);
			// tdd.clearcontact();
			String curl = MessengerService.URL_WITHOUT_WSDL;
			String cmethodname = MessengerService.METHOD_GetAddressBookList;
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
				SoapObject soapchilds1 = (SoapObject) contactlist.getProperty(0);
				SoapObject soapchilds2 = (SoapObject) soapchilds1.getProperty(1);
//				SoapObject soapchilds3 = (SoapObject) soapchilds2.getProperty(0);
				for (int i = 0; i < contactlist.getPropertyCount(); i++) {
					SoapObject soapchilds = (SoapObject) soapchilds2
							.getProperty(i);
					for (int j = 0; j < soapchilds.getPropertyCount(); j++) {
						SoapObject soapchildsson = (SoapObject) soapchilds
								.getProperty(j);

						String Id = soapchildsson.getProperty("Id").toString();
						String Name = soapchildsson.getProperty("Name")
								.toString();
						String Dept_id = soapchildsson.getProperty("Dept_id")
								.toString();
						String Mobilephone = soapchildsson.getProperty(
								"mobilephone").toString();
						String Phone = soapchildsson.getProperty("phone")
								.toString();
						String Mail = soapchildsson.getProperty("mail")
								.toString();
//						String Dept_name = soapchildsson.getProperty(
//								"Dept_name").toString();

						ContentValues values = new ContentValues();
						values.put(tdd.CONTACT_WEBID, Id);
						values.put(tdd.CONTACT_NAME, Name);
						values.put(tdd.CONTACT_DEPTID, Dept_id);
						values.put(tdd.CONTACT_MOBILEPHONE, Mobilephone);
						values.put(tdd.CONTACT_PHONE, Phone);
						values.put(tdd.CONTACT_MAIL, Mail);
//						values.put(tdd.CONTACT_DEPTNAME, Dept_name);
						tdd.insertcontact(values);

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
			msg.arg1 = 1;
			threadMessageHandler.sendMessage(msg);

		}
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);

				handler.postDelayed(overlayThread, 500);
			}
		}

	}

	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);

		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	private String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();//把字符串转化成字符数组
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				// \\u4E00是unicode编码，判断是不是中文
				if (java.lang.Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					// 将汉语拼音的全拼存到temp数组
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					//  取拼音的第一个读音
					output += temp[0];
				}
				// 大写字母转化成小写字母
				else if (input[i] > 'A' && input[i] < 'Z') {
					output += java.lang.Character.toString(input[i]);
					output = output.toLowerCase();
				}
				output += java.lang.Character.toString(input[i]);
			}
		} catch (Exception e) {
			Log.e("Exception", e.toString());
		}
		return output;
	}

}
