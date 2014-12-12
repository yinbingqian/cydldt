package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ContactListSearchActivity extends Activity {

	Context context;
	Resources resources;
	private BaseAdapter adapter;
	private ListView personList;
	private Button return_bt;
	private EditText put_search;
	private ProgressBar progressbar;
	private static final String ID = "id", NAME = "name",
			PHONENO1 = "phoneNo1", PHONENO2 = "phoneNo2",PHONENO3 = "phoneNo3",PHONENO4 = "phoneNo4",
			PHONENAME1 = "phoneName1",PHONENAME2 = "phoneName2",PHONENAME3 = "phoneName3",PHONENAME4 = "phoneName4",
			DEPTID = "deptid",MAIL = "mail", DEPTNAME = "deptname";
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
		setContentView(R.layout.activity_contact_seach);
		context = this;
		resources = this.getResources();

		Intent intent = this.getIntent();
		dept_Id = intent.getStringExtra("Id");
		dept_Grade = intent.getStringExtra("Grade");
		dept_Class = intent.getStringExtra("Class");
		dept_Remark = intent.getStringExtra("Remark");

		personList = (ListView) findViewById(R.id.list_view_contact);
		put_search = (EditText) findViewById(R.id.serveredit);
		return_bt = (Button) this.findViewById(R.id.left);
		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		put_search.addTextChangedListener(new MyaddTextChangedListener());
		progressbar = (ProgressBar) this.findViewById(R.id.progressbar);
		progressbar.setVisibility(8);

		// asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		ToDoDB tdd = new ToDoDB(context);
		Cursor cursor = tdd.selectcontact();
		if (cursor.getCount() == 0) {
			dialog = new ProgressDialog(ContactListSearchActivity.this);
			dialog.setMessage("正在更新联系人列表,请稍等.");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
			tdd.clearcontact();
			mGetContactAllDataThread mThread = new mGetContactAllDataThread();
			Thread thread = new Thread(mThread);
			thread.start();
			cursor.close();
			tdd.close();
		}
		mGetContactDataThread mThread = new mGetContactDataThread();
		Thread thread = new Thread(mThread);
		thread.start();
	}

	Handler threadMessageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.arg1 == 2) {
					dialog.dismiss();
				} else {
					// dialog.dismiss();
					// asyncQuery = new
					// MyAsyncQueryHandler(getContentResolver());
					List<ContentValues> list = (List<ContentValues>) msg.obj;
					ListAdapter listadapter = new ListAdapter(context, list);
					personList.setAdapter(listadapter);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	class MyaddTextChangedListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@SuppressLint("DefaultLocale")
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			List<ContentValues> list = new ArrayList<ContentValues>();
			ToDoDB tdd = new ToDoDB(context);
			try {
				Pattern p = Pattern.compile("[0-9]*");
				Matcher m = p.matcher(put_search.getText().toString());
				if (m.matches()) {
					Cursor cursor_contact1 = tdd.selectmobile();
					if (cursor_contact1.getCount() != 0) {
						cursor_contact1.moveToFirst();
						for (int i = 0; i < cursor_contact1.getCount(); i++) {
							if (cursor_contact1.getString(5).contains(
									put_search.getText().toString())) {
								ContentValues cv = new ContentValues();
								cv.put(ID, cursor_contact1.getString(0));
								cv.put(NAME, cursor_contact1.getString(1));
								cv.put(MAIL, cursor_contact1.getString(2));
								cv.put(DEPTID, cursor_contact1.getString(3));
								cv.put(PHONENO1,
										cursor_contact1.getString(5));
								cv.put(PHONENO2,
										cursor_contact1.getString(6));
								cv.put(PHONENO3,
										cursor_contact1.getString(7));
								cv.put(PHONENO4,
										cursor_contact1.getString(8));
								cv.put(PHONENAME1, cursor_contact1.getString(9));
								cv.put(PHONENAME2, cursor_contact1.getString(10));
								cv.put(PHONENAME3, cursor_contact1.getString(11));
								cv.put(PHONENAME4, cursor_contact1.getString(12));
								list.add(cv);
							}
							cursor_contact1.moveToNext();
						}
						if (list.size() > 0) {
							// setAdapter(list);
							ListAdapter listadapter = new ListAdapter(context,
									list);
							personList.setAdapter(listadapter);
						}
					}
				} else {
					
					Cursor cursor_contact = tdd.selectcontact();
					if (cursor_contact.getCount() != 0) {
						cursor_contact.moveToFirst();
						for (int i = 0; i < cursor_contact.getCount(); i++) {
							if (cursor_contact.getString(1).contains(
											put_search.getText().toString())) {
								ContentValues cv = new ContentValues();
								cv.put(ID, cursor_contact.getString(0));
								cv.put(NAME, cursor_contact.getString(1));
								cv.put(MAIL, cursor_contact.getString(2));
								cv.put(DEPTID, cursor_contact.getString(3));
								cv.put(PHONENO1,
										cursor_contact.getString(5));
								cv.put(PHONENO2,
										cursor_contact.getString(6));
								cv.put(PHONENO3,
										cursor_contact.getString(7));
								cv.put(PHONENO4,
										cursor_contact.getString(8));
								cv.put(PHONENAME1, cursor_contact.getString(9));
								cv.put(PHONENAME2, cursor_contact.getString(10));
								cv.put(PHONENAME3, cursor_contact.getString(11));
								cv.put(PHONENAME4, cursor_contact.getString(12));
								list.add(cv);
							}
							cursor_contact.moveToNext();
						}
						if (list.size() > 0) {
							// setAdapter(list);
							ListAdapter listadapter = new ListAdapter(context,
									list);
							personList.setAdapter(listadapter);
						}
					}}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<ContentValues> list;

		public ListAdapter(Context context, List<ContentValues> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
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
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number = (TextView) convertView
						.findViewById(R.id.number);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ContentValues cv = list.get(position);
			String user_name = cv.getAsString(NAME);
			String user_number = cv.getAsString(PHONENO1);
			holder.name.setText(user_name);
//			holder.number.setText(user_number);
			
			if (user_number.startsWith("anyType")) {
				holder.number.setText("");
			} else {
				holder.number.setText(user_number);
			}
			
			String user_id_list = cv.getAsString(ID);
			convertView.setOnClickListener(new mmContactAdapterListener(
					position, user_id_list, user_name, cv.getAsString(DEPTID),cv.getAsString(MAIL), cv.getAsString(DEPTNAME),
					cv.getAsString(PHONENO1),cv.getAsString(PHONENO2),cv.getAsString(PHONENO3),cv.getAsString(PHONENO4),
					cv.getAsString(PHONENAME1),cv.getAsString(PHONENAME2), cv.getAsString(PHONENAME3), cv.getAsString(PHONENAME4) 
					));
			return convertView;
		}
		private class ViewHolder {
			TextView name;
			TextView number;
		}
		

		class mmContactAdapterListener implements OnClickListener {
			private int position;
			private String mm_id;
			private String mm_name;
			private String mm_deptid;
			private String mm_phoneNo1;
			private String mm_phoneNo2;
			private String mm_phoneNo3;
			private String mm_phoneNo4;
			private String mm_mail;
			private String mm_deptname;
			private String mm_phoneName1;
			private String mm_phoneName2;
			private String mm_phoneName3;
			private String mm_phoneName4;

			public mmContactAdapterListener(int pos, String _id, String _name,
					String _deptid,String _mail,String _deptname, String _phoneNo1,
					String _phoneNo2, String _phoneNo3, String _phoneNo4, String _phoneName1, 
					String _phoneName2, String _phoneName3, String _phoneName4) {
				// TODO Auto-generated constructor stub
				position = pos;
				this.mm_id = _id;
				this.mm_name = _name;
				this.mm_deptid = _deptid;
				this.mm_mail = _mail;
				this.mm_deptname = _deptname;
				this.mm_phoneNo1 = _phoneNo1;
				this.mm_phoneNo2 = _phoneNo2;
				this.mm_phoneNo3 = _phoneNo3;
				this.mm_phoneNo4 = _phoneNo4;
				this.mm_phoneName1 = _phoneName1;
				this.mm_phoneName2 = _phoneName2;
				this.mm_phoneName3 = _phoneName3;
				this.mm_phoneName4 = _phoneName4;
			}

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ContactDisplayActivity.class);
				intent.putExtra("id", mm_id);
				intent.putExtra("name", mm_name);
				intent.putExtra("deptid", mm_deptid);
				intent.putExtra("mail", mm_mail);
				intent.putExtra("deptname", mm_deptname);
				intent.putExtra("phoneNo1", mm_phoneNo1);
				intent.putExtra("phoneNo2", mm_phoneNo2);
				intent.putExtra("phoneNo3", mm_phoneNo3);
				intent.putExtra("phoneNo4", mm_phoneNo4);
				intent.putExtra("phoneName1", mm_phoneName1);
				intent.putExtra("phoneName2", mm_phoneName2);
				intent.putExtra("phoneName3", mm_phoneName3);
				intent.putExtra("phoneName4", mm_phoneName4);
				startActivity(intent);
			}

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
			String cmethodname = MessengerService.METHOD_GetAddressBookListByID;
			String cnamespace = MessengerService.NAMESPACE;
			String csoapaction = cnamespace + "/" + cmethodname;

			SoapObject rpc = new SoapObject(cnamespace, cmethodname);
			rpc.addProperty("deptid", dept_Id);
			// rpc.addProperty("pagesize", 1000);
			// rpc.addProperty("pageindex", 1);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(curl);
			ht.debug = true;

			List<ContentValues> list = new ArrayList<ContentValues>();
			try {
				ht.call(csoapaction, envelope);
				SoapObject contactlist = (SoapObject) envelope.bodyIn;
				SoapObject soapchilds1 = (SoapObject) contactlist
						.getProperty(0);
				SoapObject soapchilds2 = (SoapObject) soapchilds1
						.getProperty(1);
				// SoapObject soapchilds3 = (SoapObject)
				// soapchilds2.getProperty(0);
				for (int i = 0; i < contactlist.getPropertyCount(); i++) {
					SoapObject soapchilds = (SoapObject) soapchilds2
							.getProperty(i);
					for (int j = 0; j < soapchilds.getPropertyCount(); j++) {
						SoapObject soapchildsson = (SoapObject) soapchilds
								.getProperty(j);

						String Id = soapchildsson.getProperty("Id").toString();
						String Name = soapchildsson.getProperty("Name")
								.toString();
						String mail = soapchildsson.getProperty("mail")
								.toString();
						String Dept_id = soapchildsson.getProperty("Dept_id")
								.toString();
//						String orders = soapchildsson.getProperty("orders")
//								.toString();
						String phoneNo1 = soapchildsson.getProperty(
								"phoneNo1").toString();
						String phoneNo2 = soapchildsson.getProperty(
								"phoneNo2").toString();
						String phoneNo3 = soapchildsson.getProperty(
								"phoneNo3").toString();
						String phoneNo4 = soapchildsson.getProperty(
								"phoneNo4").toString();
						String phoneName1 = soapchildsson.getProperty("phoneName1")
								.toString();
						String phoneName2 = soapchildsson.getProperty("phoneName2")
								.toString();
						String phoneName3 = soapchildsson.getProperty("phoneName3")
								.toString();
						String phoneName4 = soapchildsson.getProperty("phoneName4")
								.toString();

						ContentValues cv = new ContentValues();
						cv.put(ID, Id);
						cv.put(NAME, Name);
						cv.put(MAIL, mail);
						cv.put(DEPTID, Dept_id);
						cv.put(PHONENO1, phoneNo1);
						cv.put(PHONENO2, phoneNo2);
						cv.put(PHONENO3, phoneNo3);
						cv.put(PHONENO4, phoneNo4);
						cv.put(PHONENAME1, phoneName1);
						cv.put(PHONENAME2, phoneName2);
						cv.put(PHONENAME3, phoneName3);
						cv.put(PHONENAME4, phoneName4);
						list.add(cv);

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
			msg.obj = list;
			threadMessageHandler.sendMessage(msg);

		}
	}

	private class mGetContactAllDataThread implements Runnable {

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
			// rpc.addProperty("deptid", dept_Id);
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
				SoapObject soapchilds1 = (SoapObject) contactlist
						.getProperty(0);
				SoapObject soapchilds2 = (SoapObject) soapchilds1
						.getProperty(1);
				// SoapObject soapchilds3 = (SoapObject)
				// soapchilds2.getProperty(0);
				for (int i = 0; i < contactlist.getPropertyCount(); i++) {
					SoapObject soapchilds = (SoapObject) soapchilds2
							.getProperty(i);
					for (int j = 0; j < soapchilds.getPropertyCount(); j++) {
						SoapObject soapchildsson = (SoapObject) soapchilds
								.getProperty(j);

						String Id = soapchildsson.getProperty("Id").toString();
						String Name = soapchildsson.getProperty("Name")
								.toString();
						String mail = soapchildsson.getProperty("mail")
								.toString();
						String Dept_id = soapchildsson.getProperty("Dept_id")
								.toString();
						String orders = soapchildsson.getProperty("orders")
								.toString();
						String phoneNo1 = soapchildsson.getProperty(
								"phoneNo1").toString();
						String phoneNo2 = soapchildsson.getProperty(
								"phoneNo2").toString();
						String phoneNo3 = soapchildsson.getProperty(
								"phoneNo3").toString();
						String phoneNo4 = soapchildsson.getProperty(
								"phoneNo4").toString();
						String phoneName1 = soapchildsson.getProperty("phoneName1")
								.toString();
						String phoneName2 = soapchildsson.getProperty("phoneName2")
								.toString();
						String phoneName3 = soapchildsson.getProperty("phoneName3")
								.toString();
						String phoneName4 = soapchildsson.getProperty("phoneName4")
								.toString();
						
						
						ContentValues cv = new ContentValues();
						cv.put(tdd.CON_ID, Id);
						cv.put(tdd.CON_NAME, Name);
						cv.put(tdd.CON_MAIL, mail);
						cv.put(tdd.CON_DEPTID, Dept_id);
						cv.put(tdd.CON_ORDERS, orders);
						cv.put(tdd.CON_PHONENO1, phoneNo1);
						cv.put(tdd.CON_PHONENO2, phoneNo2);
						cv.put(tdd.CON_PHONENO3, phoneNo3);
						cv.put(tdd.CON_PHONENO4, phoneNo4);
						cv.put(tdd.CON_PHONENAME1, phoneName1);
						cv.put(tdd.CON_PHONENAME2, phoneName2);
						cv.put(tdd.CON_PHONENAME3, phoneName3);
						cv.put(tdd.CON_PHONENAME4, phoneName4);

						tdd.insertcontact(cv);

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
			msg.arg1 = 2;
			// msg.obj = list;
			threadMessageHandler.sendMessage(msg);

		}
	}
}
