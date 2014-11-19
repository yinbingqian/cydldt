package com.lnpdit.chatuidemo.activity;

import com.lnpdit.chatuidemo.R;

import lnpdit.stategrid.informatization.tools.CircularImage;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactDisplayActivity extends Activity {

	Context context;

	String id;
	String name;
	String deptid;
	String mobilephone;
	String phone;
	String mail;
	String deptname;

	CircularImage cover_user_photo;
	TextView name_text;
	TextView deptname_text;
	TextView mobilephone_text;
	TextView phone_text;
	Button mobile_call_bt;
	Button mobile_msg_bt;
	Button phone_call_bt;
	Button phone_msg_bt;
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
		mobilephone = intent.getStringExtra("mobilephone");
		phone = intent.getStringExtra("phone");
		mail = intent.getStringExtra("mail");
		deptname = intent.getStringExtra("deptname");
		
		RelativeLayout phone_layout=(RelativeLayout)findViewById(R.id.phone_layout);
		TextView third_line = (TextView)findViewById(R.id.third_line);

		if(phone.equals("anyType{}"))
		{
			phone_layout.setVisibility(View.INVISIBLE);
			third_line.setVisibility(View.INVISIBLE);
		}

		viewInit();
	}

	private void viewInit() {
		cover_user_photo = (CircularImage) this.findViewById(R.id.cover_user_photo);
		name_text = (TextView) this.findViewById(R.id.name_text);
		deptname_text = (TextView) this.findViewById(R.id.dept_text);
		mobilephone_text = (TextView) this.findViewById(R.id.mobilephone_text);
		phone_text = (TextView) this.findViewById(R.id.phone_text);
		mobile_call_bt = (Button) this.findViewById(R.id.call2_bt);
		mobile_msg_bt = (Button) this.findViewById(R.id.msg2_bt);
		phone_call_bt = (Button) this.findViewById(R.id.call1_bt);
		phone_msg_bt = (Button) this.findViewById(R.id.msg1_bt);
		return_bt = (Button) this.findViewById(R.id.return_bt);
		
		mobile_call_bt.setOnClickListener(btnListener);
		mobile_msg_bt.setOnClickListener(btnListener);
		phone_call_bt.setOnClickListener(btnListener);
		phone_msg_bt.setOnClickListener(btnListener);
		return_bt.setOnClickListener(btnListener);

		cover_user_photo.setImageResource(R.drawable.user_head_pic);

		name_text.setText(name);
		deptname_text.setText(deptname);
		mobilephone_text.setText(mobilephone);
		phone_text.setText(phone);
	}

	private android.view.View.OnClickListener btnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.call1_bt:
                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobilephone));
                startActivity(intent1);
				break;
			case R.id.call2_bt:
                Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent2);
				break;
			case R.id.msg1_bt:
			    Uri smsToUri1 = Uri.parse("smsto://" + mobilephone);  
		        Intent mIntent1 = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri1 );  
		        startActivity(mIntent1);  
				break;
			case R.id.msg2_bt:
			    Uri smsToUri2 = Uri.parse("smsto://" + phone);  
		        Intent mIntent2 = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri2 );  
		        startActivity( mIntent2 );  
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
