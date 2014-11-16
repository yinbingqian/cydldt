package com.lnpdit.chatuidemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.tmkq.MyReportActivity;
import com.sytm.tmkq.NowReportActivity;
import com.sytm.tmkq.ToMeReportActivity;

public class MailActivity extends Activity implements
		AdapterView.OnItemClickListener {
	/** Called when the activity is first created. */

	private GridView gv;
	private SharedPreferences spPreferences;
	Handler handler;

	int itemCount = 1;
	Button return_bt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		handler = new Handler();
		
		gv = (GridView) this.findViewById(R.id.gridview_01);
		return_bt = (Button) this.findViewById(R.id.return_bt);
		return_bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		itemCount = 3;

		List<Map<String, Object>> data = this.getData();

		// 下面开始适配

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.menuitem, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });

		// 将其放在GridView中
		gv.setAdapter(adapter);

		// 监听单击事件
		gv.setOnItemClickListener(this); // 给类已经实现AdapterView.OnItemClickListener接口，在该类中实现onItemClick方法

	}

	public List<Map<String, Object>> getData() {

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		if (itemCount < 5) {
			gv.setNumColumns(2);
			itemCount++;
		} else {
			gv.setNumColumns(3);
			itemCount++;
		}
		for (int i = 1; i < itemCount; i++) { // 构造数据模型
			Map<String, Object> item = new HashMap<String, Object>();
			switch (i) {

			case 1:
				item.put("itemImage", R.drawable.item1);
				item.put("itemText", getResources().getString(R.string.item1));
				break;

			case 2:
				item.put("itemImage", R.drawable.item2);
				item.put("itemText", getResources().getString(R.string.item2));
				break;

			case 3:
				item.put("itemImage", R.drawable.item3);
				item.put("itemText", getResources().getString(R.string.item3));
				break;

			default:
				break;

			}

			data.add(item);
		}

		return data;
	}

	@Override
	public void onItemClick(AdapterView parent, View v, int location, long id) {
		// 在这里处理每一个item的单击事件

		HashMap<String, Object> item = (HashMap<String, Object>) parent
				.getItemAtPosition(location);

		if (item.get("itemText").equals(
				getResources().getString(R.string.item1))) {
			Intent intent = new Intent(MailActivity.this,
					NowReportActivity.class);// 为Intent设置需要激活的组件


			startActivity(intent);
			overridePendingTransition(R.anim.my_scale_action,
					R.anim.push_left_out);

		}
		if (item.get("itemText").equals(
				getResources().getString(R.string.item2))) {

			Intent intent = new Intent();
			intent.setClass(MailActivity.this, ToMeReportActivity.class);
			startActivity(intent);
		}
		if (item.get("itemText").equals(
				getResources().getString(R.string.item3))) {

			Intent intent = new Intent(MailActivity.this,
					MyReportActivity.class);

			startActivity(intent);
		}
	}


	/** 返回键拦截监听 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
		}
		return false;
	}


}