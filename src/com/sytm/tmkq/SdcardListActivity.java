package com.sytm.tmkq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lnpdit.chatuidemo.R;
import com.sytm.adapter.SdcardAdapter;
import com.sytm.bean.UpFileModel;
import com.sytm.common.Constant;

public class SdcardListActivity extends Activity {

	private TextView tvpath;
	private ListView lvFiles;
	private Button back, file_true;
	private LinearLayout btnParent;
	// 记录当前的父文件夹
	File currentParent;
	// 记录当前路径下的所有文件夹的文件数组
	File[] currentFiles;
	SdcardAdapter adapter;
	List<UpFileModel> list = new ArrayList<UpFileModel>();
	int options = 0;
	String path1 = "", path2 = "", path3 = "", path4 = "", path5 = "", path6 = "";
	int exist = 0;
	Dialog dialog2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attachmentlist);
		lvFiles = (ListView) this.findViewById(R.id.files);
		tvpath = (TextView) this.findViewById(R.id.tvpath);
		btnParent = (LinearLayout) this.findViewById(R.id.btnParent);
		back = (Button) this.findViewById(R.id.back);
		file_true = (Button) this.findViewById(R.id.file_true);
		Intent intent = getIntent();
		exist = intent.getIntExtra("exist", 3);
		// 获取系统的SDCard的目录
		File root = new File(Constant.DownLoadPath);
		if (!root.exists()) {
			root.mkdirs();
		}
		// 如果SD卡存在的话
		if (root.exists()) {
			currentParent = root;
			currentFiles = root.listFiles();
			// 使用当前目录下的全部文件、文件夹来填充ListView
			inflateListView(currentFiles);
		}
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		file_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (options <= exist) {
					Intent intent = new Intent();
					intent.putExtra("path1", path1);
					intent.putExtra("path2", path2);
					intent.putExtra("path3", path3);
					intent.putExtra("path4", path4);
					intent.putExtra("path5", path5);
					intent.putExtra("path6", path6);
					setResult(RESULT_OK, intent);
					finish();
				}else {
					
				}
			}
		});
		lvFiles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// // 如果用户单击了文件，直接返回，不做任何处理
				// if (currentFiles[position].isFile()) {
				// String stringpath=
				// lvFiles.getItemAtPosition(position).toString();
				// Intent intent = new Intent();
				// intent.putExtra("path", stringpath);
				// setResult(RESULT_OK, intent);
				// finish();
				// }
				// Log.i("点击文件："+position,
				// list.get(position).getFile().getName());
				if (list.get(position).getFile().isFile()) {
					// 也可自定义扩展打开这个文件等
					// Intent intent = new Intent();
					// intent.putExtra("path",
					// list.get(position).getFile().getPath());
					// setResult(RESULT_OK, intent);
					// finish();
					if (Constant.FileFormat.contains(list
							.get(position)
							.getFile()
							.getName()
							.substring(
									list.get(position).getFile().getName()
											.lastIndexOf(".") + 1))) {
						if (options == exist && list.get(position).getTAG() == 0) {
							Toast.makeText(SdcardListActivity.this,
									"已到达最大上传数量", Toast.LENGTH_LONG).show();
							return;
						}
						if (options >= 0 && options <= exist) {
							if (list.get(position).getTAG() == 0) {
								if (options < 7) {
									list.get(position).setTAG(1);
									options++;
									if (options == 1) {
										path1 = list.get(position).getFile()
												.getPath();
									} else if (options == 2) {
										path2 = list.get(position).getFile()
												.getPath();
									} else if (options == 3) {
										path3 = list.get(position).getFile()
												.getPath();
									}else if (options == 4) {
										path4 = list.get(position).getFile()
												.getPath();
									}else if (options == 5) {
										path5 = list.get(position).getFile()
												.getPath();
									}else if (options == 6) {
										path6 = list.get(position).getFile()
												.getPath();
									}
								}
							} else {
								if (options > 0) {
									list.get(position).setTAG(0);
									options--;
									if (options == 0) {
										path1 = "";
										path2 = "";
										path3 = "";
										path4 = "";
										path5 = "";
										path6 = "";
									} else if (options == 1) {
										path2 = "";
										path3 = "";
										path4 = "";
										path5 = "";
										path6 = "";
									} else if (options == 2) {
										path3 = "";
										path4 = "";
										path5 = "";
										path6 = "";
									} else if (options == 3) {
										path4 = "";
										path5 = "";
										path6 = "";
									} else if (options == 4) {
										path5 = "";
										path6 = "";
									} else if (options == 5) {
										path6 = "";
									}
								}
							}
							file_true.setText("确定(" + options + ")");
							for (int i = 0; i < list.size(); i++) {
								list.get(i).setNum(options);
							}
							adapter.notifyDataSetChanged();
						}

					} else {
						Toast.makeText(SdcardListActivity.this,
								"此文件类型不符合上传需求，请选择能勾选的文件！", Toast.LENGTH_SHORT)
								.show();
					}
					return;
				}
				// 获取用户点击的文件夹 下的所有文件
				File[] tem = currentFiles[position].listFiles();
				if (tem == null || tem.length == 0) {
					Toast.makeText(SdcardListActivity.this,
							"当前路径不可访问或者该路径下没有文件", Toast.LENGTH_LONG).show();
				} else {
					// 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
					currentParent = currentFiles[position];
					// 保存当前的父文件夹内的全部文件和文件夹
					currentFiles = tem;
					// 再次更新ListView
					list.clear();
					inflateListView(currentFiles);
				}
			}
		});
		// 获取上一级目录
		btnParent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!currentParent.getCanonicalPath()
							.equals(Environment.getExternalStorageDirectory()
									.getPath())) {
						// 获取上一级目录
						currentParent = currentParent.getParentFile();
						// 列出当前目录下的所有文件
						currentFiles = currentParent.listFiles();
						// 再次更新ListView
						list.clear();
						inflateListView(currentFiles);
					} else {
						Toast.makeText(SdcardListActivity.this, "已经为根目录，不可返回",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * 
	 * 根据文件夹填充ListView
	 * 
	 * 
	 * 
	 * @param files
	 */
	private void inflateListView(File[] files) {
		// options = 0;
		// file_true.setText("确定(" + options+")");
		// path1="";
		// path2="";
		// path3="";
		for (int i = 0; i < files.length; i++) {
			UpFileModel fileModel = new UpFileModel();
			fileModel.setFile(files[i]);
			fileModel.setTAG(0);
			list.add(fileModel);
		}
		for (int i = 0; i < list.size(); i++) {
			if ((path1 + "," + path2 + "," + path3+ "," + path4+ "," + path5+ "," + path6).contains(list.get(i)
					.getFile().getPath())) {
				list.get(i).setTAG(1);
			}
		}
		// list=Arrays.asList(currentFiles);
		// for (int i = 0; i < list.size(); i++) {
		//
		// Log.i("list："+i,
		// list.get(i).getFile().getName()+">>"+list.get(i).getTAG());
		// }
		adapter = new SdcardAdapter(SdcardListActivity.this, list,exist);
		lvFiles.setAdapter(adapter);
		try {
			tvpath.setText("当前路径为:" + currentParent.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	private void AuditView() {
//		View v = View.inflate(this, R.layout.dialog, null);
//		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
//		Button btn_left = (Button) v.findViewById(R.id.dialog_button_left);
//		Button btn_right = (Button) v.findViewById(R.id.dialog_button_right);
//		btn_left.setText(getResources().getString(R.string.not_approve));
//		btn_right.setText(getResources().getString(R.string.approve));
//		dialog2 = new Dialog(this, R.style.dialog_style);
//		dialog2.setContentView(v);
//		dialog2.setCancelable(true);
//		dialog2.setCanceledOnTouchOutside(true);
//		btn_left.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				tag=2;
//				new Task().execute("audit");
//				dialog2.dismiss();
//			}
//		});
//		btn_right.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				tag=1;
//				new Task().execute("audit");
//				dialog2.dismiss();
//			}
//		});
//		if (dialog2.isShowing()) {
//			dialog2.dismiss();
//		}
//		dialog2.show();
//
//	}
}
