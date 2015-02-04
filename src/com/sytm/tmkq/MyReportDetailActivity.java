package com.sytm.tmkq;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.lnpdit.chatuidemo.R;
import com.sytm.adapter.MyReportDetailAdapter;
import com.sytm.bean.AttachFileModel;
import com.sytm.bean.ReportDetail_ReplyModel;
import com.sytm.common.Constant;
import com.sytm.netcore.AttachmentDownUtils;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.JsonUtils;
import com.sytm.view.LoadingDialog;

@SuppressLint("InlinedApi")
public class MyReportDetailActivity extends Activity {
	private TextView report_detail_date, report_detail_time,
			report_detail_content, report_detail_huifu, title, nei_title,
			reportreply_name, report_attach1, report_attach2, report_attach3, report_attach4, report_attach5, report_attach6,report_detail_title;
	private ListView listView;
	private InputMethodManager manager;
	private View top;
	private ServiceResult sr = new ServiceResult();
	private Button reply_detail_left,report_attach1_btn,report_attach2_btn,report_attach3_btn,report_attach4_btn,report_attach5_btn,report_attach6_btn;
	private Intent intent;
	private ReportDetailModel detailModel;
	private MyReportDetailAdapter adapter;
	private EditText report_content;
	private String toempid = "", repid = "", empid = "", toname = "", name = "",filepath1="",filepath2="",filepath3="",filepath4="",filepath5="",filepath6="";
	private ImageView report_detail_topdown,report_attach1_img,report_attach2_img,report_attach3_img,report_attach4_img,report_attach5_img,report_attach6_img;
	private List<ReportDetail_ReplyModel> list = new ArrayList<ReportDetail_ReplyModel>();
	private List<AttachFileModel> fileModels = new ArrayList<AttachFileModel>();
	private LinearLayout reportreply_view, cancel_reply, report_reply,report_attach;
	private LinearLayout report_attach1_rel,report_attach2_rel,report_attach3_rel,report_attach4_rel,report_attach5_rel,report_attach6_rel;
	private int tag = 0;
	private Dialog dialog2;
	private boolean open = true;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (reportreply_view.isShown()) {
				reportreply_view.setVisibility(View.GONE);
				listView.setEnabled(true);
				return true;
			} else {
				if (tag == 1) {
					Intent intent = new Intent();
					setResult(20, intent);
					finish();
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myreport_detail);
		top = LayoutInflater.from(this).inflate(
				R.layout.myreport_detail_topview, null);
		report_detail_date = (TextView) top
				.findViewById(R.id.report_detail_date);
		report_detail_time = (TextView) top
				.findViewById(R.id.report_detail_time);
		report_detail_content = (TextView) top
				.findViewById(R.id.report_detail_content);
		report_detail_title = (TextView) top
				.findViewById(R.id.report_detail_title);
		report_detail_huifu = (TextView) top.findViewById(R.id.report_detail_huifu);
		report_attach =(LinearLayout) top.findViewById(R.id.report_attach);
		report_attach1 = (TextView) top.findViewById(R.id.report_attach1);
		report_attach2 = (TextView) top.findViewById(R.id.report_attach2);
		report_attach3 = (TextView) top.findViewById(R.id.report_attach3);
		report_attach4 = (TextView) top.findViewById(R.id.report_attach4);
		report_attach5 = (TextView) top.findViewById(R.id.report_attach5);
		report_attach6 = (TextView) top.findViewById(R.id.report_attach6);
		report_attach1_btn = (Button) top.findViewById(R.id.report_attach1_btn);
		report_attach2_btn = (Button) top.findViewById(R.id.report_attach2_btn);
		report_attach3_btn = (Button) top.findViewById(R.id.report_attach3_btn);
		report_attach4_btn = (Button) top.findViewById(R.id.report_attach4_btn);
		report_attach5_btn = (Button) top.findViewById(R.id.report_attach5_btn);
		report_attach6_btn = (Button) top.findViewById(R.id.report_attach6_btn);
		report_attach1_img = (ImageView) top.findViewById(R.id.report_attach1_img);
		report_attach2_img = (ImageView) top.findViewById(R.id.report_attach2_img);
		report_attach3_img = (ImageView) top.findViewById(R.id.report_attach3_img);
		report_attach4_img = (ImageView) top.findViewById(R.id.report_attach4_img);
		report_attach5_img = (ImageView) top.findViewById(R.id.report_attach5_img);
		report_attach6_img = (ImageView) top.findViewById(R.id.report_attach6_img);
		report_attach1_rel = (LinearLayout) top.findViewById(R.id.report_attach1_rel);
		report_attach2_rel = (LinearLayout) top.findViewById(R.id.report_attach2_rel);
		report_attach3_rel = (LinearLayout) top.findViewById(R.id.report_attach3_rel);
		report_attach4_rel = (LinearLayout) top.findViewById(R.id.report_attach4_rel);
		report_attach5_rel = (LinearLayout) top.findViewById(R.id.report_attach5_rel);
		report_attach6_rel = (LinearLayout) top.findViewById(R.id.report_attach6_rel);
		report_attach.setVisibility(View.GONE);
		report_attach.setEnabled(false);
		listView = (ListView) findViewById(R.id.report_detail_listview);
		reply_detail_left = (Button) findViewById(R.id.reply_detail_left);
		title = (TextView) findViewById(R.id.title);
		nei_title = (TextView) top.findViewById(R.id.nei_title);
		report_detail_topdown = (ImageView) top
				.findViewById(R.id.report_detail_topdown);
		reportreply_view = (LinearLayout) findViewById(R.id.reportreply_view);
		cancel_reply = (LinearLayout) findViewById(R.id.cancel_reply);
		report_reply = (LinearLayout) findViewById(R.id.report_reply);
		report_content = (EditText) findViewById(R.id.report_content);
		reportreply_name = (TextView) findViewById(R.id.reportreply_name);
		reportreply_view.setVisibility(View.GONE);
		listView.setEnabled(true);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		intent = getIntent();
		empid = intent.getStringExtra("empid");
		name = intent.getStringExtra("name");
		repid = intent.getStringExtra("id");
		tag = intent.getIntExtra("tag", 0);
		if (tag == 0) {
			report_detail_huifu.setVisibility(View.GONE);
			title.setText(getResources().getString(R.string.myreport));
			nei_title.setText(getResources().getString(R.string.myreport));
			new Task().execute("MyreportDetail");
		} else {
			report_detail_huifu.setVisibility(View.VISIBLE);
			title.setText(getResources().getString(R.string.tomyreport));
			nei_title.setText(name);
			new Task().execute("isread");
		}
		listView.addHeaderView(top);
		adapter = new MyReportDetailAdapter(MyReportDetailActivity.this, list,
				empid, tag);
		listView.setAdapter(adapter);
		report_detail_date.setVisibility(View.GONE);
		report_detail_huifu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (detailModel!=null) {
					toempid = detailModel.getEmpid();
					toname = intent.getStringExtra("name");
					report_content.setText("");
					reportreply_view.setVisibility(View.VISIBLE);
					reportreply_name.setText("@" + toname);
					listView.setEnabled(false);
				}
			}
		});
		reply_detail_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tag == 1) {
					Intent intent = new Intent();
					setResult(20, intent);
				}
				finish();

			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				if (!ServiceUtils.isServiceRunning(MyReportDetailActivity.this, Constant.Locationservice)) {
//					myDialog(getResources().getString(R.string.servicesclosetips));
//					return;
//				}
				if (arg2 == 0) {

				} else {

					repid = list.get(arg2 - 1).getRepid();
					if (list.get(arg2 - 1).getRepempid().equals(empid)) {
						toempid = list.get(arg2 - 1).getToempid();
						toname = list.get(arg2 - 1).getToempname();
					} else {
						toempid = list.get(arg2 - 1).getRepempid();
						toname = list.get(arg2 - 1).getRepempname();
					}
					report_content.setText("");
					reportreply_view.setVisibility(View.VISIBLE);
					reportreply_name.setText("@" + toname);
					listView.setEnabled(false);
				}

			}
		});
		cancel_reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reportreply_view.setVisibility(View.GONE);
				listView.setEnabled(true);
			}
		});
		report_reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (!ServiceUtils.isServiceRunning(MyReportDetailActivity.this, Constant.Locationservice)) {
//					myDialog(getResources().getString(R.string.servicesclosetips));
//					return;
//				}
				if (report_content.getText().toString() == null
						|| report_content.getText().toString().equals("")) {
					myDialog(getResources().getString(
							R.string.please_enter_content));
				} else {
					dialog2 = new LoadingDialog().createLoadingDialog(
							MyReportDetailActivity.this, getResources()
									.getString(R.string.sending),true);
					dialog2.show();
					new Task().execute("reportreply");
				}
			}
		});
		report_attach1_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!filepath1.equals("")) {
					Handler handler = new Handler();
					AttachmentDownUtils filedownload = new AttachmentDownUtils(MyReportDetailActivity.this, handler, filepath1);
					filedownload.run();
				}
			}
		});
		report_attach2_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!filepath2.equals("")) {
					Handler handler = new Handler();
					AttachmentDownUtils filedownload = new AttachmentDownUtils(MyReportDetailActivity.this, handler, filepath2);
					filedownload.run();
				}
			}
		});
		report_attach3_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!filepath3.equals("")) {
					Handler handler = new Handler();
					AttachmentDownUtils filedownload = new AttachmentDownUtils(MyReportDetailActivity.this, handler, filepath3);
					filedownload.run();
				}
			}
		});
		report_attach4_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!filepath4.equals("")) {
					Handler handler = new Handler();
					AttachmentDownUtils filedownload = new AttachmentDownUtils(MyReportDetailActivity.this, handler, filepath4);
					filedownload.run();
				}
			}
		});
		report_attach5_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!filepath5.equals("")) {
					Handler handler = new Handler();
					AttachmentDownUtils filedownload = new AttachmentDownUtils(MyReportDetailActivity.this, handler, filepath5);
					filedownload.run();
				}
			}
		});
		report_attach6_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!filepath6.equals("")) {
					Handler handler = new Handler();
					AttachmentDownUtils filedownload = new AttachmentDownUtils(MyReportDetailActivity.this, handler, filepath6);
					filedownload.run();
				}
			}
		});
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
				if (exeParam.equals("MyreportDetail")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("GetReportInfo");
					sc.addParameter("empid", intent.getStringExtra("empid"));
					sc.addParameter("id", intent.getStringExtra("id"));
					sr = Network.postDataService(sc);
				}else if (exeParam.equals("isread")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("SetRead");
					sc.addParameter("empid",intent.getStringExtra("empid"));
					sc.addParameter("repid", repid);
					sr = Network.postDataService(sc);
				}else
