package lnpdit.stategrid.informatization.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.lnpdit.chatuidemo.R;
import com.lnpdit.chatuidemo.activity.ContactActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsDeptAdapter extends BaseAdapter {
	private class buttonViewHolder {
		TextView textview;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private buttonViewHolder holder;

	public NewsDeptAdapter(Context c,
			ArrayList<HashMap<String, Object>> appList, int resource,
			String[] from, int[] to) {
		mAppList = appList;
		mContext = c;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		keyString = new String[from.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
	}

	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int positon) {
		mAppList.remove(positon);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			holder = (buttonViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.list_in_news_dept, null);
			holder = new buttonViewHolder();
			holder.textview = (TextView) convertView.findViewById(R.id.textview);
			convertView.setTag(holder);
		}
		HashMap<String, Object> appInfo = mAppList.get(position);
		if (appInfo != null) {
			String Id = (String) appInfo.get(keyString[0]);
			String Grade = (String) appInfo.get(keyString[1]);
			String Class = (String) appInfo.get(keyString[2]);
			String Remark = (String) appInfo.get(keyString[3]);

			holder.textview.setText(Class);
			
//			convertView.setOnClickListener(new AdapterListener(position, Id,
//					Grade, Class, Remark));
		}
		return convertView;
	}

	public void addItem(ArrayList<HashMap<String, Object>> item) {
		int count = item.size();
		for (int i = 0; i < count; i++) {
			mAppList.add(item.get(i));
		}
	}

	class AdapterListener implements OnClickListener {
		private int position;
		private String Id;
		private String Grade;
		private String Class;
		private String Remark;
		private String mark;
		private String aging;
		private String status;
		private String count;

		public AdapterListener(int pos, String _Id, String _Grade,
				String _Class, String _Remark) {
			// TODO Auto-generated constructor stub
			position = pos;
			Id = _Id;
			Grade = _Grade;
			Class = _Class;
			Remark = _Remark;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			Intent intent = new Intent();
//			intent.setClass(mContext, ContactActivity.class);
//			intent.putExtra("Id", Id);
//			intent.putExtra("Grade", Grade);
//			intent.putExtra("Class", Class);
//			intent.putExtra("Remark", Remark);
//			mContext.startActivity(intent);
			Toast.makeText(mContext, Class, Toast.LENGTH_SHORT).show();
		}
	}
}
