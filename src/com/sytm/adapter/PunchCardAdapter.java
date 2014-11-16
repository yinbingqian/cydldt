package com.sytm.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.bean.PunchCardMode;
import com.sytm.common.PunchModel;
import com.sytm.util.DateTimeUtils;

public class PunchCardAdapter extends BaseAdapter {
	Context context;
	List<PunchModel> list = new ArrayList<PunchModel>();
	boolean TAG = true;
	int num = 0;
	String today = DateTimeUtils.getOldDate(0);
	String yesterday = DateTimeUtils.getOldDate(-1);
	String beforeyesterday = DateTimeUtils.getOldDate(-2);

	public PunchCardAdapter(Context context, List<PunchModel> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.puch_item, null);
			holder = new ViewHolder();
			holder.worktime = (TextView) convertView
					.findViewById(R.id.work_time);
			holder.workdate = (TextView) convertView
					.findViewById(R.id.work_date);
			holder.workaddr = (TextView) convertView
					.findViewById(R.id.work_addr);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list != null && list.size() > 0) {
			if (list.get(position).getCheckdate().length() > 14) {
				holder.workdate.setText(list.get(position).getCheckdate()
						.substring(0, 10));
				holder.worktime.setText(list.get(position).getCheckdate()
						.substring(11));
			}
			holder.workaddr.setText(list.get(position).getAddress().toString());
		}
		return convertView;
	}

	class ViewHolder {
		TextView worktime;
		TextView workdate;
		TextView workaddr;
	}
}