//				if (exeParam.equals("MyreportDetaillist")) {
//					ServiceContent sc = new ServiceContent();
//					sc.setClassname("MReportHandler");
//					sc.setMethodname("GetReplyReportList");
//					sc.addParameter("repid", idredpid);
//					sr = Network.postDataService(sc);
//				}
				if (exeParam.equals("reportreply")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("ReplyReport");
					sc.addParameter("empid", empid);
					sc.addParameter("toempid", toempid);
					sc.addParameter("detail", report_content.getText()
							.toString());
					sc.addParameter("repid", repid);
					sr = Network.postDataService(sc);
				}

			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {
			/*
			 * if (null != mProgressDialog && mProgressDialog.isShowing()) {
			 * mProgressDialog.dismiss(); }
			 */
			// mDialog.dismiss();
			if (result.equals("MyreportDetail")) {
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					detailModel = new ReportDetailModel();
					try {
						detailModel = (ReportDetailModel) JsonUtils.parseObject(sr.getData(),ReportDetailModel.class);
						fileModels = (List<AttachFileModel>)JsonUtils.parseCollection(detailModel.getFiles().toString(),  List.class, AttachFileModel.class);
						list = (List<ReportDetail_ReplyModel>) JsonUtils.parseCollection(detailModel.getReply(), List.class,ReportDetail_ReplyModel.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					report_detail_content.setText(detailModel.getReport());
					report_detail_time.setText(detailModel.getAdddate());
					report_detail_title.setText(detailModel.getTitle());
					if (fileModels.size()!=0) {
						report_attach.setVisibility(View.VISIBLE);
					}
					for (int i = 0; i < fileModels.size(); i++) {
						String fileformat = fileModels.get(i).getFilename().substring(fileModels.get(i).getFilename().lastIndexOf(".")+1);
						if (i == 0) {
							report_attach1.setText(fileModels.get(i).getFilename());
							filepath1 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.GONE);
							report_attach3_rel.setVisibility(View.GONE);
							report_attach4_rel.setVisibility(View.GONE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach1_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 1) {
							report_attach2.setText(fileModels.get(i).getFilename());
							filepath2 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.GONE);
							report_attach4_rel.setVisibility(View.GONE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach2_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 2) {
							report_attach3.setText(fileModels.get(i).getFilename());
							filepath3 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.GONE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach3_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 3) {
							report_attach4.setText(fileModels.get(i).getFilename());
							filepath4 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.VISIBLE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach4_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 4) {
							report_attach5.setText(fileModels.get(i).getFilename());
							filepath5 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.VISIBLE);
							report_attach5_rel.setVisibility(View.VISIBLE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach5_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 5) {
							report_attach6.setText(fileModels.get(i).getFilename());
							filepath6 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.VISIBLE);
							report_attach5_rel.setVisibility(View.VISIBLE);
							report_attach6_rel.setVisibility(View.VISIBLE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach6_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.txt_icon);
							}
						}
					}
//					if (tag == 0) {
//						idredpid = detailModel.getId();
//					} else {
//						idredpid = intent.getStringExtra("id");
//					}
					if (list.size() > 0) {
						report_detail_topdown.setVisibility(View.VISIBLE);
					}
					adapter = new MyReportDetailAdapter(
							MyReportDetailActivity.this, list, empid, tag);
					listView.setAdapter(adapter);
				}

			}  else if (result.equals("isread")) {
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					detailModel = new ReportDetailModel();
					try {
						detailModel = (ReportDetailModel) JsonUtils.parseObject(sr.getData(),ReportDetailModel.class);
						fileModels = (List<AttachFileModel>)JsonUtils.parseCollection(detailModel.getFiles().toString(),  List.class, AttachFileModel.class);
						list = (List<ReportDetail_ReplyModel>) JsonUtils.parseCollection(detailModel.getReply(), List.class,ReportDetail_ReplyModel.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					report_detail_content.setText(detailModel.getReport());
					report_detail_time.setText(detailModel.getAdddate());
					if (fileModels.size()!=0) {
						report_attach.setVisibility(View.VISIBLE);
					}
					for (int i = 0; i < fileModels.size(); i++) {
						String fileformat = fileModels.get(i).getFilename().substring(fileModels.get(i).getFilename().lastIndexOf(".")+1);
						if (i == 0) {
							report_attach1.setText(fileModels.get(i).getFilename());
							filepath1 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.GONE);
							report_attach3_rel.setVisibility(View.GONE);
							report_attach4_rel.setVisibility(View.GONE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach1_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach1_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 1) {
							report_attach2.setText(fileModels.get(i).getFilename());
							filepath2 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.GONE);
							report_attach4_rel.setVisibility(View.GONE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach2_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach2_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 2) {
							report_attach3.setText(fileModels.get(i).getFilename());
							filepath3 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.GONE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach3_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach3_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 3) {
							report_attach4.setText(fileModels.get(i).getFilename());
							filepath4 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.VISIBLE);
							report_attach5_rel.setVisibility(View.GONE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach4_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach4_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 4) {
							report_attach5.setText(fileModels.get(i).getFilename());
							filepath5 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.VISIBLE);
							report_attach5_rel.setVisibility(View.VISIBLE);
							report_attach6_rel.setVisibility(View.GONE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach5_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach5_img.setImageResource(R.drawable.txt_icon);
							}
						}else if (i == 5) {
							report_attach6.setText(fileModels.get(i).getFilename());
							filepath6 = fileModels.get(i).getFilepath();
							report_attach1_rel.setVisibility(View.VISIBLE);
							report_attach2_rel.setVisibility(View.VISIBLE);
							report_attach3_rel.setVisibility(View.VISIBLE);
							report_attach4_rel.setVisibility(View.VISIBLE);
							report_attach5_rel.setVisibility(View.VISIBLE);
							report_attach6_rel.setVisibility(View.VISIBLE);
							if (Constant.ImgFormat.contains(fileformat)) {
								report_attach6_img.setImageResource(R.drawable.img_icon);
							}else if(Constant.WordFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.word_icon);
							}else if(Constant.XlsFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.xls_icon);
							}else if(Constant.PptFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.ppt_icon);
							}else if(Constant.TxtFormat.contains(fileformat)){
								report_attach6_img.setImageResource(R.drawable.txt_icon);
							}
						}
					}
