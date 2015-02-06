package com.sytm.tmkq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lnpdit.stategrid.informatization.adapter.ContactDeptAdapter;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.lnpdit.chatuidemo.activity.ContactDeptActivity;
import com.lnpdit.chatuidemo.activity.ContactListSearchActivity;
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
import com.sytm.widget.AlphabetListReportDepView.OnItemClickListener;
import com.sytm.widget.AlphabetListReportDepView.OnRefreshListener;

@SuppressLint({ "HandlerLeak", "InlinedApi" })
@SuppressWarnings("unused")
public class ReportDepContactsActivity extends Activity {
//	private ImageLoader mImageLoader;
	private View mView;
	private ListView listView;
//	private HashMap<String, Integer> alphaIndexer = null;
	private ServiceResult sr = new ServiceResult();
	private ArrayList<DepnameModel> menuList = new ArrayList<DepnameModel>();
	private ReportDepAdapter adapter;
//	private EditText put_search;
	private SharedPreferences sp;
	private String empid = "";
	private int TAG = 0;
	private Button left;
//	private TextView number;
//	private RelativeLayout topLayout;
	private int screenWidth;
	private int screenHeight;
	private InputMethodManager manager;
	private PopupWindow popupWindow;
	private RelativeLayout contact_view_main;
	private int NUM = 0;
//	private ContactDBManager dbManager;
	private Boolean open=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportdep);
		initialize();
		open=true;
		manager = (InputMethodManager) getSystemService(
				Context.INPUT_METHOD_SERVICE);
//		dbManager = new ContactDBManager(ReportDepContactsActivity.this);
		Intent intent = getIntent();
		TAG = intent.getIntExtra("TAG", 0);
		sp = getSharedPreferences("TMMTC", Context.MODE_PRIVATE);
//		if (dbManager.isData("reportdep")) {
//			menuList = dbManager.query("reportdep");
//			dbManager.closeDB();
//			setData();
//		} else {
			new Task().execute("reportdep");
//		}
		empid = sp.getString("empid", "");
	}

	@Override
	public void onStart() {
		super.onStart();
//		number.setText("总共" + modes.size() + "个部门");
	}

	public void initialize( ) {
//		mImageLoader = ImageLoader.getInstance();
		listView = (ListView) findViewById(R.id.listview);
//		topLayout = (RelativeLayout) findViewById(R.id.top);
//		put_search = (EditText) findViewById(R.id.serveredit);
		left = (Button) findViewById(R.id.return_bt);
//		search_bt = (Button) findViewById(R.id.search_bt);
//		number = (TextView) findViewById(R.id.contactnumber);
//		left.setOnClickListener(new MyOnClickListener());
		left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
//		search_bt.setOnClickListener(new MyOnClickListener());
//		put_search.addTextChangedListener(new MyaddTextChangedListener());
//		topLayout.setOnTouchListener(new MyOnTouchListener());
//		listView.setOnTouchListener(new MyOnTouchListener());
//		listView.setOnItemClickListener(this);
//		listView.setonRefreshListener(this);
	}

//	class MyOnTouchListener implements OnTouchListener {
//
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//			if (event.getAction() == MotionEvent.ACTION_DOWN) {
//				if (getCurrentFocus() != null
//						&& getCurrentFocus().getWindowToken() != null) {
//					manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//							android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);
//				}
//			}
//			return false;
//		}
//
//	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.return_bt:
				finish();
				break;
//			case R.id.search_bt:
//				Intent intent = new Intent();
//				intent.setClass(ReportDepContactsActivity.this,ReportContactActivity.class);
//				SharedPreferences sharedPreferences = getSharedPreferences(
//						"TMMTC", Context.MODE_PRIVATE);
//				String DeptId = sharedPreferences.getString("DeptId", "");
//				intent.putExtra("id", DeptId);
//				intent.putExtra("Grade", "");
//				intent.putExtra("Class", "我的部门");
//				intent.putExtra("Remark", "");
//				startActivity(intent);
//				break;
			default:
				break;
			}
		}

	}


