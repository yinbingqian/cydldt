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
import com.sytm.bean.LeaveEventModel;

public class LeaveEventAdapter extends BaseAdapter {
	Context context;
	List<LeaveEventModel> list = new ArrayList<LeaveEventModel>();

	public LeaveEventAdapter(Context context, List<LeaveEventModel> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listview_view_item, null);
			holder = new ViewHolder();
			holder.listview_view_txt = (TextView) convertView
					.findViewById(R.id.listview_view_txt);
			holder.listview_view_img = (ImageView) convertView
					.findViewById(R.id.listview_view_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.listview_view_txt.setText(list.get(position).getValue().toString());
		if (list.get(position).getTAG() == 1) {
			holder.listview_view_img.setImageResource(R.drawable.danx_04);
		} else {
			holder.listview_view_img.setImageResource(R.drawable.dx_05);
		}
		return convertView;
	}

	class ViewHolder {
		TextView listview_view_txt;
		ImageView listview_view_img;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

}
