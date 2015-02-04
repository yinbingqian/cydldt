package com.sytm.tmkq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lnpdit.chatuidemo.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sytm.bean.TelBookModel;
import com.sytm.common.Constant;
import com.sytm.db.SendReportDBManager;
import com.sytm.netcore.Network;
import com.sytm.netcore.ServiceContent;
import com.sytm.netcore.ServiceResult;
import com.sytm.util.ImageUtils;
import com.sytm.application.LocationUtils;
import com.sytm.view.FlowLayout;
import com.sytm.view.LoadingDialog;

public class NowReportActivity extends Activity implements OnClickListener {
	private Button back;
	private LinearLayout send, cancel_report;
	private EditText title;
	private String titleString;
	private EditText content;
	private String conString;
	private InputMethodManager manager;
	private ServiceResult sr = new ServiceResult();
	private SharedPreferences sp;
	private ImageView send_weizhi;
	private String tishi = "", lng = "", lat = "", GetType = "", path1 = "",
			path2 = "", path3 = "", path4 = "", path5 = "", path6 = "";
	private List<TelBookModel> list = new ArrayList<TelBookModel>();
	private List<TelBookModel> bookModels = new ArrayList<TelBookModel>();
	private StringBuffer ids = new StringBuffer();;
	private View viewtext;
	private Intent intentcontact;
	private FlowLayout flowLayout;
	private int tag = 0, scress = 0;
	private Dialog dialog2;
	private TextView send_bg_name, sendreport_name1, sendreport_name2,
			sendreport_name3;
	private SendReportDBManager dbManager;
	private boolean btn1 = true, btn2 = true, btn3 = true;
	private BroadcastReceiver myReceiver;
	private Dialog dialog;
	private boolean open = true;
	private final int UP_LOADING = 1;
	private int SCAN = 1;
	private final int LEN = 500;
	private int num = 0;
	private Bitmap smallBitmap;
	private ImageView imageView, imageView1, imageView2,imageView3,imageView4,imageView5;
	private TextView filename1, filename2, filename3,filename4,filename5,filename6;
	/***
	 * 使用照相机拍照获取图片
	 */
	private final int SELECT_PIC_BY_TACK_PHOTO = 100;
	/***
	 * 使用相册中的图片
	 */
	private final int SELECT_PIC_BY_PICK_PHOTO = 200;
	private PopupWindow popupWindow;
	private String picpathmain;
	private File file;
	private boolean Exist1 = false, Exist2 = false, Exist3 = false, Exist4 = false, Exist5 = false, Exist6 = false;
	private int exist = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sendreport);
		sp = this.getSharedPreferences("TMMTC", MODE_PRIVATE);
		back = (Button) findViewById(R.id.back);
		flowLayout = (FlowLayout) findViewById(R.id.send_name);
		send = (LinearLayout) findViewById(R.id.send_report);
		cancel_report = (LinearLayout) findViewById(R.id.cancel_report);
		title = (EditText) findViewById(R.id.report_title);
		content = (EditText) findViewById(R.id.report_content);
		send_weizhi = (ImageView) findViewById(R.id.send_weizhi);
		send_bg_name = (TextView) findViewById(R.id.send_bg_name);
		sendreport_name1 = (TextView) findViewById(R.id.sendreport_name1);
		sendreport_name2 = (TextView) findViewById(R.id.sendreport_name2);
		sendreport_name3 = (TextView) findViewById(R.id.sendreport_name3);
		imageView = (ImageView) this.findViewById(R.id.imageView);
		imageView1 = (ImageView) this.findViewById(R.id.imageView1);
		imageView2 = (ImageView) this.findViewById(R.id.imageView2);
		imageView3 = (ImageView) this.findViewById(R.id.imageView3);
		imageView4 = (ImageView) this.findViewById(R.id.imageView4);
		imageView5 = (ImageView) this.findViewById(R.id.imageView5);
		filename1 = (TextView) findViewById(R.id.filename1);
		filename2 = (TextView) findViewById(R.id.filename2);
		filename3 = (TextView) findViewById(R.id.filename3);
		filename4 = (TextView) findViewById(R.id.filename4);
		filename5 = (TextView) findViewById(R.id.filename5);
		filename6 = (TextView) findViewById(R.id.filename6);
		sendreport_name1.setOnClickListener(this);
		sendreport_name2.setOnClickListener(this);
		sendreport_name3.setOnClickListener(this);
		dbManager = new SendReportDBManager(NowReportActivity.this);
		flowLayout.removeAllViewsInLayout();
		bookModels = dbManager.query("sendreport");
		intentcontact = getIntent();
		tag = intentcontact.getIntExtra("tag", 0);
		if (tag == 0) {
			send_bg_name.setVisibility(View.VISIBLE);
			for (int i = 0; i < bookModels.size(); i++) {
				if (i == 0) {
					sendreport_name1.setText(bookModels.get(i).getName());
				} else if (i == 1) {
					sendreport_name2.setText(bookModels.get(i).getName());
				}
				if (i == 2) {
					sendreport_name3.setText(bookModels.get(i).getName());
				}
			}

		} else {
			send_bg_name.setVisibility(View.GONE);
			TelBookModel bookModel = new TelBookModel();
			bookModel.setName(intentcontact.getStringExtra("name"));
			bookModel
					.setId(Integer.parseInt(intentcontact.getStringExtra("id")));
			list.add(bookModel);
			viewtext = LayoutInflater.from(this).inflate(R.layout.view, null);
			Button box = (Button) viewtext.findViewById(R.id.txt);
			box.setText(intentcontact.getStringExtra("name"));
			box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					v.setClickable(false);
					flowLayout.removeView(v);
					if (tag == 1) {
						list.clear();
					}
					send_bg_name.setVisibility(View.VISIBLE);
				}
			});
			flowLayout.addView(box);
		}
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		IntentFilter filter = new IntentFilter();
		filter.addAction("report");
		filter.setPriority(Integer.MAX_VALUE);
		myReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getIntExtra("TAGS", 10) == 0) {
					tishi = getResources().getString(
							R.string.Please_check_network_try_again);
					myDialog(tishi);
					if (dialog2.isShowing()) {
						dialog2.dismiss();
					}
				} else if (intent.getIntExtra("TAGS", 10) == 1) {
					lng = intent.getStringExtra("Lng");
					lat = intent.getStringExtra("Lat");
					GetType = intent.getStringExtra("GetType");
					new Task().execute("report");
				}
			}
		};
		registerReceiver(myReceiver, filter);
		send_weizhi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NowReportActivity.this,
						ReportDepContactsActivity.class);
				startActivityForResult(intent, 10);
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		cancel_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (!ServiceUtils.isServiceRunning(NowReportActivity.this,
				// Constant.Locationservice)) {
				// myDialog(getResources().getString(R.string.servicesclosetips));
				// return;
				// }
				titleString = title.getText().toString();
				conString = content.getText().toString();
				if (titleString == null || titleString.equals("")) {
					myDialog("请输入标题");
				} else if (conString == null || conString.equals("")) {
					myDialog(getResources().getString(
							R.string.Please_enter_description));
				} else {
					if (list == null || flowLayout.getChildCount() == 0) {
						myDialog(getResources().getString(
								R.string.select_contact));
					} else {
						dbManager.clearTable("sendreport");
						List<TelBookModel> listss = new ArrayList<TelBookModel>();
						if (list.size() > 3) {
							listss.add(list.get(list.size() - 1));
							listss.add(list.get(list.size() - 2));
							listss.add(list.get(list.size() - 3));
							dbManager.add(listss, "sendreport");
						} else {
							if (list.size() != 0) {
								dbManager.add(list, "sendreport");
							}
						}
						dialog2 = new LoadingDialog().createLoadingDialog(
								NowReportActivity.this, getResources()
										.getString(R.string.sending), true);
						dialog2.show();
						for (int j = 0; j < list.size(); j++) {
							if (j == list.size() - 1) {
								ids.append(String.valueOf(list.get(j).getId())
										+ "");
							} else {
								ids.append(String.valueOf(list.get(j).getId())
										+ ",");
							}
						}
						LocationUtils lu = new LocationUtils();
						lu.GetLocationNow(NowReportActivity.this, "10", "");
					}
				}
			}
		});
		// report_attachment.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(NowReportActivity.this,
		// SdcardListActivity.class);
		// startActivityForResult(intent, UP_LOADING);
		// }
		// });
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popuWidow(v, 0);
			}
		});
		imageView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popuWidow(v, 1);
			}
		});
		imageView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popuWidow(v, 2);
			}
		});
		imageView3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popuWidow(v, 3);
			}
		});
		imageView4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popuWidow(v, 4);
			}
		});
		imageView5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popuWidow(v, 5);
			}
		});
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public void popuWidow(View v, int number) {
		// 加载PopupWindow的布局文件
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.file_path, null);
		// 加载PopupWindow的媒介布局文件
		Button xiangce = (Button) view.findViewById(R.id.xiangce);
		Button zhaoxiang = (Button) view.findViewById(R.id.paizhao);
		Button quxiao = (Button) view.findViewById(R.id.quxiao);
		Button sdcard = (Button) view.findViewById(R.id.sdcard);
		num = number;
		xiangce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pickPhoto();
				popupWindow.dismiss();
			}
		});
		zhaoxiang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				takePhoto();
				popupWindow.dismiss();
			}
		});
		quxiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				num = 10;
				popupWindow.dismiss();
			}
		});
		sdcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exist = 0;
				if (!Exist1) {
					exist++;
				}
				if (!Exist2) {
					exist++;
				}
				if (!Exist3) {
					exist++;
				}
				if (!Exist4) {
					exist++;
				}
				if (!Exist5) {
					exist++;
				}
				if (!Exist6) {
					exist++;
				}
				if (exist == 0) {
					myDialog("已达到添加附件最大值，不可添加！");
					return;
				}
				if (ImageUtils.checkSDCardAvailable()) {
					Intent intent = new Intent(NowReportActivity.this,
							SdcardListActivity.class);
					intent.putExtra("exist", exist);
					startActivityForResult(intent, UP_LOADING);
				} else {
					myDialog(getResources()
							.getString(R.string.sdcard_not_exist));
				}
				popupWindow.dismiss();
			}
		});
		// 实例化PopupWindow中的组件
		popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
				location[0] + v.getWidth(), location[1]);
	}

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		// 执行拍照前，应该先判断SD卡是否存在
		if (ImageUtils.checkSDCardAvailable()) {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
			/***
			 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
			 * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
			 */
			picpathmain = Environment.getExternalStorageDirectory().getPath()
					+ "/sytm/image/";
			File dir = new File(picpathmain);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(picpathmain, "image.jpg");
			file.delete();
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.i("file", file.getPath() + "");
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(file));
			/** ----------------- */
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
		} else {
			myDialog(getResources().getString(R.string.sdcard_not_exist));
		}
	}

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		if (ImageUtils.checkSDCardAvailable()) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
		} else {
			myDialog(getResources().getString(R.string.sdcard_not_exist));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		SCAN = 1;
		switch (requestCode) {
		case UP_LOADING:
			String path111 = data.getStringExtra("path1");
			String path222 = data.getStringExtra("path2");
			String path333 = data.getStringExtra("path3");
			String path444 = data.getStringExtra("path4");
			String path555 = data.getStringExtra("path5");
			String path666 = data.getStringExtra("path6");
			String filefromt1 = path111.substring(path111.lastIndexOf(".") + 1);
			String filefromt2 = path222.substring(path222.lastIndexOf(".") + 1);
			String filefromt3 = path333.substring(path333.lastIndexOf(".") + 1);
			String filefromt4 = path444.substring(path444.lastIndexOf(".") + 1);
			String filefromt5 = path555.substring(path555.lastIndexOf(".") + 1);
			String filefromt6 = path666.substring(path666.lastIndexOf(".") + 1);
			if (!filefromt1.equals("")) {
				if (!Exist1) {
					if (Constant.ImgFormat.contains(filefromt1)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path111, options);
						imageView.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt1)) {
						imageView.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt1)) {
						imageView.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt1)) {
						imageView.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt1)) {
						imageView.setImageResource(R.drawable.word_icon);
					}
					filename1.setText(path111.substring(path111
							.lastIndexOf("/") + 1));
					Exist1 = !Exist1;
					path1 = path111;
				} else if (!Exist2) {
					if (Constant.ImgFormat.contains(filefromt1)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path111, options);
						imageView1.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt1)) {
						imageView1.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt1)) {
						imageView1.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt1)) {
						imageView.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt1)) {
						imageView1.setImageResource(R.drawable.word_icon);
					}
					filename2.setText(path111.substring(path111
							.lastIndexOf("/") + 1));
					Exist2 = !Exist2;
					path2 = path222;
				} else if (!Exist3) {
					if (Constant.ImgFormat.contains(filefromt1)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path111, options);
						imageView.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt1)) {
						imageView2.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt1)) {
						imageView2.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt1)) {
						imageView2.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt1)) {
						imageView2.setImageResource(R.drawable.word_icon);
					}
					filename3.setText(path111.substring(path111
							.lastIndexOf("/") + 1));
					Exist3 = !Exist3;
					path3 = path333;
				}else if (!Exist4) {
					if (Constant.ImgFormat.contains(filefromt1)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path111, options);
						imageView3.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt1)) {
						imageView3.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt1)) {
						imageView3.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt1)) {
						imageView3.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt1)) {
						imageView3.setImageResource(R.drawable.word_icon);
					}
					filename4.setText(path111.substring(path111
							.lastIndexOf("/") + 1));
					Exist4 = !Exist4;
					path4 = path444;
				}else if (!Exist5) {
					if (Constant.ImgFormat.contains(filefromt1)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path111, options);
						imageView4.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt1)) {
						imageView4.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt1)) {
						imageView4.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt1)) {
						imageView4.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt1)) {
						imageView4.setImageResource(R.drawable.word_icon);
					}
					filename5.setText(path111.substring(path111
							.lastIndexOf("/") + 1));
					Exist5 = !Exist5;
					path5 = path555;
				}else if (!Exist6) {
					if (Constant.ImgFormat.contains(filefromt1)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path111, options);
						imageView5.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt1)) {
						imageView5.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt1)) {
						imageView5.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt1)) {
						imageView5.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt1)) {
						imageView5.setImageResource(R.drawable.word_icon);
					}
					filename4.setText(path111.substring(path111
							.lastIndexOf("/") + 1));
					Exist6 = !Exist6;
					path6 = path666;
				}
			}
			if (!filefromt2.equals("")) {
				if (!Exist1) {
					if (Constant.ImgFormat.contains(filefromt2)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path222, options);
						imageView.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt2)) {
						imageView.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt2)) {
						imageView.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt2)) {
						imageView.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt2)) {
						imageView.setImageResource(R.drawable.word_icon);
					}
					filename1.setText(path222.substring(path222
							.lastIndexOf("/") + 1));
					Exist1 = !Exist1;
					path1 = path111;
				} else if (!Exist2) {
					if (Constant.ImgFormat.contains(filefromt2)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path222, options);
						imageView1.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt2)) {
						imageView1.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt2)) {
						imageView1.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt2)) {
						imageView1.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt2)) {
						imageView1.setImageResource(R.drawable.word_icon);
					}
					filename2.setText(path222.substring(path222
							.lastIndexOf("/") + 1));
					Exist2 = !Exist2;
					path2 = path222;
				} else if (!Exist3) {
					if (Constant.ImgFormat.contains(filefromt2)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path222, options);
						imageView2.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt2)) {
						imageView2.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt2)) {
						imageView2.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt2)) {
						imageView2.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt2)) {
						imageView2.setImageResource(R.drawable.word_icon);
					}
					filename3.setText(path222.substring(path222
							.lastIndexOf("/") + 1));
					Exist3 = !Exist3;
					path3 = path333;
				}else if (!Exist4) {
					if (Constant.ImgFormat.contains(filefromt2)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path222, options);
						imageView3.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt2)) {
						imageView3.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt2)) {
						imageView3.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt2)) {
						imageView3.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt2)) {
						imageView3.setImageResource(R.drawable.word_icon);
					}
					filename4.setText(path222.substring(path222
							.lastIndexOf("/") + 1));
					Exist4 = !Exist4;
					path4 = path444;
				}else if (!Exist5) {
					if (Constant.ImgFormat.contains(filefromt2)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path222, options);
						imageView4.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt2)) {
						imageView4.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt2)) {
						imageView4.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt2)) {
						imageView4.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt2)) {
						imageView4.setImageResource(R.drawable.word_icon);
					}
					filename5.setText(path222.substring(path222
							.lastIndexOf("/") + 1));
					Exist5 = !Exist5;
					path5 = path555;
				}else if (!Exist6) {
					if (Constant.ImgFormat.contains(filefromt2)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path222, options);
						imageView5.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt2)) {
						imageView5.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt2)) {
						imageView5.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt2)) {
						imageView5.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt2)) {
						imageView5.setImageResource(R.drawable.word_icon);
					}
					filename6.setText(path222.substring(path222
							.lastIndexOf("/") + 1));
					Exist6 = !Exist6;
					path6 = path666;
				}
			}
			if (!filefromt3.equals("")) {
				if (!Exist1) {
					if (Constant.ImgFormat.contains(filefromt3)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path333, options);
						imageView.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt3)) {
						imageView.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt3)) {
						imageView.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt3)) {
						imageView.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt3)) {
						imageView.setImageResource(R.drawable.word_icon);
					}
					filename1.setText(path333.substring(path333
							.lastIndexOf("/") + 1));
					Exist1 = !Exist1;
					path1 = path111;
				} else if (!Exist2) {
					if (Constant.ImgFormat.contains(filefromt3)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path333, options);
						imageView1.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt3)) {
						imageView1.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt3)) {
						imageView1.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt3)) {
						imageView1.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt3)) {
						imageView1.setImageResource(R.drawable.word_icon);
					}
					filename2.setText(path333.substring(path333
							.lastIndexOf("/") + 1));
					Exist2 = !Exist2;
					path2 = path222;
				} else if (!Exist3) {
					if (Constant.ImgFormat.contains(filefromt3)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path333, options);
						imageView2.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt3)) {
						imageView2.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt3)) {
						imageView2.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt3)) {
						imageView2.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt3)) {
						imageView2.setImageResource(R.drawable.word_icon);
					}
					filename3.setText(path333.substring(path333
							.lastIndexOf("/") + 1));
					Exist3 = !Exist3;
					path3 = path333;
				}else if (!Exist4) {
					if (Constant.ImgFormat.contains(filefromt3)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path333, options);
						imageView3.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt3)) {
						imageView3.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt3)) {
						imageView3.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt3)) {
						imageView3.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt3)) {
						imageView3.setImageResource(R.drawable.word_icon);
					}
					filename4.setText(path333.substring(path333
							.lastIndexOf("/") + 1));
					Exist4 = !Exist4;
					path4 = path444;
				}else if (!Exist5) {
					if (Constant.ImgFormat.contains(filefromt3)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path333, options);
						imageView4.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt3)) {
						imageView4.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt3)) {
						imageView4.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt3)) {
						imageView4.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt3)) {
						imageView4.setImageResource(R.drawable.word_icon);
					}
					filename5.setText(path333.substring(path333
							.lastIndexOf("/") + 1));
					Exist5 = !Exist5;
					path5 = path555;
				}else if (!Exist6) {
					if (Constant.ImgFormat.contains(filefromt4)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path333, options);
						imageView5.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt4)) {
						imageView5.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt3)) {
						imageView5.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt3)) {
						imageView5.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt3)) {
						imageView5.setImageResource(R.drawable.word_icon);
					}
					filename6.setText(path333.substring(path333
							.lastIndexOf("/") + 1));
					Exist6 = !Exist6;
					path6 = path666;
				}
			}
			if (!filefromt4.equals("")) {
				if (!Exist1) {
					if (Constant.ImgFormat.contains(filefromt4)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path444, options);
						imageView.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt4)) {
						imageView.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt4)) {
						imageView.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt4)) {
						imageView.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt4)) {
						imageView.setImageResource(R.drawable.word_icon);
					}
					filename1.setText(path444.substring(path444
							.lastIndexOf("/") + 1));
					Exist1 = !Exist1;
					path1 = path111;
				} else if (!Exist2) {
					if (Constant.ImgFormat.contains(filefromt4)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path444, options);
						imageView1.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt4)) {
						imageView1.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt4)) {
						imageView1.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt4)) {
						imageView1.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt4)) {
						imageView1.setImageResource(R.drawable.word_icon);
					}
					filename2.setText(path444.substring(path444
							.lastIndexOf("/") + 1));
					Exist2 = !Exist2;
					path2 = path222;
				} else if (!Exist3) {
					if (Constant.ImgFormat.contains(filefromt4)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path444, options);
						imageView2.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt4)) {
						imageView2.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt4)) {
						imageView2.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt4)) {
						imageView2.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt4)) {
						imageView2.setImageResource(R.drawable.word_icon);
					}
					filename3.setText(path444.substring(path444
							.lastIndexOf("/") + 1));
					Exist3 = !Exist3;
					path3 = path333;
				}else if (!Exist4) {
					if (Constant.ImgFormat.contains(filefromt4)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path444, options);
						imageView3.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt4)) {
						imageView3.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt4)) {
						imageView3.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt4)) {
						imageView3.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt4)) {
						imageView3.setImageResource(R.drawable.word_icon);
					}
					filename4.setText(path444.substring(path444
							.lastIndexOf("/") + 1));
					Exist4 = !Exist4;
					path4 = path444;
				}else if (!Exist5) {
					if (Constant.ImgFormat.contains(filefromt4)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path444, options);
						imageView4.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt4)) {
						imageView4.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt4)) {
						imageView4.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt4)) {
						imageView4.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt4)) {
						imageView4.setImageResource(R.drawable.word_icon);
					}
					filename5.setText(path444.substring(path444
							.lastIndexOf("/") + 1));
					Exist5 = !Exist5;
					path5 = path555;
				}else if (!Exist6) {
					if (Constant.ImgFormat.contains(filefromt4)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path444, options);
						imageView5.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt4)) {
						imageView5.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt4)) {
						imageView5.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt4)) {
						imageView5.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt4)) {
						imageView5.setImageResource(R.drawable.word_icon);
					}
					filename6.setText(path444.substring(path444
							.lastIndexOf("/") + 1));
					Exist6 = !Exist6;
					path6 = path666;
				}
			}
			if (!filefromt5.equals("")) {
				if (!Exist1) {
					if (Constant.ImgFormat.contains(filefromt5)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path555, options);
						imageView.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt5)) {
						imageView.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt5)) {
						imageView.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt5)) {
						imageView.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt5)) {
						imageView.setImageResource(R.drawable.word_icon);
					}
					filename1.setText(path555.substring(path555
							.lastIndexOf("/") + 1));
					Exist1 = !Exist1;
					path1 = path111;
				} else if (!Exist2) {
					if (Constant.ImgFormat.contains(filefromt5)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path555, options);
						imageView1.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt5)) {
						imageView1.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt5)) {
						imageView1.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt5)) {
						imageView1.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt5)) {
						imageView1.setImageResource(R.drawable.word_icon);
					}
					filename2.setText(path555.substring(path555
							.lastIndexOf("/") + 1));
					Exist2 = !Exist2;
					path2 = path222;
				} else if (!Exist3) {
					if (Constant.ImgFormat.contains(filefromt5)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path555, options);
						imageView2.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt5)) {
						imageView2.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt5)) {
						imageView2.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt5)) {
						imageView2.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt5)) {
						imageView2.setImageResource(R.drawable.word_icon);
					}
					filename3.setText(path555.substring(path555
							.lastIndexOf("/") + 1));
					Exist3 = !Exist3;
					path3 = path333;
				}else if (!Exist4) {
					if (Constant.ImgFormat.contains(filefromt5)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path555, options);
						imageView3.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt5)) {
						imageView3.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt5)) {
						imageView3.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt5)) {
						imageView3.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt5)) {
						imageView3.setImageResource(R.drawable.word_icon);
					}
					filename4.setText(path555.substring(path555
							.lastIndexOf("/") + 1));
					Exist4 = !Exist4;
					path4 = path444;
				}else if (!Exist5) {
					if (Constant.ImgFormat.contains(filefromt5)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path555, options);
						imageView4.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt5)) {
						imageView4.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt5)) {
						imageView4.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt5)) {
						imageView4.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt5)) {
						imageView4.setImageResource(R.drawable.word_icon);
					}
					filename5.setText(path555.substring(path555
							.lastIndexOf("/") + 1));
					Exist5 = !Exist5;
					path5 = path555;
				}else if (!Exist6) {
					if (Constant.ImgFormat.contains(filefromt5)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path555, options);
						imageView5.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt5)) {
						imageView5.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt5)) {
						imageView5.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt5)) {
						imageView5.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt5)) {
						imageView5.setImageResource(R.drawable.word_icon);
					}
					filename6.setText(path555.substring(path555
							.lastIndexOf("/") + 1));
					Exist6 = !Exist6;
					path6 = path666;
				}
			}
			if (!filefromt6.equals("")) {
				if (!Exist1) {
					if (Constant.ImgFormat.contains(filefromt6)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path666, options);
						imageView.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt6)) {
						imageView.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt6)) {
						imageView.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt6)) {
						imageView.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt6)) {
						imageView.setImageResource(R.drawable.word_icon);
					}
					filename1.setText(path666.substring(path666
							.lastIndexOf("/") + 1));
					Exist1 = !Exist1;
					path1 = path111;
				} else if (!Exist2) {
					if (Constant.ImgFormat.contains(filefromt6)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path666, options);
						imageView1.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt6)) {
						imageView1.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt6)) {
						imageView1.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt6)) {
						imageView1.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt6)) {
						imageView1.setImageResource(R.drawable.word_icon);
					}
					filename2.setText(path666.substring(path666
							.lastIndexOf("/") + 1));
					Exist2 = !Exist2;
					path2 = path222;
				} else if (!Exist3) {
					if (Constant.ImgFormat.contains(filefromt6)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path666, options);
						imageView2.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt6)) {
						imageView2.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt6)) {
						imageView2.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt6)) {
						imageView2.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt6)) {
						imageView2.setImageResource(R.drawable.word_icon);
					}
					filename3.setText(path666.substring(path666
							.lastIndexOf("/") + 1));
					Exist3 = !Exist3;
					path3 = path333;
				}else if (!Exist4) {
					if (Constant.ImgFormat.contains(filefromt6)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path666, options);
						imageView3.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt6)) {
						imageView3.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt6)) {
						imageView3.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt6)) {
						imageView3.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt6)) {
						imageView3.setImageResource(R.drawable.word_icon);
					}
					filename4.setText(path666.substring(path666
							.lastIndexOf("/") + 1));
					Exist4 = !Exist4;
					path4 = path444;
				}else if (!Exist5) {
					if (Constant.ImgFormat.contains(filefromt6)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path666, options);
						imageView4.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt6)) {
						imageView4.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt6)) {
						imageView4.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt6)) {
						imageView4.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt6)) {
						imageView4.setImageResource(R.drawable.word_icon);
					}
					filename5.setText(path666.substring(path666
							.lastIndexOf("/") + 1));
					Exist5 = !Exist5;
					path5 = path555;
				}else if (!Exist6) {
					if (Constant.ImgFormat.contains(filefromt6)) {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = false;
						smallBitmap = BitmapFactory
								.decodeFile(path666, options);
						imageView5.setImageBitmap(smallBitmap);
					} else if (Constant.TxtFormat.contains(filefromt6)) {
						imageView5.setImageResource(R.drawable.txt_icon);
					} else if (Constant.PptFormat.contains(filefromt6)) {
						imageView5.setImageResource(R.drawable.ppt_icon);
					} else if (Constant.XlsFormat.contains(filefromt6)) {
						imageView5.setImageResource(R.drawable.xls_icon);
					} else if (Constant.WordFormat.contains(filefromt6)) {
						imageView5.setImageResource(R.drawable.word_icon);
					}
					filename6.setText(path666.substring(path666
							.lastIndexOf("/") + 1));
					Exist6 = !Exist6;
					path6 = path666;
				}
			}
			smallBitmap = null;
			System.gc();
			break;
		case 10:
			sendreport_name1.setText("");
			sendreport_name2.setText("");
			sendreport_name3.setText("");
			send_bg_name.setVisibility(View.GONE);
			flowLayout.removeAllViewsInLayout();
			flowLayout.removeAllViews();
			list.clear();
			list = (List<TelBookModel>) data.getSerializableExtra("list");
			for (int i = 0; i < list.size(); i++) {
				viewtext = LayoutInflater.from(this).inflate(R.layout.view,
						null);
				Button box = (Button) viewtext.findViewById(R.id.txt);
				box.setTag(i);
				box.setText(list.get(i).getName());
				box.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						v.setClickable(false);
						flowLayout.removeView(v);
						if (list.size() - 1 >= Integer.parseInt(v.getTag()
								.toString())) {
							list.remove(Integer.parseInt(v.getTag().toString()));
						}
						if (flowLayout.getChildCount() == 0) {
							send_bg_name.setVisibility(View.VISIBLE);
						} else {

						}
					}
				});
				flowLayout.addView(box);
			}
			break;
		case SELECT_PIC_BY_TACK_PHOTO:
			if (data != null) {
				if (data.hasExtra("data")) {
					// 处理缩略图
					final Bitmap photo = data.getParcelableExtra("data");
					// 文件夹sytm
					String path = Environment.getExternalStorageDirectory()
							.toString() + "/sytm/";
					if (num == 0) {
						String name = System.currentTimeMillis() + num + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(photo, path, name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path1 = path + name;
						imageView.setImageBitmap(photo);
						filename1.setText(name);
					} else if (num == 1) {
						String name = System.currentTimeMillis() + num + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(photo, path, name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path2 = path + name;
						imageView1.setImageBitmap(photo);
						filename2.setText(name);
					} else if (num == 2) {
						String name = System.currentTimeMillis() + num + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(photo, path, name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path3 = path + name;
						imageView2.setImageBitmap(photo);
						filename3.setText(name);
					}else if (num == 3) {
						String name = System.currentTimeMillis() + num + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(photo, path, name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path4 = path + name;
						imageView3.setImageBitmap(photo);
						filename4.setText(name);
					}else if (num == 4) {
						String name = System.currentTimeMillis() + num + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(photo, path, name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path5 = path + name;
						imageView4.setImageBitmap(photo);
						filename5.setText(name);
					} else {
						String name = System.currentTimeMillis() + num + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(photo, path, name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path6 = path + name;
						imageView5.setImageBitmap(photo);
						filename6.setText(name);
					}
					
				} else {

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory().getPath()
							+ "/sytm/image/image.jpg", options);
					int w = options.outWidth;
					int h = options.outHeight;
					if (w >= h) {
						if (LEN < h) {

							while (LEN < h) {
								h -= 20;
							}
							SCAN = options.outHeight / h;
						}
					} else {
						if (LEN < w) {

							while (LEN < w) {
								w -= 20;
							}
							SCAN = options.outWidth / w;
						}
					}
					options.inSampleSize = SCAN;
					options.inJustDecodeBounds = false;
					smallBitmap = BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory().getPath()
							+ "/sytm/image/image.jpg", options);
					// 文件夹sytm
					String path = Environment.getExternalStorageDirectory()
							.getPath() + "/sytm/";
					// 将处理过的图片显示在界面上，并保存到本地
					if (num == 0) {
						String name = System.currentTimeMillis() + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(smallBitmap, path,
									name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path1 = path + name;
						imageView.setImageBitmap(smallBitmap);
						Exist1 = true;
						filename1.setText(name);
					} else if (num == 1) {
						String name = System.currentTimeMillis() + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(smallBitmap, path,
									name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path2 = path + name;
						imageView1.setImageBitmap(smallBitmap);
						Exist2 = true;
						filename2.setText(name);
					} else if (num == 2) {
						String name = System.currentTimeMillis() + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(smallBitmap, path,
									name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path3 = path + name;
						imageView2.setImageBitmap(smallBitmap);
						Exist3 = true;
						filename3.setText(name);
					} else if (num == 3) {
						String name = System.currentTimeMillis() + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(smallBitmap, path,
									name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path4 = path + name;
						imageView3.setImageBitmap(smallBitmap);
						Exist4 = true;
						filename4.setText(name);
					} else if (num == 4) {
						String name = System.currentTimeMillis() + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(smallBitmap, path,
									name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path5 = path + name;
						imageView4.setImageBitmap(smallBitmap);
						Exist5 = true;
						filename5.setText(name);
					} else {
						String name = System.currentTimeMillis() + ".jpg";
						try {
							ImageUtils.savePhotoToSDCard(smallBitmap, path,
									name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						path6 = path + name;
						imageView5.setImageBitmap(smallBitmap);
						Exist6 = true;
						filename6.setText(name);
					}
					smallBitmap = null;
					System.gc();
				}
			} else {

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory().getPath()
						+ "/sytm/image/image.jpg", options);
				int w = options.outWidth;
				int h = options.outHeight;
				if (w >= h) {
					if (LEN < h) {

						while (LEN < h) {
							h -= 20;
						}
						SCAN = options.outHeight / h;
					}
				} else {
					if (LEN < w) {

						while (LEN < w) {
							w -= 20;
						}
						SCAN = options.outWidth / w;
					}
				}
				options.inSampleSize = SCAN;
				options.inJustDecodeBounds = false;
				smallBitmap = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory().getPath()
						+ "/sytm/image/image.jpg", options);
				// 文件夹sytm
				String path = Environment.getExternalStorageDirectory()
						.getPath() + "/sytm/";
				// 将处理过的图片显示在界面上，并保存到本地
				if (num == 0) {
					String name = System.currentTimeMillis() + ".jpg";
					try {
						ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
					} catch (IOException e) {
						e.printStackTrace();
					}
					path1 = path + name;
					imageView.setImageBitmap(smallBitmap);
					Exist1 = true;
					filename1.setText(name);
				} else if (num == 1) {
					String name = System.currentTimeMillis() + ".jpg";
					try {
						ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
					} catch (IOException e) {
						e.printStackTrace();
					}
					path2 = path + name;
					imageView1.setImageBitmap(smallBitmap);
					Exist2 = true;
					filename2.setText(name);
				} else if (num == 2) {
					String name = System.currentTimeMillis() + ".jpg";
					try {
						ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
					} catch (IOException e) {
						e.printStackTrace();
					}
					path3 = path + name;
					imageView2.setImageBitmap(smallBitmap);
					Exist3 = true;
					filename3.setText(name);
				}  else if (num == 3) {
					String name = System.currentTimeMillis() + ".jpg";
					try {
						ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
					} catch (IOException e) {
						e.printStackTrace();
					}
					path4 = path + name;
					imageView3.setImageBitmap(smallBitmap);
					Exist4 = true;
					filename4.setText(name);
				} else if (num == 4) {
					String name = System.currentTimeMillis() + ".jpg";
					try {
						ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
					} catch (IOException e) {
						e.printStackTrace();
					}
					path5 = path + name;
					imageView4.setImageBitmap(smallBitmap);
					Exist5 = true;
					filename5.setText(name);
				}else {
					String name = System.currentTimeMillis() + ".jpg";
					try {
						ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
					} catch (IOException e) {
						e.printStackTrace();
					}
					path6 = path + name;
					imageView5.setImageBitmap(smallBitmap);
					Exist6 = true;
					filename6.setText(name);
				}
				smallBitmap = null;
				System.gc();
			}
			break;
		case SELECT_PIC_BY_PICK_PHOTO:
			if (data == null) {
				Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return;
			}
			Uri originalUri = data.getData();
			if (originalUri == null) {
				Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return;
			}
			// 使用ContentProvider通过URI获取原始图片
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(originalUri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picturePath, options);
			// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
			int w = options.outWidth;
			int h = options.outHeight;
			if (w >= h) {
				if (LEN < h) {

					while (LEN < h) {
						h -= 20;
					}
					SCAN = options.outHeight / h;
				}
			} else {
				if (LEN < w) {

					while (LEN < w) {
						w -= 20;
					}
					SCAN = options.outWidth / w;
				}
			}
			// 释放原始图片占用的内存，防止out of memory异常发生
			// photo.recycle();
			options.inSampleSize = SCAN;
			options.inJustDecodeBounds = false;
			smallBitmap = BitmapFactory.decodeFile(picturePath, options);
			// 文件夹sytm
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/sytm/";
			if (num == 0) {
				String name = System.currentTimeMillis() + ".jpg";
				try {
					ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
				} catch (IOException e) {
					e.printStackTrace();
				}
				path1 = path + name;
				imageView.setImageBitmap(smallBitmap);
				Exist1 = true;
				filename1.setText(name);
			} else if (num == 1) {
				String name = System.currentTimeMillis() + ".jpg";
				try {
					ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
				} catch (IOException e) {
					e.printStackTrace();
				}
				path2 = path + name;
				imageView1.setImageBitmap(smallBitmap);
				Exist2 = true;
				filename2.setText(name);
			} else if (num == 2) {
				String name = System.currentTimeMillis() + ".jpg";
				try {
					ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
				} catch (IOException e) {
					e.printStackTrace();
				}
				path3 = path + name;
				imageView2.setImageBitmap(smallBitmap);
				Exist3 = true;
				filename3.setText(name);
			} else if (num == 3) {
				String name = System.currentTimeMillis() + ".jpg";
				try {
					ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
				} catch (IOException e) {
					e.printStackTrace();
				}
				path4 = path + name;
				imageView3.setImageBitmap(smallBitmap);
				Exist4 = true;
				filename4.setText(name);
			} else if (num == 4) {
				String name = System.currentTimeMillis() + ".jpg";
				try {
					ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
				} catch (IOException e) {
					e.printStackTrace();
				}
				path5 = path + name;
				imageView4.setImageBitmap(smallBitmap);
				Exist5 = true;
				filename5.setText(name);
			} else {
				String name = System.currentTimeMillis() + ".jpg";
				try {
					ImageUtils.savePhotoToSDCard(smallBitmap, path, name);
				} catch (IOException e) {
					e.printStackTrace();
				}
				path6 = path + name;
				imageView5.setImageBitmap(smallBitmap);
				Exist6 = true;
				filename6.setText(name);
			}
			smallBitmap = null;
			System.gc();
			break;
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
				if (exeParam.equals("report")) {
					ServiceContent sc = new ServiceContent();
					sc.setClassname("MReportHandler");
					sc.setMethodname("AddReport");
					sc.addStringPart("empid", sp.getString("empid", ""));
					sc.addStringPart("report", conString);
					sc.addStringPart("lng", lng);
					sc.addStringPart("lat", lat);
					sc.addStringPart("loctype", GetType);
					sc.addStringPart("ids", ids.toString());
					sc.addStringPart("title", titleString);
					sc.addStringPart("device", "android");
					Log.i("发送汇报", "empid="+sp.getString("empid", "")+",report="+conString+",lng="+lng+",lat="+lat+",loctype="+GetType+",ids="+ids.toString()+",title="+titleString+",device="+"android");
					if (!path1.equals("")) {
						Log.i("上传文件路径1", path1);
						File file = new File(path1);
						sc.addStringPart(
								"filename1",
								"$one$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		"."))
										+ "," + file.getName());
						sc.addFilePart(
								"file",
								file,
								"$one$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		".")));
					}
					if (!path2.equals("")) {
						Log.i("上传文件路径2", path2);
						File file = new File(path2);
						sc.addStringPart(
								"filename2",
								"$two$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		"."))
										+ "," + file.getName());
						sc.addFilePart(
								"file",
								file,
								"$two$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		".")));
					}
					if (!path3.equals("")) {
						Log.i("上传文件路径3", path3);
						File file = new File(path3);
						sc.addStringPart(
								"filename3",
								"$three$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		"."))
										+ "," + file.getName());
						sc.addFilePart(
								"file",
								file,
								"$three$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		".")));
					}
					if (!path4.equals("")) {
						Log.i("上传文件路径4", path4);
						File file = new File(path4);
						sc.addStringPart(
								"filename4",
								"$four$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		"."))
										+ "," + file.getName());
						sc.addFilePart(
								"file",
								file,
								"$four$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		".")));
					}
					if (!path5.equals("")) {
						Log.i("上传文件路径5", path5);
						File file = new File(path5);
						sc.addStringPart(
								"filename5",
								"$five$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		"."))
										+ "," + file.getName());
						sc.addFilePart(
								"file",
								file,
								"$five$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		".")));
					}
					if (!path6.equals("")) {
						Log.i("上传文件路径6", path6);
						File file = new File(path6);
						sc.addStringPart(
								"filename6",
								"$six$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		"."))
										+ "," + file.getName());
						sc.addFilePart(
								"file",
								file,
								"$six$"
										+ file.getName()
												.substring(
														file.getName()
																.lastIndexOf(
																		".")));
					}
					sc.setMultipost(true);
					sr = Network.postDataService(sc);
				}
			} catch (Exception e) {
				exeParam = "ERROR";
			}
			return exeParam;
		}

		// 执行完成后传送结果给UI线程 此方法最后执行
		protected void onPostExecute(String result) {
			if (result.equals("report")) {
				if (dialog2.isShowing()) {
					dialog2.dismiss();
				}
				if (!sr.GetIsError()) {
					content.setText("");
					scress = 1;
				}
				myDialog(sr.getMessage());

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

	public void myDialog(String msg) {
		View v = View.inflate(this, R.layout.dialog_alone, null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		if (dialog!=null) {
			if(dialog.isShowing()){
				dialog.dismiss();
			}
		}
		dialog = new Dialog(this, R.style.dialog_style);
		dialog.setContentView(v);
		dialog.setCancelable(true);
		textView.setText(msg);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (scress == 1) {
					finish();
				}
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
		dbManager.closeDB();
		unregisterReceiver(myReceiver);
		open = false;
	}

	@Override
	public void onClick(View v) {
		viewtext = LayoutInflater.from(this).inflate(R.layout.view, null);
		send_bg_name.setVisibility(View.GONE);
		switch (v.getId()) {
		case R.id.sendreport_name1:
			if (bookModels.size() > 0) {
				if (btn1) {
					list.add(bookModels.get(0));
					Button box = (Button) viewtext.findViewById(R.id.txt);
					box.setTag(list.size() - 1);
					box.setText(sendreport_name1.getText().toString());
					box.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							list.remove(Integer.parseInt(v.getTag().toString()));
							flowLayout.removeView(v);
							btn1 = true;
							if (flowLayout.getChildCount() == 0) {
								send_bg_name.setVisibility(View.VISIBLE);
							}
						}
					});
					flowLayout.addView(box);
					btn1 = false;
				}

			}
			break;
		case R.id.sendreport_name2:
			if (btn2) {
				if (bookModels.size() > 1) {
					list.add(bookModels.get(1));
					Button box = (Button) viewtext.findViewById(R.id.txt);
					box.setTag(list.size() - 1);
					box.setText(sendreport_name2.getText().toString());
					box.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							list.remove(Integer.parseInt(v.getTag().toString()));
							flowLayout.removeView(v);
							btn2 = true;
							if (flowLayout.getChildCount() == 0) {
								send_bg_name.setVisibility(View.VISIBLE);
							}
						}
					});
					flowLayout.addView(box);
					btn2 = false;
				}
			}
			break;
		case R.id.sendreport_name3:
			if (bookModels.size() > 2) {
				if (btn3) {
					list.add(bookModels.get(2));
					Button box = (Button) viewtext.findViewById(R.id.txt);
					box.setTag(list.size() - 1);
					box.setText(sendreport_name3.getText().toString());
					box.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							list.remove(Integer.parseInt(v.getTag().toString()));
							flowLayout.removeView(v);
							btn3 = true;
							if (flowLayout.getChildCount() == 0) {
								send_bg_name.setVisibility(View.VISIBLE);
							}
						}
					});
					flowLayout.addView(box);
					btn3 = false;
				}
			}
			break;

		default:
			break;
		}
		if (flowLayout.getChildCount() == 0) {
			send_bg_name.setVisibility(View.VISIBLE);
		}
	}
}
