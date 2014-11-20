package com.sytm.adapter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.bean.MyReportListModel;
import com.sytm.util.DateTimeUtils;

public class ReportAdapter extends BaseAdapter {
	Context context;
	List<MyReportListModel> list = new ArrayList<MyReportListModel>();
	public ReportAdapter(Context context, List<MyReportListModel> list) {
		this.context = context;
		this.list =list;
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
			convertView = inflater.inflate(R.layout.report_item, null);
			holder = new ViewHolder();
			holder.lin_top= (LinearLayout) convertView.findViewById(R.id.lin_top);
			holder.report_top_day =  (TextView) convertView.findViewById(R.id.report_top_day);
			holder.report_top_date =  (TextView) convertView.findViewById(R.id.report_top_date);
			holder.report_title =  (TextView) convertView.findViewById(R.id.report_title);
			holder.report_sheng =  (TextView) convertView.findViewById(R.id.report_sheng);
			holder.report_reply_num =  (TextView) convertView.findViewById(R.id.report_reply_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
				holder.report_top_date.setText(getDate(list.get(position).getAdddate()));
				holder.report_top_day.setText(getTimeTypeHm(list.get(position).getAdddate()));
//				holder.report_title.setText(list.get(position).getAdddate().substring(list.get(position).getAdddate().lastIndexOf("/")+1, list.get(position).getAdddate().indexOf(" "))+"日的工作日志");
				holder.report_title.setText(list.get(position).getTitle());
				if (list.get(position).getTitle().equals("")) {
					holder.report_title.setText(list.get(position).getAdddate().substring(list.get(position).getAdddate().lastIndexOf("/")+1, list.get(position).getAdddate().indexOf(" "))+"日的邮件");
				}
				holder.report_sheng.setText(list.get(position).getProvince()+" "+list.get(position).getCity()+" "+list.get(position).getDistrict());
				if (list.get(position).getReplycount().equals("0")) {
					holder.report_reply_num.setVisibility(View.GONE);
				}else {
					holder.report_reply_num.setVisibility(View.VISIBLE);
					holder.report_reply_num.setText(list.get(position).getReplycount());
				}
		return convertView;
	}

	class ViewHolder {
		LinearLayout lin_top;
		TextView report_top_day;
		TextView report_top_date;
		TextView report_title;
		TextView report_sheng;
		TextView report_reply_num;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	public String getTimeTypeHm(String formatdate) {
		SimpleDateFormat formatBuilder = new SimpleDateFormat("HH:mm:ss");
		return formatBuilder.format(StringToDate(formatdate));
	}
	public String getDate(String formatdate) {
		SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy-MM-dd");
		return formatBuilder.format(StringToDate(formatdate));
	}
	public Date StringToDate(String dateStr){
		String formatStr = "yyyy/MM/dd HH:mm:ss";
		SimpleDateFormat dd=new SimpleDateFormat(formatStr);
		Date date=null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
		return date;
	}
}
