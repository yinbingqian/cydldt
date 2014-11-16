/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lnpdit.chatuidemo.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.tools.UpdataInfoParser;
import lnpdit.stategrid.informatization.tools.UpdateInfo;
import lnpdit.stategrid.informatization.tools.UpdateManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lnpdit.chatuidemo.Constant;
import com.lnpdit.chatuidemo.CydlApplication;
import com.lnpdit.chatuidemo.R;
import com.lnpdit.chatuidemo.db.UserDao;
import com.lnpdit.chatuidemo.domain.User;
import com.lnpdit.chatuidemo.utils.CommonUtils;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
	public static final int REQUEST_CODE_SETNICK=1;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private UpdateInfo info = new UpdateInfo();
	public static String localVersion="1";
	public static String serVersion="1";

	private boolean progressShow;
	private Context context;
	
	Button login_bt;
	ProgressBar progressbar;
	
	public final String PREF_USERNAME = "username";
	private static final String PREF_PWD = "pwd";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Intent intent_extra = this.getIntent();
		String username = intent_extra.getStringExtra("username");
		String pwd = intent_extra.getStringExtra("pwd");
		String name = intent_extra.getStringExtra("name");
		
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//		preferences.edit().putString(PREF_USERNAME, username).ucommit();  
//		preferences.edit().putString(PREF_PWD, pwd).commit();
		
		usernameEditText = (EditText) this.findViewById(R.id.username);
		passwordEditText = (EditText) this.findViewById(R.id.password);
		
		context = this;
		
		
