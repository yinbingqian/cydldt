package com.sytm.tmkq;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.adapter.ReportAdapter;
import com.sytm.bean.MyReportListModel;
import com.sytm.bean.MyReportModel;
import com.sytm.db.MyReportDBManager;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.JsonUtils;
import com.sytm.widget.RTPullListView;
import com.sytm.widget.RTPullListView.OnRefreshListener;

@SuppressLint("HandlerLeak")
public class MyReportActivity extends Activity {
	private View footer;
	private Button back, report_right;
	private List<MyReportListModel> list = new ArrayList<MyReportListModel>();
	private List<MyReportListModel> listlist = new ArrayList<MyReportListModel>();
	private MyReportModel model = new MyReportModel();
	private ServiceResult sr = new ServiceResult();
	private ReportAdapter adapter;
	private RTPullListView listView;
	private SharedPreferences sp;
	private String flag = "", lastmonth = "", lastyear = "";
	boolean open=true;
	private List<MyReportModel> sqlitelist = new ArrayList<MyReportModel>();
	private MyReportDBManager dbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		footer = LayoutInflater.from(this).inflate(R.layout.footer, null);
		initialize();
		listView.addFooterView(footer);
		Intent intent = getIntent();
		intent.getIntExtra("TAG", 0);
		dbManager = new MyReportDBManager(this.getApplicationContext());
		if (dbManager.isData()) {
			sqlitelist=dbManager.query();
			dbManager.closeDB();
			if (sqlitelist!=null&&sqlitelist.size()>0) {
				model = sqlitelist.get(0);
				lastmonth = model.getLastmonth();
				lastyear = model.getLastyear();
				flag = model.getFlag();
				try {
					lastmonth = model.getLastmonth();
					lastyear = model.getLastyear();
					flag = model.getFlag();
					list = (List<MyReportListModel>) JsonUtils
							.parseCollection(model.getReports(),
									List.class, MyReportListModel.class);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				adapter = new ReportAdapter(MyReportActivity.this, list);
				listView.setAdapter(adapter);
			}
		}
		new Task().execute("reportlist");
	}

	public void initialize() {
		sp = getSharedPreferences("TMMTC", Context.MODE_PRIVATE);
		back = (Button) findViewById(R.id.report_left);
		report_right = (Button) findViewById(R.id.report_right);
		listView = (RTPullListView) findViewById(R.id.report_listview);
		back.setOnClickListener(new MyOnClickListener());
		report_right.setOnClickListener(new MyOnClickListener());
		listView.setOnScrollListener(new MyOnScrollListener());
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Task().execute("reportlist");
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (list.size() < arg2) {

				} else {
					Intent intent = new Intent(MyReportActivity.this,
							MyReportDetailActivity.class);
					intent.putExtra("id", list.get(arg2 - 1).getId().toString());
					intent.putExtra("empid", sp.getString("empid", ""));
					intent.putExtra("tag", 0);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		open=false;
		if (dbManager.isOpen()) {
			dbManager.closeDB();
		}
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.report_left:
				finish();
				break;
			case R.id.report_right:
				Intent intent8 = new Intent(MyReportActivity.this,NowReportActivity.class);
				startActivity(intent8);
				break;
			default:
				break;
			}
		}

	}

	int visibleLastIndex;

	class MyOnScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
			/* 如果滚动到最后一条 */
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (adapter!=null) {
				int itemsLastIndex = adapter.getCount(); // 数据集最后一项的索引
				int lastIndex = itemsLastIndex;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& visibleLastIndex == lastIndex) {
					listView.addFooterView(footer);
					new Task().execute("nextpage");
				}
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
				if (exeParam.equals("reportlist")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("GetMyReportList");
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.addParameter("flag", "0");
					sc.addParameter("size", "3");
					sr = Network.postDataService(sc);
				} else if (exeParam.equals("nextpage")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("GetMyReportList");
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.addParameter("flag", flag);
					sc.addParameter("size", "3");
					sc.addParameter("lastyear", lastyear);
					sc.addParameter("lastmonth", lastmonth);
					sr = Network.postDataService(sc);
				}
			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {
			if (result.equals("reportlist")) {
				listView.onRefreshComplete();
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					try {
						model = (MyReportModel) JsonUtils.parseObject(
								sr.getData(), MyReportModel.class);
						lastmonth = model.getLastmonth();
						lastyear = model.getLastyear();
						flag = model.getFlag();
						list = (List<MyReportListModel>) JsonUtils
								.parseCollection(model.getReports(),
										List.class, MyReportListModel.class);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter = new ReportAdapter(MyReportActivity.this, list);
					listView.setAdapter(adapter);
					listView.removeFooterView(footer);
					if (model.getReports()!=null&&!model.getReports().equals("")) {
						if (dbManager.isOpen()) {
							dbManager.clearTable();
							dbManager.addItem(model);
							dbManager.closeDB();
						} else {
							dbManager = null;
							dbManager = new MyReportDBManager(MyReportActivity.this);
							dbManager.clearTable();
							dbManager.addItem(model);
							dbManager.closeDB();
						}
					}
				}
			} else if(result.equals("nextpage")){
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					try {
						model = (MyReportModel) JsonUtils.parseObject(
								sr.getData(), MyReportModel.class);
						lastmonth = model.getLastmonth();
						lastyear = model.getLastyear();
						flag = model.getFlag();
						if (!model.getReports().equals("")) {
							listlist = (List<MyReportListModel>) JsonUtils
									.parseCollection(
											model.getReports(),
											List.class,
											MyReportListModel.class);
							list.addAll(listlist);
							adapter.notifyDataSetChanged();
							if (listView.getFooterViewsCount() > 0) {
								listView.removeFooterView(footer);
							}
						} else {
							if (listView.getFooterViewsCount() > 0) {
								listView.removeFooterView(footer);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else if (result.equals("ERROR")) {
				myDialog(getResources().getString(R.string.please_error));
			} else {
				myDialog(getResources().getString(R.string.no_thing));
			}
		}
	}

	Dialog dialog;

	public void myDialog(String msg) {
		View v = View.inflate(MyReportActivity.this, R.layout.dialog_alone,
				null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		dialog = new Dialog(MyReportActivity.this, R.style.dialog_style);
		dialog.setContentView(v);
		dialog.setCancelable(false);
		textView.setText(msg);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		if (open) {
			dialog.show();
		}
	}
}