//					if (tag == 0) {
//						idredpid = detailModel.getId();
//					} else {
//						idredpid = intent.getStringExtra("id");
//					}
					if (list.size() > 0) {
						report_detail_topdown.setVisibility(View.VISIBLE);
					}
					adapter = new MyReportDetailAdapter(
							MyReportDetailActivity.this, list, empid, tag);
					listView.setAdapter(adapter);
				}
			}
//			else if (result.equals("MyreportDetaillist")) {
//				if (sr.GetIsError()) {
//					myDialog(sr.getMessage());
//				} else {
//					try {
//						list = (List<ReportDetail_ReplyModel>) JsonUtils
//								.parseCollection(sr.getData(), List.class,
//										ReportDetail_ReplyModel.class);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					if (list.size() > 0) {
//						report_detail_topdown.setVisibility(View.VISIBLE);
//					}
//					adapter = new MyReportDetailAdapter(
//							MyReportDetailActivity.this, list, empid, tag);
//					listView.setAdapter(adapter);
//				}
//
//			} 
			else if (result.equals("reportreply")) {
				if (dialog2.isShowing()) {
					dialog2.dismiss();
				}
				if (sr.GetIsError()) {
					myDialog(sr.getMessage());
				} else {
					myDialog(sr.getMessage());
					new Task().execute("MyreportDetail");
					report_content.setText("");
					reportreply_view.setVisibility(View.GONE);
					listView.setEnabled(true);
				}

			} else if (result.equals("ERROR")) {
				myDialog(getResources().getString(R.string.please_error));
			} else {
				myDialog(getResources().getString(R.string.no_thing));
			}
		}
	}

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

	Dialog dialog;

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
	protected void onDestroy() {
		super.onDestroy();
		open = false;
	}

}
