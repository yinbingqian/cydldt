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
import com.sytm.adapter.ToReportAdapter;
import com.sytm.bean.MyReportModel;
import com.sytm.bean.ToMyReportModel;
import com.sytm.db.ReportDBManager;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.JsonUtils;
import com.sytm.widget.RTPullListView;
import com.sytm.widget.RTPullListView.OnRefreshListener;

@SuppressLint("HandlerLeak")
public class ToMeReportActivity extends Activity {
	private View footer;
	private Button back;
	private List<ToMyReportModel> list = new ArrayList<ToMyReportModel>();
	private List<MyReportModel> sqlitelist = new ArrayList<MyReportModel>();
	private List<ToMyReportModel> listlist = new ArrayList<ToMyReportModel>();
	private MyReportModel model = new MyReportModel();
	private ServiceResult sr = new ServiceResult();
	private ToReportAdapter adapter;
	private RTPullListView listView;
	private SharedPreferences sp;
	private String flag = "", lastmonth = "", lastyear = "", lastday = "";
	private int number = 0;
	private int visibleLastIndex;
	private ReportDBManager dbManager;
	boolean open=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toreport);
		footer = LayoutInflater.from(this).inflate(R.layout.footer, null);
		initialize();
		dbManager = new ReportDBManager(this.getApplicationContext());
		if (dbManager.isData()) {
			sqlitelist=dbManager.query();
			dbManager.closeDB();
			if (sqlitelist!=null&&sqlitelist.size()>0) {
				model = sqlitelist.get(0);
				lastmonth = model.getLastmonth();
				lastyear = model.getLastyear();
				flag = model.getFlag();
				lastday = model.getLastday();
				try {
					list = (List<ToMyReportModel>) JsonUtils
							.parseCollection( model.getReports(),
									List.class, ToMyReportModel.class);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				adapter = new ToReportAdapter(ToMeReportActivity.this, list);
				listView.setAdapter(adapter);
			}
		}
		listView.addFooterView(footer);
		Intent intent = getIntent();
		intent.getIntExtra("TAG", 0);
		new Task().execute("tomereportlist");
	}

	public void initialize() {
		sp = getSharedPreferences("TMMTC", Context.MODE_PRIVATE);
		back = (Button) findViewById(R.id.toreport_left);
		listView = (RTPullListView) findViewById(R.id.toreport_listview);
		back.setOnClickListener(new MyOnClickListener());
		listView.setOnScrollListener(new MyOnScrollListener());
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Task().execute("tomereportlist");
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (list.size() < arg2) {

				} else {
					Intent intent = new Intent(ToMeReportActivity.this,
							MyReportDetailActivity.class);
					intent.putExtra("id", list.get(arg2 - 1).getRepid()
							.toString());
					intent.putExtra("empid", sp.getString("empid", ""));
					intent.putExtra("name", list.get(arg2 - 1).getAddempname()
							.toString());
					intent.putExtra("tag", 1);
					list.get(arg2 - 1).getIsread();
					number = arg2 - 1;
					startActivityForResult(intent, 10);
				}

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10 && resultCode == 20) {
			if (list.size() > 0) {
				list.get(number).setIsread("1");
				adapter.notifyDataSetChanged();
			}
		}
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
			case R.id.toreport_left:
				finish();
				break;
			default:
				break;
			}
		}

	}

	class MyOnScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
			/* 如果滚动到最后一条 */
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (adapter != null) {
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
				if (exeParam.equals("tomereportlist")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("GetToMeReportList");
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.addParameter("flag", "0");
					sc.addParameter("size", "3");
					sr = Network.postDataService(sc);
				} else if (exeParam.equals("nextpage")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("GetToMeReportList");
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.addParameter("flag", flag);
					sc.addParameter("size", "3");
					sc.addParameter("lastyear", lastyear);
					sc.addParameter("lastmonth", lastmonth);
					sc.addParameter("lastday", lastday);
					sr = Network.postDataService(sc);
				} 
			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {
			if (result.equals("tomereportlist")) {
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
						lastday = model.getLastday();
						list = (List<ToMyReportModel>) JsonUtils
								.parseCollection(model.getReports(),
										List.class, ToMyReportModel.class);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter = new ToReportAdapter(ToMeReportActivity.this, list);
					listView.setAdapter(adapter);
					if (listView.getFooterViewsCount() > 0) {
						listView.removeFooterView(footer);
					}
					if (model.getReports()!=null&&!model.getReports().equals("")) {
						if (dbManager.isOpen()) {
							dbManager.clearTable();
							dbManager.addItem(model);
							dbManager.closeDB();
						} else {
							dbManager = null;
							dbManager = new ReportDBManager(ToMeReportActivity.this);
							dbManager.clearTable();
							dbManager.addItem(model);
							dbManager.closeDB();
						}
					}
				}
			}else if (result.equals("nextpage")) {
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					try {
						model = (MyReportModel) JsonUtils
								.parseObject(sr.getData(),
										MyReportModel.class);
						lastmonth = model.getLastmonth();
						lastyear = model.getLastyear();
						flag = model.getFlag();
						lastday = model.getLastday();
						listlist = (List<ToMyReportModel>) JsonUtils
								.parseCollection(
										model.getReports(),
										List.class,
										ToMyReportModel.class);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					list.addAll(listlist);
					adapter.notifyDataSetChanged();
					if (listView.getFooterViewsCount() > 0) {
						listView.removeFooterView(footer);
					}
				}

			} else if (result.equals("ERROR")) {
				myDialog(getResources().getString(R.string.please_error));
			} else {
				myDialog(getResources().getString(R.string.no_thing));
			}
		}
	}

	Dialog dialog;

	public void myDialog(String msg) {
		View v = View.inflate(ToMeReportActivity.this, R.layout.dialog_alone,
				null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		dialog = new Dialog(ToMeReportActivity.this, R.style.dialog_style);
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
