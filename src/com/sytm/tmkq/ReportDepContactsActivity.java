package com.sytm.tmkq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lnpdit.chatuidemo.R;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sytm.adapter.ReportDepAdapter;
import com.sytm.application.HanZiUtils;
import com.sytm.common.DepnameModel;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.JsonUtils;
import com.sytm.util.PinyinComparator;
import com.sytm.widget.AlphabetListReportDepView;
import com.sytm.widget.RTPullListView;
import com.sytm.widget.RTPullListView.OnRefreshListener;

@SuppressLint({ "HandlerLeak", "InlinedApi" })
@SuppressWarnings("unused")
public class ReportDepContactsActivity extends Activity implements
OnItemClickListener, OnRefreshListener {
	private View mView;
	private ArrayList<DepnameModel> menuList = new ArrayList<DepnameModel>();
	private RTPullListView listView;
	private ServiceResult sr = new ServiceResult();
	private ReportDepAdapter adapter;
	private SharedPreferences sp;
	private String empid = "";
	private int TAG = 0;
	private Button left;
	private RelativeLayout topLayout;
	private int screenWidth;
	private int screenHeight;
	private InputMethodManager manager;
	private PopupWindow popupWindow;
	private RelativeLayout contact_view_main;
	private int NUM = 0;
	private Boolean open=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportdep);
		initialize();
		open=true;
		manager = (InputMethodManager) getSystemService(
				Context.INPUT_METHOD_SERVICE);
		Intent intent = getIntent();
		TAG = intent.getIntExtra("TAG", 0);
		sp = getSharedPreferences("TMMTC", Context.MODE_PRIVATE);
		empid = sp.getString("empid", "");
		new Task().execute("reportdep");
	}

	public void initialize( ) {
		listView = (RTPullListView) findViewById(R.id.mylistviewkq);
		topLayout = (RelativeLayout) findViewById(R.id.top);
		left = (Button) findViewById(R.id.left);
		left.setOnClickListener(new MyOnClickListener());
		topLayout.setOnTouchListener(new MyOnTouchListener());
		listView.setOnTouchListener(new MyOnTouchListener());
		listView.setOnItemClickListener(this);
		listView.setonRefreshListener(this);
	}

	class MyOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (getCurrentFocus() != null
						&& getCurrentFocus().getWindowToken() != null) {
					manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
							android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
			return false;
		}

	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left:
				finish();
				break;
			case R.id.right:
				break;
			default:
				break;
			}
		}

	}




	/**
	 * 异步线程
	 * 
	 * @author wyq
	 * 
	 */
	class Task extends AsyncTask<String, String, String> {
		// 开启另外一个线程执行任务
		@Override
		protected String doInBackground(String... params) {
			String exeParam = params[0];
			try {
				if (exeParam.equals("reportdep")) {
					ServiceContent sc = new ServiceContent();
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.setClassname("DepartmentHandler");
					sc.setMethodname("GetDepList");
					sr = Network.postDataService(sc);
				}
			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {
			listView.onRefreshComplete();
			if (result.equals("reportdep")) {
				if (sr.GetIsError()) {
					// myDialog(sr.getMessage());
				} else {
					menuList.clear();
					try {
						menuList = (ArrayList<DepnameModel>) JsonUtils
								.parseCollection(sr.getData(), List.class,
										DepnameModel.class);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter = new ReportDepAdapter(ReportDepContactsActivity.this, menuList);
					listView.setAdapter(adapter);
				}
			} else if (result.equals("ERROR")) {
				myDialog(getResources().getString(R.string.please_error));
			} else {
				myDialog(getResources().getString(R.string.no_thing));
			}
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		open = false;
		menuList.clear();
		int TAG = 0;
	}

	Dialog dialog;

	public void myDialog(String msg) {
		View v = View.inflate(ReportDepContactsActivity.this, R.layout.dialog_alone, null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		dialog = new Dialog(ReportDepContactsActivity.this, R.style.dialog_style);
		dialog.setContentView(v);
		dialog.setCancelable(true);
		textView.setText(msg);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		if (open) {
			dialog.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (getCurrentFocus() != null
				&& getCurrentFocus().getWindowToken() != null) {
			manager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(),
					android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);
		}
		Intent intent  = new Intent(ReportDepContactsActivity.this,ReportContactActivity.class);
//		startActivity(intent);
		Log.i("获取:"+position, menuList.get(position-1).getId()+"::"+menuList.get(position-1).getDepname());
		intent.putExtra("id", menuList.get(position-1).getId());
		startActivityForResult(intent, 5);
//		popuWidow(position - 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK&&requestCode==5) {
			Intent intent = new Intent();
			intent.putExtra("list", data.getSerializableExtra("list"));
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	@Override
	public void onRefresh() {
		new Task().execute("reportdep");
	}

}