//	/**
//	 * 绑定数据
//	 * 
//	 * @param
//	 */
//
//	private void setData() {
//		modes.clear();
//		if (alphaIndexer != null) {
//			alphaIndexer.clear();
//			alphaIndexer = null;
//		}
//		if (menuList != null && menuList.size() > 0) {
//			if (alphaIndexer == null) {
//				alphaIndexer = new HashMap<String, Integer>();
//				for (int i = 0; i < menuList.size(); i++) {
//					// 取出每一条数据
//					String id = menuList.get(i).getId();
//					String depname = menuList.get(i).getDepname();
//					String organid = menuList.get(i).getOrganid();
//					String remarks = menuList.get(i).getRemarks();
//					String tags = menuList.get(i).getTags();
//
//					// 把数据封装在实体中
//					DepnameModel mode = new DepnameModel();
//					mode.setId(id);
//					mode.setDepname(depname);
//					mode.setOrganid(organid);
//					mode.setRemarks(remarks);
//					mode.setTags(tags);
//					String firstAlpha = HanZiUtils.toPinYin(depname);
//					mode.setFirstAlpha(firstAlpha);
//					// 将封装的实体加到数组中
//					modes.add(mode);
//
//				}
//				PinyinComparator pinyinComparator = new PinyinComparator();
//				// 根据a-z进行排序源数据
//				//Collections.sort(modes, pinyinComparator);
//				for (int i = 0; i < modes.size(); i++) {
//					// 处理当点击某一个字母后，其内容出现在屏幕可见状态下的最上面
//					DepnameModel conteactMode = modes.get(i);
//					String currentAlpha = conteactMode.getFirstAlpha();
//					DepnameModel mode = (i - 1) >= 0 ? modes.get(i - 1) : null;
//					String previewStr = "";
//					if (mode != null) {
//						previewStr = mode.getFirstAlpha();
//					}
//					if (!previewStr.equals(currentAlpha)) {
//						alphaIndexer.put(currentAlpha, i);}
//				}
//				// 把数据设置到adapter
//				adapter = new ReportDepAdapter(ReportDepContactsActivity.this);
//				adapter.setData(modes);
////				listView.setAlphabetIndex(alphaIndexer);
//				listView.setAdapter(adapter);
//				number.setText("总共" + modes.size() + "个部门");
//			}
//		}
//	}

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
//			listView.onRefreshComplete();
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
					int count = menuList.size();
					ArrayList<HashMap<String, Object>> remoteWindowItem_double = new ArrayList<HashMap<String, Object>>();
					for (int i = 0; i < menuList.size() / 2; i++) {
						DepnameModel hash1 = menuList.get(i * 2);
						String id1 = hash1.getId();
						String grade1 = hash1.getOrganid();
						String class1 = hash1.getDepname();
						String remark1 = hash1.getRemarks();

						DepnameModel hash2 = menuList.get(i * 2 + 1);
						String id2 = hash2.getId();
						String grade2 = hash2.getOrganid();
						String class2 = hash2.getDepname();
						String remark2 = hash2.getRemarks();

						HashMap<String, Object> hash_double = new HashMap<String, Object>();
						hash_double.put("id1", id1);
						hash_double.put("grade1", grade1);
						hash_double.put("class1", class1);
						hash_double.put("remark1", remark1);
						hash_double.put("id2", id2);
						hash_double.put("grade2", grade2);
						hash_double.put("class2", class2);
						hash_double.put("remark2", remark2);
						remoteWindowItem_double.add(hash_double);
					}
					if (menuList.size() % 1 != 0) {
						DepnameModel hash1 = menuList.get(count - 1);
						String id1 = hash1.getId();
						String grade1 = hash1.getOrganid();
						String class1 = hash1.getDepname();
						String remark1 = hash1.getRemarks();

						HashMap<String, Object> hash_double = new HashMap<String, Object>();
						hash_double.put("id1", id1);
						hash_double.put("grade1", grade1);
						hash_double.put("class1", class1);
						hash_double.put("remark1", remark1);
						hash_double.put("id2", "");
						hash_double.put("grade2", "");
						hash_double.put("class2", "");
						hash_double.put("remark2", "");
						remoteWindowItem_double.add(hash_double);
					}
					adapter = new ReportDepAdapter(
							ReportDepContactsActivity.this, remoteWindowItem_double, R.layout.list_in_weather,
							new String[] { "id1", "grade1", "class1", "remark1",
									"id2", "grade2", "class2", "remark2" },
							new int[] { R.id.temp_tv, R.id.temp_tv, R.id.temp_tv,
									R.id.temp_tv },ReportDepContactsActivity.this);
					listView.setAdapter(adapter);
//					adapter = new ReportDepAdapter(ReportDepContactsActivity.this,menuList);
//					Log.i("sdsadsa", adapter+"''''");
//					listView.setAdapter(adapter);
//					setData();
//					if (dbManager.isOpen()) {
//						dbManager.clearTable("reportdep");
//						dbManager.add(menuList, "reportdep");
//						dbManager.closeDB();
//					} else {
//						dbManager = null;
//						dbManager = new ContactDBManager(ReportDepContactsActivity.this);
//						dbManager.clearTable("reportdep");
//						dbManager.add(menuList, "reportdep");
//						dbManager.closeDB();
//					}
				}
			} else if (result.equals("ERROR")) {
				myDialog(getResources().getString(R.string.please_error));
			} else {
				myDialog(getResources().getString(R.string.no_thing));
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		open = false;
//		if (alphaIndexer != null) {
//			alphaIndexer.clear();
//			alphaIndexer = null;
//		}
//		if (dbManager.isOpen()) {
//			dbManager.closeDB();
//		}
//		dbManager = null;
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

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		if (getCurrentFocus() != null
//				&& getCurrentFocus().getWindowToken() != null) {
//			manager.hideSoftInputFromWindow(getCurrentFocus()
//					.getWindowToken(),
//					android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);
//		}
//		Intent intent  = new Intent(ReportDepContactsActivity.this,ReportContactActivity.class);
//		
////		startActivity(intent);
//		Log.i("获取:"+position, modes.get(position-1).getId()+"::"+modes.get(position-1).getDepname());
//		intent.putExtra("id", modes.get(position-1).getId());
//		startActivityForResult(intent, 5);
////		popuWidow(position - 1);
//	}

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

//	@Override
//	public void onRefresh() {
//		new Task().execute("reportdep");
//	}

}
