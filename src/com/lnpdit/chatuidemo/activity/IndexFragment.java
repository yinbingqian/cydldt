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

public class IndexFragment extends Fragment {

	private Button btn_contact;
	private Button btn_information;
	private Button btn_meeting;
	private Button btn_message;
	private Button btn_location;
	private Button btn_mic;
	private Button btn_mail;
	private Button btn_control;
	private SharedPreferences sp;
	private Context context;

	String information = "";
	String contact = "";
	String meeting = "";
	String message = "";
	String location = "";
	String mic = "";
	String mail = "";
	String control = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_index, container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = this.getActivity();
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"user_info", Activity.MODE_PRIVATE);
		information = sharedPreferences.getString("information", "");
		contact = sharedPreferences.getString("contact", "");
		meeting = sharedPreferences.getString("meeting", "");
		message = sharedPreferences.getString("message", "");
		location = sharedPreferences.getString("location", "");
		mic = sharedPreferences.getString("mic", "");
		mail = sharedPreferences.getString("mail", "");
		control = sharedPreferences.getString("control", "");
		viewInit();
	}

	private void viewInit() {
		// cover_user_photo = (CircularImage)
		// this.findViewById(R.id.cover_user_photo);
		btn_contact = (Button) getView().findViewById(R.id.btn_contact);
		btn_information = (Button) getView().findViewById(R.id.btn_information);
		btn_meeting = (Button) getView().findViewById(R.id.btn_meeting);
		btn_message = (Button) getView().findViewById(R.id.btn_message);
		btn_location = (Button) getView().findViewById(R.id.btn_location);
		btn_mic = (Button) getView().findViewById(R.id.btn_mic);
		btn_mail = (Button) getView().findViewById(R.id.btn_mail);
		btn_control = (Button) getView().findViewById(R.id.btn_control);

		sp = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		// String username = sp.getString("RealName", "");
		// username_text = (TextView) this.findViewById(R.id.user_name_text);
		// username_text.setText("您好，" + username);

		// cover_user_photo.setImageResource(R.drawable.user_head_pic);
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
				if (contact.equals("1")) {

					startActivity(new Intent(getActivity(),
							ContactDeptActivity.class));
				} else {
					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_information:
				if (information.equals("1")) {
					startActivity(new Intent(getActivity(),
							InformationActivity.class));
				} else {
					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_meeting:
				if (sp.getString("meeting", "").equals("1")) {
					try {
						String meeting_username = sp.getString("RealName", "");
						String meeting_pwd = sp.getString("Pwd", "");
						String url = "FLVURI://?view=LoginPanel&ip=211.140.246.114:8080/NetMeetingJava&username="
								+ meeting_username + "&password=" + meeting_pwd;
						// String url =
						// "FLVURI://?view=LoginPanel&ip=211.140.246.114:8080/NetMeetingJava&username=1&password=1";
						// String url =
						// "FLVURI://?view=LoginPanel&ip=www.3gln.cn&username=aa&password=aa";
						Uri uri = Uri.parse(url);
						Intent _intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(_intent);
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(getActivity(), "未安装视频会议系统",
								Toast.LENGTH_SHORT).show();
						MeetingUpdate mManager = new MeetingUpdate(
								getActivity());
						mManager.downloadmeeting();
					}
				} else
					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();

				break;
			case R.id.btn_message:
				if (sp.getString("message", "").equals("1")) {
					// Toast.makeText(getActivity(), "点击了短信发送",
					// Toast.LENGTH_SHORT)
					// .show();
					startActivity(new Intent(getActivity(), MASActivity.class));
				} else
					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_location:
				if (location.equals("1")) {
					startActivity(new Intent(getActivity(), KqPageMain.class)
							.putExtra("out", 1));
				} else {

					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_mic:
				if (mic.equals("1")) {
					startActivity(new Intent(getActivity(), MicActivity.class));
				} else {

					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_mail:
				if (mail.equals("1")) {
					startActivity(new Intent(getActivity(), MailActivity.class));
				} else {

					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_control:
				if (control.equals("1")) {
					startActivity(new Intent(getActivity(),
							ControlActivity.class));
				} else {

					Toast.makeText(getActivity(), "对不起，您没有权限使用该功能",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};
}