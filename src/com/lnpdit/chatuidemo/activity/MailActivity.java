package com.lnpdit.chatuidemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lnpdit.chatuidemo.R;
import com.sytm.tmkq.MyReportActivity;
import com.sytm.tmkq.NowReportActivity;
import com.sytm.tmkq.ToMeReportActivity;

public class MailActivity extends Activity implements OnClickListener {

	Button return_bt;
	Button send_bt;
	Button receive_bt;
	Button write_bt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		viewInit();

	}

	private void viewInit() {
		// TODO Auto-generated method stub
		return_bt = (Button) findViewById(R.id.return_bt);
		send_bt = (Button) findViewById(R.id.mail_send_bt);
		write_bt = (Button) findViewById(R.id.mail_write_bt);
		receive_bt = (Button) findViewById(R.id.mail_receive_bt);
		
		return_bt.setClickable(true);
		send_bt.setClickable(true);
		write_bt.setClickable(true);
		receive_bt.setClickable(true);
		
		return_bt.setOnClickListener(this);
		send_bt.setOnClickListener(this);
		write_bt.setOnClickListener(this);
		receive_bt.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.return_bt:
			finish();
			break;
		case R.id.mail_send_bt:
			startActivity(new Intent(MailActivity.this, MyReportActivity.class));
			break;
		case R.id.mail_write_bt:
			startActivity(new Intent(MailActivity.this, NowReportActivity.class));
			break;
		case R.id.mail_receive_bt:
			startActivity(new Intent(MailActivity.this, ToMeReportActivity.class));
			break;
		default:
			break;
		}
	}
}