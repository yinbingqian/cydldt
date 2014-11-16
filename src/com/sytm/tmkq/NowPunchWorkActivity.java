package com.sytm.tmkq;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.adapter.PunchCardAdapter;
import com.sytm.bean.PunchCardMode;
import com.sytm.bean.ReportMode;
import com.sytm.common.Constant;
import com.sytm.common.PunchModel;
import com.sytm.db.PunchDBManager;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.JsonUtils;
import com.sytm.widget.RTPullListView;
import com.sytm.widget.RTPullListView.OnRefreshListener;

@SuppressLint("HandlerLeak")
public class NowPunchWorkActivity extends Activity {
	private View footer;
	private Button back;
	private List<PunchModel> list = new ArrayList<PunchModel>();
	private ServiceResult sr = new ServiceResult();
	private RTPullListView listView;
	private SharedPreferences sp;
	private int page = 1, total = 0, totalpage = 0;
	private ReportMode reportMode;
	private PunchCardAdapter adapter;
	private boolean bool=true;
	private List<ReportMode> sqlitelist = new ArrayList<ReportMode>();
	private PunchDBManager dbManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.punchrecord);
		footer = LayoutInflater.from(this).inflate(R.layout.footer, null);
		page = 1;
		initialize();
		listView.addFooterView(footer);
		dbManager = new PunchDBManager(this.getApplicationContext());
		if (dbManager.isData()) {
			sqlitelist=dbManager.query();
			dbManager.closeDB();
			if (sqlitelist!=null&&sqlitelist.size()>0) {
				reportMode = new ReportMode();
				reportMode = sqlitelist.get(0);
				try {
					total = reportMode.getTotal();
					totalpage = total / Constant.size;
					if (total > Constant.size) {
						if (total % Constant.size != 0) {
							totalpage++;
						}
					} else {
						totalpage = 1;
					}
					page = 1;
					list = (List<PunchModel>) JsonUtils.parseCollection(
							reportMode.getRows(), List.class,
							PunchModel.class);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				adapter = new PunchCardAdapter(NowPunchWorkActivity.this,
						list);
				listView.setAdapter(adapter);
			}
		}
		new Task().execute("punchcard");
	}

	public void initialize() {
		back = (Button) findViewById(R.id.back);
		sp = getSharedPreferences("TMMTC", Context.MODE_PRIVATE);
		listView = (RTPullListView) findViewById(R.id.pubch_listview);
		back.setOnClickListener(new MyOnClickListener());
		listView.setOnScrollListener(new MyOnScrollListener());
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Task().execute("punchcard");
			}
		});
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		bool=false;
		page = 1;
		total = 0;
		totalpage = 0;
//		if (dbManager.isOpen()) {
//			dbManager.closeDB();
//		}
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
			if (adapter != null) {
				int itemsLastIndex = adapter.getCount(); // 数据集最后一项的索引
				int lastIndex = itemsLastIndex;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& visibleLastIndex == lastIndex) {
					if (page < totalpage) {
						listView.addFooterView(footer);
						page = page + 1;
						new Task().execute("punchcardnext");
					}
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
				if (exeParam.equals("punchcard")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("AttenceHandler");
					sc.setMethodname("GetCheckInList");
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.addParameter("page", "1");
					sc.addParameter("size", String.valueOf(Constant.size));
					sr = Network.postDataService(sc);
				} else if (exeParam.equals("punchcardnext")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("AttenceHandler");
					sc.setMethodname("GetList");
					sc.addParameter("empid", sp.getString("empid", ""));
					sc.addParameter("page", String.valueOf(page));
					sc.addParameter("size", String.valueOf(Constant.size));
					sr = Network.postDataService(sc);
				}
			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {

			if (result.equals("punchcard")) {
				listView.onRefreshComplete();
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					reportMode = new ReportMode();
					try {
						reportMode = (ReportMode) JsonUtils.parseObject(
								sr.getData(), ReportMode.class);
						total = reportMode.getTotal();
						totalpage = total / Constant.size;
						if (total > Constant.size) {
							if (total % Constant.size != 0) {
								totalpage++;
							}
						} else {
							totalpage = 1;
						}
						page = 1;
						list = (List<PunchModel>) JsonUtils.parseCollection(
								reportMode.getRows(), List.class,
								PunchModel.class);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter = new PunchCardAdapter(NowPunchWorkActivity.this,
							list);
					listView.setAdapter(adapter);
					listView.removeFooterView(footer);
					if (reportMode.getRows()!=null&&!reportMode.getRows().equals("")) {
						if (dbManager.isOpen()) {
							dbManager.clearTable();
							dbManager.addItem(reportMode);
							dbManager.closeDB();
						} else {
							dbManager = null;
							dbManager = new PunchDBManager(NowPunchWorkActivity.this);
							dbManager.clearTable();
							dbManager.addItem(reportMode);
							dbManager.closeDB();
						}
					}
				}
			} else if (result.equals("punchcardnext")) {
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					reportMode = new ReportMode();
					try {
						reportMode = (ReportMode) JsonUtils.parseObject(
								sr.getData(), ReportMode.class);
						List<PunchModel> nextList;
						nextList = (List<PunchModel>) JsonUtils
								.parseCollection(reportMode.getRows(),
										List.class, PunchModel.class);
						list.addAll(nextList);
						adapter.notifyDataSetChanged();
						if (listView.getFooterViewsCount() > 0) {
							listView.removeFooterView(footer);
						}
					} catch (JSONException e) {
						e.printStackTrace();
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
		View v = View.inflate(NowPunchWorkActivity.this, R.layout.dialog_alone,
				null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		dialog = new Dialog(NowPunchWorkActivity.this, R.style.dialog_style);
		dialog.setContentView(v);
		dialog.setCancelable(false);
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
		if (bool) {
			dialog.show();
		}
	}
	
}
