package lnpdit.stategrid.informatization.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.lnpdit.chatuidemo.R;
import com.lnpdit.chatuidemo.activity.PeixunQuestionActivity;

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

public class PeixunListAdapter extends BaseAdapter {
	private class buttonViewHolder {
		TextView appdept;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private buttonViewHolder holder;

	public PeixunListAdapter(Context c,
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
			convertView = mInflater.inflate(R.layout.list_item_peixun, null);
			holder = new buttonViewHolder();
			holder.appdept = (TextView) convertView.findViewById(R.id.textview);
			convertView.setTag(holder);
		}
		HashMap<String, Object> appInfo = mAppList.get(position);
		if (appInfo != null) {
			String id = (String) appInfo.get(keyString[0]);
			String deptid = (String) appInfo.get(keyString[1]);
			String title = (String) appInfo.get(keyString[2]);
			String remark1 = (String) appInfo.get(keyString[3]);
			String remark2 = (String) appInfo.get(keyString[4]);
			String remark3 = (String) appInfo.get(keyString[5]);
			String crtime = (String) appInfo.get(keyString[6]);
			
			holder.appdept.setText(title);
			
			convertView.setOnClickListener(new AdapterListener(position, id,
					deptid, title, remark1, remark2, remark3, crtime));
			holder.appdept.setOnClickListener(new AdapterListener(position, id,
					deptid, title, remark1, remark2, remark3, crtime));
			
			holder.appdept.setClickable(true);
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
		private String id;
		private String deptid;
		private String title;
		private String remark1;
		private String remark2;
		private String remark3;
		private String crtime;
		private String mark;
		private String aging;
		private String status;
		private String count;

		public AdapterListener(int pos, String _id,String _deptid,String _title,
				String _remark1,String _remark2,String _remark3,String _crtime) {
			// TODO Auto-generated constructor stub
			position = pos;
			id = _id;
			deptid = _deptid;
			title = _title;
			remark1 = _remark1;
			remark2 = _remark2;
			remark3 = _remark3;
			crtime = _crtime;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(mContext, PeixunQuestionActivity.class);
			intent.putExtra("id", id);
			intent.putExtra("deptid", deptid);
			intent.putExtra("title", title);
			intent.putExtra("remark1", remark1);
			intent.putExtra("remark2", remark2);
			intent.putExtra("remark3", remark3);
			intent.putExtra("crtime", crtime);
			mContext.startActivity(intent);
		}
	}
}
