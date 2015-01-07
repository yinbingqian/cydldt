package com.lnpdit.chatuidemo.activity;

import lnpdit.stategrid.informatization.tools.MeetingUpdate;

import com.lnpdit.chatuidemo.R;
import com.sytm.tmkq.KqPageMain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class IndexFragment extends Fragment{

	
	private Button btn_contact;
	private Button btn_information;
	private Button btn_meeting;
	private Button btn_message;
	private Button btn_location;
	private Button btn_mic;
	private Button btn_mail;
	private Button btn_control;
	private SharedPreferences sp;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
		return inflater.inflate(R.layout.fragment_index, container, false);
			
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewInit();
	}
	private void viewInit() {
		//cover_user_photo = (CircularImage) this.findViewById(R.id.cover_user_photo);
		btn_contact = (Button)getView().findViewById(R.id.btn_contact);
		btn_information = (Button)getView().findViewById(R.id.btn_information);
		btn_meeting = (Button) getView().findViewById(R.id.btn_meeting);
		btn_message = (Button) getView().findViewById(R.id.btn_message);
		btn_location = (Button) getView().findViewById(R.id.btn_location);
		btn_mic = (Button) getView().findViewById(R.id.btn_mic);
		btn_mail = (Button) getView().findViewById(R.id.btn_mail);
		btn_control = (Button) getView().findViewById(R.id.btn_control);

		sp = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
		// String username = sp.getString("RealName", "");
		//username_text = (TextView) this.findViewById(R.id.user_name_text);
		// username_text.setText("您好，" + username);

		//cover_user_photo.setImageResource(R.drawable.user_head_pic);
		btn_contact.setOnClickListener(btn_listener);
		btn_information.setOnClickListener(btn_listener);
		btn_meeting.setOnClickListener(btn_listener);
		btn_message.setOnClickListener(btn_listener);
		btn_location.setOnClickListener(btn_listener);
		btn_mic.setOnClickListener(btn_listener);
		btn_mail.setOnClickListener(btn_listener);
		btn_control.setOnClickListener(btn_listener);
	}
	private android.view.View.OnClickListener btn_listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_contact:
			    Toast.makeText(getActivity(), "点击了企业通讯录", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), ContactDeptActivity.class));
				break;
			case R.id.btn_information:
				Toast.makeText(getActivity(), "点击了企业微信",Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), InformationActivity.class));
				break;
			case R.id.btn_meeting:
				if(sp.getString("meeting", "").equals("1"))
				{
					try {
						String meeting_username = sp.getString("RealName", "");
						String meeting_pwd = sp.getString("Pwd", "");
						String url = "FLVURI://?view=LoginPanel&ip=211.140.246.114:8080/NetMeetingJava&username="
								+ meeting_username + "&password=" + meeting_pwd;
//						String url = "FLVURI://?view=LoginPanel&ip=211.140.246.114:8080/NetMeetingJava&username=1&password=1";
						// String url = "FLVURI://?view=LoginPanel&ip=www.3gln.cn&username=aa&password=aa";
						Uri uri = Uri.parse(url);
						Intent _intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(_intent);
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(getActivity(), "未安装视频会议系统", Toast.LENGTH_SHORT).show();
						MeetingUpdate mManager = new MeetingUpdate(getActivity());
						mManager.downloadmeeting();
					}
				}else
					Toast.makeText(getActivity(), "您没有权限使用该功能", Toast.LENGTH_SHORT).show();
				
				break;
			case R.id.btn_message:
				if(sp.getString("message", "").equals("1"))
				{
					Toast.makeText(getActivity(), "点击了短信发送",Toast.LENGTH_SHORT).show();
					startActivity(new Intent(getActivity(), MASActivity.class));
				}else
					Toast.makeText(getActivity(), "您没有权限使用该功能",Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_location:
				Toast.makeText(getActivity(), "点击了位置管理",	Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), KqPageMain.class).putExtra("out", 1));
				break;
			case R.id.btn_mic:
				Toast.makeText(getActivity(), "点击了麦克风",Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), MicActivity.class));
				break;
			case R.id.btn_mail:
				Toast.makeText(getActivity(), "点击了手机邮箱",Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), MailActivity.class));
				break;
			case R.id.btn_control:
				Toast.makeText(getActivity(), "点击了现场管控",Toast.LENGTH_SHORT).show();				
				startActivity(new Intent(getActivity(), ControlActivity.class));
				break;
			default:
				break;
			}
		}
	};
}