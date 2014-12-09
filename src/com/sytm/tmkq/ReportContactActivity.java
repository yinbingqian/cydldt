package com.sytm.tmkq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lnpdit.chatuidemo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sytm.adapter.ReportContactAdapter;
import com.sytm.bean.ReportContactModel;
import com.sytm.bean.TelBookModel;
import com.sytm.db.ReportContactDBManager;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.JsonUtils;
import com.sytm.application.HanZiUtils;
import com.sytm.widget.AlphabetListReportView;
import com.sytm.widget.AlphabetListReportView.OnItemClickListener;
import com.sytm.widget.AlphabetListReportView.OnRefreshListener;

@SuppressLint("HandlerLeak")
public class ReportContactActivity extends Activity implements
		OnItemClickListener, OnRefreshListener {
	private ImageLoader mImageLoader;
	private List<ReportContactModel> modes = new ArrayList<ReportContactModel>();
	private List<TelBookModel> list = new ArrayList<TelBookModel>();
	private AlphabetListReportView listView;
	private HashMap<String, Integer> alphaIndexer = null;
	private ServiceResult sr = new ServiceResult();
	private ArrayList<TelBookModel> menuList = new ArrayList<TelBookModel>();
	private ReportContactAdapter adapter;
	private EditText put_search;
	private SharedPreferences sp;
	private String empid = "";
	private Button left, right;
	private TextView number;
	private InputMethodManager manager;
	private Dialog dialog;
	Intent intent;
	private boolean open = true;
	private ReportContactDBManager dbManager;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(
						getCurrentFocus().getWindowToken(),
						android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportcontact);
		mImageLoader = ImageLoader.getInstance();
		open = true;
		intent=getIntent();
		listView = (AlphabetListReportView) findViewById(R.id.mylistviewkq);
		put_search = (EditText) findViewById(R.id.serveredit);
		left = (Button) findViewById(R.id.left);
		right = (Button) findViewById(R.id.reportcontact_right);
		number = (TextView) findViewById(R.id.contactnumber);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		listView.setOnItemClickListener(this);
		listView.setonRefreshListener(this);
		left.setOnClickListener(new MyOnClickListener());
		right.setOnClickListener(new MyOnClickListener());
		put_search.addTextChangedListener(new MyaddTextChangedListener());
		number.setText(getResources().getString(R.string.all_contacts)
				+ modes.size() + getResources().getString(R.string.person));
		sp = getSharedPreferences("TMMTC", MODE_PRIVATE);
		empid = sp.getString("empid", "");
		dbManager = new ReportContactDBManager(this.getApplicationContext());
		if (dbManager.isData()) {
			menuList = dbManager.query();
			setData();
		}
		new Task().execute("list");
	}

	/**
	 * 绑定数据
	 * 
	 * @param
	 */

	private void setData() {
		modes.clear();
		if (alphaIndexer != null) {
			alphaIndexer.clear();
			alphaIndexer = null;
		}
		number.setText(getResources().getString(R.string.all_contacts)
				+ modes.size()
				+ getResources().getString(R.string.person));
		if (menuList != null && menuList.size() > 0) {
			if (alphaIndexer == null) {
				alphaIndexer = new HashMap<String, Integer>();
				for (int i = 0; i < menuList.size(); i++) {

					// 取出每一条数据
					String name = menuList.get(i).getName();
					String phone = menuList.get(i).getMobile();
					String empnum = menuList.get(i).getEmpnum();
					String depname = menuList.get(i).getDepname();
					String gender = menuList.get(i).getGender();
					String id = String.valueOf(menuList.get(i).getId());
					String head = menuList.get(i).getHead();
					String postname = menuList.get(i).getPostname();
					String email = menuList.get(i).getEmail();
					String tel = menuList.get(i).getTel();
					int tag = menuList.get(i).getTag();

					// 过滤"+86"
					if (phone.startsWith("+86")) {
						phone = phone.substring(3);
					}

					// 把数据封装在实体中
					ReportContactModel mode = new ReportContactModel();
					mode.setName(name);
					mode.setPhone(phone);
					mode.setEmpnum(empnum);
					mode.setDepname(depname);
					mode.setGender(gender);
					mode.setHead(head);
					mode.setPostname(postname);
					mode.setEmail(email);
					mode.setTel(tel);
					mode.setId(id);
					mode.setTag(tag);
					String firstAlpha = HanZiUtils.toPinYin(name);
					mode.setFirstAlpha(firstAlpha);
					// 将封装的实体加到数组中
					modes.add(mode);

				}
				reportComparator pinyinComparator = new reportComparator();
				// 根据a-z进行排序源数据
				Collections.sort(modes, pinyinComparator);
				for (int i = 0; i < modes.size(); i++) {
					// 处理当点击某一个字母后，其内容出现在屏幕可见状态下的最上面
					ReportContactModel conteactMode = modes.get(i);
					String currentAlpha = conteactMode.getFirstAlpha();
					ReportContactModel mode = (i - 1) >= 0 ? modes.get(i - 1)
							: null;
					String previewStr = "";
					if (mode != null) {
						previewStr = mode.getFirstAlpha();
					}
					if (!previewStr.equals(currentAlpha)) {
						alphaIndexer.put(currentAlpha, i);
					}
				}
				// 把数据设置到adapter
				adapter = new ReportContactAdapter(ReportContactActivity.this,
						mImageLoader);
				adapter.setData(modes);
				listView.setAlphabetIndex(alphaIndexer);
				listView.setAdapter(adapter);
				number.setText(getResources().getString(R.string.all_contacts)
						+ modes.size()
						+ getResources().getString(R.string.person));
			}
		}
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left:
				finish();
				break;
			case R.id.reportcontact_right:
				for (int i = 0; i < menuList.size(); i++) {
					if (menuList.get(i).getTag() == 1) {
						list.add(menuList.get(i));
					}
				}
				Intent intent = new Intent();
				intent.putExtra("list", (Serializable) list);
				setResult(RESULT_OK, intent);
				finish();
				break;
			default:
				break;
			}
		}

	}

	class MyaddTextChangedListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@SuppressLint("DefaultLocale")
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String getname = put_search.getText().toString();
			if (getname == null || getname.equals("")) {
				setData();
			} else {
				modes.clear();
				for (int i = 0; i < menuList.size(); i++) {
					String pinyin = HanZiUtils.updateFormattedText(menuList
							.get(i).getName());
					if (menuList.get(i).getName().toLowerCase()
							.contains(getname.toLowerCase())
							|| pinyin.toLowerCase().contains(
									getname.toLowerCase())) {
						// 取出每一条数据
						String name = menuList.get(i).getName();
						String phone = menuList.get(i).getMobile();
						String empnum = menuList.get(i).getEmpnum();
						String depname = menuList.get(i).getDepname();
						String gender = menuList.get(i).getGender();
						String id = String.valueOf(menuList.get(i).getId());
						String head = menuList.get(i).getHead();
						String postname = menuList.get(i).getPostname();
						String email = menuList.get(i).getEmail();
						String tel = menuList.get(i).getTel();
						int tag = menuList.get(i).getTag();

						// 过滤"+86"
						if (phone.startsWith("+86")) {
							phone = phone.substring(3);
						}

						// 把数据封装在实体中
						ReportContactModel mode = new ReportContactModel();
						mode.setName(name);
						mode.setPhone(phone);
						mode.setEmpnum(empnum);
						mode.setDepname(depname);
						mode.setGender(gender);
						mode.setHead(head);
						mode.setPostname(postname);
						mode.setEmail(email);
						mode.setId(id);
						mode.setTel(tel);
						mode.setTag(tag);
						String firstAlpha = HanZiUtils.toPinYin(name);
						mode.setFirstAlpha(firstAlpha);
						// 将封装的实体加到数组中
						modes.add(mode);

					}
				}
				reportComparator pinyinComparator = new reportComparator();
				// 根据a-z进行排序源数据
				Collections.sort(modes, pinyinComparator);
				listView.setAdapter(adapter);
				number.setText(getResources().getString(R.string.all_contacts)
						+ modes.size()
						+ getResources().getString(R.string.person));
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
				if (exeParam.equals("list")) {
					ServiceContent sc = new ServiceContent();
					sc.addParameter("empid", empid);
					sc.addParameter("depid", intent.getStringExtra("id"));
					sc.setClassname("EmployeeHandler");
					sc.setMethodname("GetReportEmpListByDep");
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
			if (result.equals("list")) {
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					menuList.clear();
					try {
						menuList = (ArrayList<TelBookModel>) JsonUtils
								.parseCollection(sr.getData(), List.class,
										TelBookModel.class);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					modes.clear();
					if (alphaIndexer != null) {
						alphaIndexer.clear();
						alphaIndexer = null;
					}
					setData();
					if (menuList != null && menuList.size() > 0) {
						if (dbManager.isOpen()) {
							dbManager.clearTable();
							dbManager.add(menuList);
							dbManager.closeDB();
						} else {
							dbManager = null;
							dbManager = new ReportContactDBManager(
									ReportContactActivity.this);
							dbManager.clearTable();
							dbManager.add(menuList);
							dbManager.closeDB();
						}
					}
				}
			} else if (result.equals("Loc")) {
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					myDialog(getResources()
							.getString(
									R.string.Request_successful_Please_wait_positioning_notice));
				}
			} else if (result.equals("ERROR")) {
				myDialog(getResources().getString(R.string.please_error));
			} else {
				myDialog(getResources().getString(R.string.no_thing));

			}
		}
	}

	public void myDialog(String msg) {
		View v = View.inflate(this, R.layout.dialog_alone, null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		dialog = new Dialog(this, R.style.dialog_style);
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
		if (open) {
			dialog.show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		AlphabetListReportView.remove();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		open = false;
		if (alphaIndexer != null) {
			alphaIndexer.clear();
			alphaIndexer = null;
		}
		if (dbManager.isOpen()) {
			dbManager.closeDB();
		}
	}

	@Override
	public void onRefresh() {
		new Task().execute("list");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (modes.get(position - 1).getTag() == 0) {
			modes.get(position - 1).setTag(1);
			for (int i = 0; i < menuList.size(); i++) {
				if (modes.get(position - 1).getId()
						.equals(Integer.toString(menuList.get(i).getId()))) {
					menuList.get(i).setTag(1);
				}
			}
		} else {
			modes.get(position - 1).setTag(0);
			for (int i = 0; i < menuList.size(); i++) {
				if (modes.get(position - 1).getId()
						.equals(Integer.toString(menuList.get(i).getId()))) {
					menuList.get(i).setTag(0);
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	class reportComparator implements Comparator<ReportContactModel> {

		public int compare(ReportContactModel o1, ReportContactModel o2) {
			if (o1.getFirstAlpha().equals("#")
					|| o2.getFirstAlpha().equals("@")) {
				return -1;
			} else if (o1.getFirstAlpha().equals("@")
					|| o2.getFirstAlpha().equals("#")) {
				return 1;
			} else {
				return o1.getFirstAlpha().compareTo(o2.getFirstAlpha());
			}
		}
	}
}
