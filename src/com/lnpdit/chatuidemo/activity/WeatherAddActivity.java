package com.lnpdit.chatuidemo.activity;

import com.lnpdit.chatuidemo.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class WeatherAddActivity extends Activity{

	Context context;
	
	String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_add);
		
		context = this;
		
		viewInit();
	}
	
	private void viewInit(){
		SharedPreferences sp = this.getSharedPreferences("user_info", MODE_APPEND);
		userid = sp.getString("Id", "");
	}
	
	
}
