package com.lnpdit.chatuidemo.activity;

import com.lnpdit.chatuidemo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ControlActivity extends Activity implements OnClickListener {

	Button return_bt;	
	Button yujing_bt;
	Button anpai_bt;
	Button fankui_bt;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		viewInit();
	}

	private void viewInit() {
		return_bt = (Button)findViewById(R.id.return_bt);
		return_bt.setOnClickListener(this);
		return_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		yujing_bt = (Button)findViewById(R.id.yujing_bt);
		anpai_bt = (Button)findViewById(R.id.anpai_bt);
		fankui_bt = (Button)findViewById(R.id.fankui_bt);
		
		
		yujing_bt.setClickable(true);
		anpai_bt.setClickable(true);
		fankui_bt.setClickable(true);

		yujing_bt.setOnClickListener(this);
		anpai_bt.setOnClickListener(this);
		fankui_bt.setOnClickListener(this);
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.yujing_bt:
			startActivity(new Intent(ControlActivity.this, WeatherActivity.class));
			break;
		case R.id.anpai_bt:
			startActivity(new Intent(ControlActivity.this, JobPlanActivity.class));
			break;
		case R.id.fankui_bt:
			startActivity(new Intent(ControlActivity.this, JobBackActivity.class));
			break;
		default:
			break;
		}
	}
}
