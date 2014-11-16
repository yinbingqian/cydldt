package com.sytm.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.bean.ToMyReportModel;

public class ToReportAdapter extends BaseAdapter {
	Context context;
	List<ToMyReportModel> list = new ArrayList<ToMyReportModel>();

	public ToReportAdapter(Context context, List<ToMyReportModel> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.tomyreport, null);
			holder = new ViewHolder();
			holder.lin_top = (LinearLayout) convertView
					.findViewById(R.id.tolin_top);
			holder.report_top_day = (TextView) convertView
					.findViewById(R.id.toreport_top_day);
			holder.report_top_date = (TextView) convertView
					.findViewById(R.id.toreport_top_date);
			holder.report_title = (TextView) convertView
					.findViewById(R.id.toreport_title);
			holder.report_sheng = (TextView) convertView
					.findViewById(R.id.toreport_sheng);
			holder.report_shi = (TextView) convertView
					.findViewById(R.id.toreport_shi);
			holder.report_reply_num = (TextView) convertView
					.findViewById(R.id.toreport_reply_num);
			holder.report_time = (TextView) convertView
					.findViewById(R.id.toreport_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!list.get(position).getDay().equals("")) {
			holder.lin_top.setVisibility(View.VISIBLE);
			holder.report_top_day.setText(list.get(position).getDay());
			holder.report_top_date.setText(list.get(position).getYear() + "."
					+ list.get(position).getMonth());
			holder.lin_top.setBackgroundResource(R.drawable.hb_02);
		}else {
			holder.lin_top.setVisibility(View.GONE);
		}
		if (list.get(position).getAddempname().equals("")) {
			
			holder.report_title.setText(context.getResources().getString(R.string.work_report));
		}else {
			holder.report_title.setText(list.get(position).getAddempname()+context.getResources().getString(R.string.work_report_to));
		}
		holder.report_sheng.setText(list.get(position).getProvince());
		holder.report_shi.setText(list.get(position).getCity());
			holder.report_reply_num.setText(list.get(position).getReplycount());
			if (list.get(position).getIsread().equals("1")) {
				holder.report_reply_num.setBackgroundResource(R.drawable.ty_05);
			}else {
				holder.report_reply_num.setBackgroundResource(R.drawable.hb2_05);
			}
		holder.report_time.setText(list.get(position).getAdddate()
				.substring(list.get(position).getAdddate().indexOf(" ") + 1));
		return convertView;
	}
	class ViewHolder {
		LinearLayout lin_top;
		TextView report_top_day;
		TextView report_top_date;
		TextView report_title;
		TextView report_sheng;
		TextView report_shi;
		TextView report_reply_num;
		TextView report_time;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
}
