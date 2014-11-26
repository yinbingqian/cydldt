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

public class ContactDeptAdapter extends BaseAdapter {
	private class buttonViewHolder {
		TextView appdept;
		TextView appdept2;
		LinearLayout imagebg1;
		LinearLayout imagebg2;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private buttonViewHolder holder;

	public ContactDeptAdapter(Context c,
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
			convertView = mInflater.inflate(R.layout.list_in_contactdept, null);
			holder = new buttonViewHolder();
			holder.appdept = (TextView) convertView.findViewById(R.id.dept_tv);
			holder.appdept2 = (TextView) convertView.findViewById(R.id.dept_tv2);
			holder.imagebg1 = (LinearLayout) convertView.findViewById(R.id.imagebg_1);
			holder.imagebg2 = (LinearLayout) convertView.findViewById(R.id.imagebg_2);
			convertView.setTag(holder);
		}
		HashMap<String, Object> appInfo = mAppList.get(position);
		if (appInfo != null) {
			String Id1 = (String) appInfo.get(keyString[0]);
			String Grade1 = (String) appInfo.get(keyString[1]);
			String Class1 = (String) appInfo.get(keyString[2]);
			String Remark1 = (String) appInfo.get(keyString[3]);
			String Id2 = (String) appInfo.get(keyString[4]);
			String Grade2 = (String) appInfo.get(keyString[5]);
			String Class2 = (String) appInfo.get(keyString[6]);
			String Remark2 = (String) appInfo.get(keyString[7]);

			holder.appdept.setText(Class1);
			holder.appdept2.setText(Class2);
			
			convertView.setOnClickListener(new AdapterListener(position, Id1,
					Grade1, Class1, Remark1));
			holder.imagebg1.setOnClickListener(new AdapterListener(position, Id1,
					Grade1, Class1, Remark1));
			holder.imagebg2.setOnClickListener(new AdapterListener(position, Id2,
					Grade2, Class2, Remark2));
			
			holder.imagebg1.setClickable(true);
			holder.imagebg2.setClickable(true);
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
			Intent intent = new Intent();
			intent.setClass(mContext, ContactActivity.class);
			intent.putExtra("Id", Id);
			intent.putExtra("Grade", Grade);
			intent.putExtra("Class", Class);
			intent.putExtra("Remark", Remark);
			mContext.startActivity(intent);
		}
	}
}
