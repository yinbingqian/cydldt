package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.lnpdit.chatuidemo.R;

import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.data.ToDoDB;
import lnpdit.stategrid.informatization.tools.CircularImage;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactDisplayActivity extends Activity {

	Context context;

	String id;
	String name;
	String deptid;
	String phoneNo1;
	String phoneNo2;
	String phoneNo3;
	String phoneNo4;
	String phoneName1;
	String phoneName2;
	String phoneName3;
	String phoneName4;
	String mail;
	String deptname;

	CircularImage cover_user_photo;
	TextView name_text;
	TextView deptname_text;
	TextView phoneNo1_text;
	TextView phoneNo2_text;
	TextView phoneNo3_text;
	TextView phoneNo4_text;
	TextView phoneName1_text;
	TextView phoneName2_text;
	TextView phoneName3_text;
	TextView phoneName4_text;
	
	Button phone1_call_bt;
	Button phone1_msg_bt;
	Button phone2_call_bt;
	Button phone2_msg_bt;
	Button phone3_call_bt;
	Button phone3_msg_bt;
	Button phone4_call_bt;
	Button phone4_msg_bt;
	Button return_bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_display);

		context = this;

		Intent intent = this.getIntent();
		id = intent.getStringExtra("id");
		name = intent.getStringExtra("name");
		deptid = intent.getStringExtra("deptid");
		phoneNo1 = intent.getStringExtra("phoneNo1");
		phoneNo2 = intent.getStringExtra("phoneNo2");
		phoneNo3 = intent.getStringExtra("phoneNo3");
		phoneNo4 = intent.getStringExtra("phoneNo4");
		phoneName1 = intent.getStringExtra("phoneName1");
		phoneName2 = intent.getStringExtra("phoneName2");
		phoneName3 = intent.getStringExtra("phoneName3");
		phoneName4 = intent.getStringExtra("phoneName4");
		mail = intent.getStringExtra("mail");
		deptname = intent.getStringExtra("deptname");
		viewInit();
		
		RelativeLayout phone1_layout=(RelativeLayout)findViewById(R.id.phone1_layout);
		TextView second_line = (TextView)findViewById(R.id.second_line);
		RelativeLayout phone2_layout=(RelativeLayout)findViewById(R.id.phone2_layout);
		TextView third_line = (TextView)findViewById(R.id.third_line);
		RelativeLayout phone3_layout=(RelativeLayout)findViewById(R.id.phone3_layout);
		TextView fourth_line = (TextView)findViewById(R.id.fourth_line);
		RelativeLayout phone4_layout=(RelativeLayout)findViewById(R.id.phone4_layout);
		TextView fifth_line = (TextView)findViewById(R.id.fifth_line);
		
		if(phoneNo1.startsWith("anyType"))
		{
			phone1_layout.setVisibility(8);
			second_line.setVisibility(8);
		}
		if(phoneNo2.startsWith("anyType"))
		{
			phone2_layout.setVisibility(8);
			third_line.setVisibility(8);
		}
		if(phoneNo3.startsWith("anyType"))
		{
			phone3_layout.setVisibility(8);
			fourth_line.setVisibility(8);
		}
		if(phoneNo4.startsWith("anyType"))
		{
			phone4_layout.setVisibility(8);
			fifth_line.setVisibility(8);
		}
	}

	private void viewInit() {
		cover_user_photo = (CircularImage) this.findViewById(R.id.cover_user_photo);
		name_text = (TextView) this.findViewById(R.id.name_text);
		deptname_text = (TextView) this.findViewById(R.id.dept_text);
		phoneNo1_text = (TextView) this.findViewById(R.id.phoneNo1_text);
		phoneNo2_text = (TextView) this.findViewById(R.id.phoneNo2_text);
		phoneNo3_text = (TextView) this.findViewById(R.id.phoneNo3_text);
		phoneNo4_text = (TextView) this.findViewById(R.id.phoneNo4_text);
		phoneName1_text = (TextView) this.findViewById(R.id.phoneName1_text);
		phoneName2_text = (TextView) this.findViewById(R.id.phoneName2_text);
		phoneName3_text = (TextView) this.findViewById(R.id.phoneName3_text);
		phoneName4_text = (TextView) this.findViewById(R.id.phoneName4_text);
		phone1_call_bt = (Button) this.findViewById(R.id.call2_bt);
		phone1_msg_bt = (Button) this.findViewById(R.id.msg2_bt);
		phone2_call_bt = (Button) this.findViewById(R.id.call3_bt);
		phone2_msg_bt = (Button) this.findViewById(R.id.msg3_bt);
		phone3_call_bt = (Button) this.findViewById(R.id.call4_bt);
		phone3_msg_bt = (Button) this.findViewById(R.id.msg4_bt);
		phone4_call_bt = (Button) this.findViewById(R.id.call1_bt);
		phone4_msg_bt = (Button) this.findViewById(R.id.msg1_bt);
		return_bt = (Button) this.findViewById(R.id.return_bt);
		
		phone1_call_bt.setOnClickListener(btnListener);
		phone1_msg_bt.setOnClickListener(btnListener);
		phone2_call_bt.setOnClickListener(btnListener);
		phone2_msg_bt.setOnClickListener(btnListener);
		phone3_call_bt.setOnClickListener(btnListener);
		phone3_msg_bt.setOnClickListener(btnListener);
		phone4_call_bt.setOnClickListener(btnListener);
		phone4_msg_bt.setOnClickListener(btnListener);
		return_bt.setOnClickListener(btnListener);
		

		cover_user_photo.setImageResource(R.drawable.user_head_pic);

		name_text.setText(name);
		deptname_text.setText(deptname);
		phoneNo1_text.setText(phoneNo1);
		phoneNo2_text.setText(phoneNo2);
		phoneNo3_text.setText(phoneNo3);
		phoneNo4_text.setText(phoneNo4);
		phoneName1_text.setText(phoneName1);
		phoneName2_text.setText(phoneName2);
		phoneName3_text.setText(phoneName3);
		phoneName4_text.setText(phoneName4);
	}

	private android.view.View.OnClickListener btnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.call1_bt:
                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo1));
                startActivity(intent1);
				break;
			case R.id.call2_bt:
                Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo2));
                startActivity(intent2);
				break;
			case R.id.call3_bt:
				Intent intent3 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo3));
				startActivity(intent3);
				break;
			case R.id.call4_bt:
				Intent intent4 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo4));
				startActivity(intent4);
				break;
			case R.id.msg1_bt:
			    Uri smsToUri1 = Uri.parse("smsto://" + phoneNo1);  
		        Intent mIntent1 = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri1 );  
		        startActivity(mIntent1);  
				break;
			case R.id.msg2_bt:
			    Uri smsToUri2 = Uri.parse("smsto://" + phoneNo2);  
		        Intent mIntent2 = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri2 );  
		        startActivity( mIntent2 );  
				break;
			case R.id.msg3_bt:
				Uri smsToUri3 = Uri.parse("smsto://" + phoneNo3);  
				Intent mIntent3 = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri3 );  
				startActivity( mIntent3);  
				break;
			case R.id.msg4_bt:
				Uri smsToUri4 = Uri.parse("smsto://" + phoneNo4);  
				Intent mIntent4 = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri4 );  
				startActivity( mIntent4 );  
				break;
			case R.id.return_bt:
				finish();
				break;

			default:
				break;
			}
		}
	};

}