//		usernameEditText.setText(username);
//		passwordEditText.setText(pwd);
//		Intent intent=new Intent(LoginActivity.this, com.lnpdit.chatuidemo.activity.AlertDialog.class);
//		intent.putExtra("editTextShow", true);
//		intent.putExtra("titleIsCancel",true);
//		intent.putExtra("msg", "请设置当前用户的昵称");
//		startActivityForResult(intent, REQUEST_CODE_SETNICK);
		CheckVersion ck = new CheckVersion();
		Thread th = new Thread(ck);
		th.start();
	}
	
	public void init()
	{
		// 如果用户名密码都有，直接进入主页面
				if (CydlApplication.getInstance().getUserName() != null && CydlApplication.getInstance().getPassword() != null) {
					startActivity(new Intent(this, MainActivity.class));
					finish();
				}
				// 如果用户名改变，清空密码
				usernameEditText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						passwordEditText.setText(null);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
	}

	/**
	 * 登陆
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		final String username = usernameEditText.getText().toString();
		final String password = passwordEditText.getText().toString();
		CydlApplication.currentUserNick=username;
		
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			progressShow = true;
			final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow = false;
				}
			});
			pd.setMessage("正在登陆...");
			pd.show();
			// 调用sdk登陆方法登陆聊天服务器
			EMChatManager.getInstance().login(username, password, new EMCallBack() {

				@Override
				public void onSuccess() {
					if (!progressShow) {
						return;
					}
					// 登陆成功，保存用户名密码
					CydlApplication.getInstance().setUserName(username);
					CydlApplication.getInstance().setPassword(password);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.setMessage("正在获取好友和群聊列表...");
						}
					});
					try {
						// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
						List<String> usernames = EMChatManager.getInstance().getContactUserNames();
						Map<String, User> userlist = new HashMap<String, User>();
						for (String username : usernames) {
							User user = new User();
							user.setUsername(username);
							setUserHearder(username, user);
							userlist.put(username, user);
						}
						// 添加user"申请与通知"
						User newFriends = new User();
						newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
						newFriends.setNick("申请与通知");
						newFriends.setHeader("");
						userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
						// 添加"群聊"	
						User groupUser = new User();
						groupUser.setUsername(Constant.GROUP_USERNAME);
						groupUser.setNick("群聊");
						groupUser.setHeader("");
						userlist.put(Constant.GROUP_USERNAME, groupUser);

						// 存入内存
						CydlApplication.getInstance().setContactList(userlist);
						// 存入db
						UserDao dao = new UserDao(LoginActivity.this);
						List<User> users = new ArrayList<User>(userlist.values());
						dao.saveContactList(users);

						// 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
						EMGroupManager.getInstance().getGroupsFromServer();
						//从服务器端获取用户信息
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
							boolean updatenick = EMChatManager.getInstance()
									.updateCurrentUserNick(CydlApplication.currentUserNick);
							if (!updatenick) {
								EMLog.e("LoginActivity",
										"update current user nick fail");
							}

					if (!LoginActivity.this.isFinishing())
						pd.dismiss();
					// 进入主页面
					MAdminLoginThread loginrunnable = new MAdminLoginThread();
					loginrunnable.setLoginInfo(username, password);
					Thread adminthread = new Thread(loginrunnable);
					adminthread.start();
					
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					finish();
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(int code, final String message) {
					if (!progressShow) {
						return;
					}
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), "登录失败: " + message, 0).show();

						}
					});
				}
			});
		}
	}

	
	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CydlApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(CydlApplication.getInstance().getUserName());
		}
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
	
	//消息Handler
	Handler mLoginInfoNomatch_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			HashMap<String, String> login_hash = (HashMap<String, String>) msg.obj;
			switch (msg.arg1) {
			case 0:// 普通用户
				String Id = login_hash.get("Id");
				String Sim = login_hash.get("Sim");
				String Name = login_hash.get("Name");
				String RealName = login_hash.get("RealName");
				String Sex = login_hash.get("Sex");
				String HeadPic = login_hash.get("HeadPic");
				String Islock = login_hash.get("Islock");
				String information = login_hash.get("information");
				String contact = login_hash.get("contact");
				String meeting = login_hash.get("meeting");
				String message = login_hash.get("message");
				String location = login_hash.get("location");
				String mic = login_hash.get("mic");
				String mail = login_hash.get("mail");
				String control = login_hash.get("control");
				String AdrId = login_hash.get("AdrId");

				if (Id.equals("0")) {
					Toast.makeText(context, "用户名或密码错误，请重新输入.", Toast.LENGTH_SHORT).show();

					login_bt.setVisibility(1);
					progressbar.setVisibility(8);
					return;
				}
				if (Islock.equals("True")) {
					Toast.makeText(context, "您的g帐号被锁定，无法登陆.", Toast.LENGTH_SHORT).show();

					login_bt.setVisibility(1);
					progressbar.setVisibility(8);
					return;
				}
				if (!Id.equals("0") && Islock.equals("False")) {

					SharedPreferences sp = context.getSharedPreferences("user_info", MODE_APPEND);
					SharedPreferences.Editor edit = sp.edit();
					edit.putString("Id", Id);
					edit.putString("Sim", Sim);
					edit.putString("Name", Name);
					edit.putString("RealName", RealName);
					edit.putString("Sex", Sex);
					edit.putString("HeadPic", HeadPic);
					edit.putString("Islock", Islock);
					edit.putString("information", information);
					edit.putString("contact", contact);
					edit.putString("meeting", meeting);
					edit.putString("message", message);
					edit.putString("location", location);
					edit.putString("mic", mic);
					edit.putString("mail", mail);
					edit.putString("control", control);
					edit.putString("AdrId", AdrId);
					edit.putString("Pwd", passwordEditText.getText().toString());
					edit.commit();

					SharedPreferences spPreferences = context.getSharedPreferences("TMMTC", MODE_PRIVATE);
					Editor editor = spPreferences.edit();
					editor.putString("name", Name);
					editor.putString("password", passwordEditText.getText().toString());
					editor.putString("empid", AdrId);
					editor.putString("TAG", "YES");
					editor.commit();
				}
				break;
			default:
				break;
			}

		}
	};
	public class MAdminLoginThread implements Runnable {
		private String username_t = "";
		private String pwd_t = "";

		public void setLoginInfo(String _username, String _pwd) {
			this.username_t = _username;
			this.pwd_t = _pwd;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String url = MessengerService.URL;
			String methodname = MessengerService.METHOD_UserInfoLogin;
			String namespace = MessengerService.NAMESPACE;
			String soapaction = namespace + "/" + methodname;

			SoapObject rpc = new SoapObject(namespace, methodname);

			rpc.addProperty("username", username_t);
			rpc.addProperty("password", pwd_t);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);

			HttpTransportSE ht = new HttpTransportSE(url);

			ht.debug = true;
			try {
				ht.call(soapaction, envelope);
				SoapObject siminfo = (SoapObject) envelope.bodyIn;

				SoapObject soapchilds1 = (SoapObject) siminfo.getProperty(0);
				SoapObject soapchilds2 = (SoapObject) soapchilds1.getProperty(1);
				SoapObject soapchilds3 = (SoapObject) soapchilds2.getProperty(0);
				SoapObject soapchilds = (SoapObject) soapchilds3.getProperty(0);

				String Id = soapchilds.getProperty("Id").toString();
				if (Id.startsWith("anyType")) {
					Id = "";
				}
				String Sim = soapchilds.getProperty("Sim").toString();
				if (Sim.startsWith("anyType")) {
					Sim = "";
				}
				String Name = soapchilds.getProperty("Name").toString();
				if (Name.startsWith("anyType")) {
					Name = "";
				}
				String RealName = soapchilds.getProperty("RealName").toString();
				if (RealName.startsWith("anyType")) {
					RealName = "";
				}
				String Sex = soapchilds.getProperty("Sex").toString();
				if (Sex.startsWith("anyType")) {
					Sex = "";
				}
				String HeadPic = soapchilds.getProperty("HeadPic").toString();
				if (HeadPic.startsWith("anyType")) {
					HeadPic = "";
				}
				String Islock = soapchilds.getProperty("Islock").toString();
				if (Islock.startsWith("anyType")) {
					Islock = "";
				}
				String information = soapchilds.getProperty("information")
						.toString();
				if (information.startsWith("anyType")) {
					information = "";
				}
				String contact = soapchilds.getProperty("contact").toString();
				if (contact.startsWith("anyType")) {
					contact = "";
				}
				String meeting = soapchilds.getProperty("meeting").toString();
				if (meeting.startsWith("anyType")) {
					meeting = "";
				}
				String message = soapchilds.getProperty("message").toString();
				if (message.startsWith("anyType")) {
					message = "";
				}
				String location = soapchilds.getProperty("location").toString();
				if (location.startsWith("anyType")) {
					location = "";
				}
				String mic = soapchilds.getProperty("mic").toString();
				if (mic.startsWith("anyType")) {
					mic = "";
				}
				String mail = soapchilds.getProperty("mail").toString();
				if (mail.startsWith("anyType")) {
					mail = "";
				}
				String control = soapchilds.getProperty("control").toString();
				if (control.startsWith("anyType")) {
					control = "";
				}
				String AdrId = soapchilds.getProperty("KqId").toString();
				if (AdrId.startsWith("anyType")) {
					AdrId = "";
				}

				HashMap<String, String> userdata = new HashMap<String, String>();
				userdata.put("Id", Id);
				userdata.put("Sim", Sim);
				userdata.put("Name", Name);
				userdata.put("RealName", RealName);
				userdata.put("Sex", Sex);
				userdata.put("HeadPic", HeadPic);
				userdata.put("Islock", Islock);
				userdata.put("information", information);
				userdata.put("contact", contact);
				userdata.put("meeting", meeting);
				userdata.put("message", message);
				userdata.put("location", location);
				userdata.put("mic", mic);
				userdata.put("mail", mail);
				userdata.put("control", control);
				userdata.put("AdrId", AdrId);

				Message msg = new Message();
				msg.arg1 = 0;
				msg.obj = userdata;
				mLoginInfoNomatch_handler.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch blockan
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//检查版本更新
	public class CheckVersion implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				// 从资源文件获取服务器 地址
				String path = MessengerService.IP + "/apksource/version.xml";
				// 包装成url的对象
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				InputStream is = conn.getInputStream();
				info = UpdataInfoParser.getUpdateInfo(is);
				PackageManager packageManager = getPackageManager();  
				PackageInfo localVersion = packageManager.getPackageInfo(getPackageName(), 0); 
				System.out.println("VersionActivity            ----------->          info = " + info);
				if (info.getVersion().equals(localVersion)) {
					init();
				} else {
					Looper.prepare();
					String download_url = info.getUrl();
					UpdateManager uManager = new UpdateManager(LoginActivity.this);
					uManager.checkUpdateInfo(info.getDescription());
					Looper.loop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}


