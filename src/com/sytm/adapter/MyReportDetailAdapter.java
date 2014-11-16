package com.sytm.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.bean.ReportDetail_ReplyModel;

public class MyReportDetailAdapter extends BaseAdapter {
	Context context;
	String empid = "";
	int tag=0;
	List<ReportDetail_ReplyModel> list = new ArrayList<ReportDetail_ReplyModel>();

	public MyReportDetailAdapter(Context context,
			List<ReportDetail_ReplyModel> list, String empid,int tag) {
		this.context = context;
		this.list = list;
		this.empid = empid;
		this.tag = tag;
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
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.myreportdetail_item, null);
			holder.report_detail_listview_name = (TextView) convertView
					.findViewById(R.id.report_detail_listview_name);
			holder.report_detail_listview_content = (TextView) convertView
					.findViewById(R.id.report_detail_listview_content);
			holder.report_detail_listview_date = (TextView) convertView
					.findViewById(R.id.report_detail_listview_date);
			holder.listview_huifu = (TextView) convertView
					.findViewById(R.id.listview_huifu);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();

		}
		if (empid.equals(list.get(position).getRepempid())) {

			holder.report_detail_listview_name.setText(Html.fromHtml("<font color='#53A701'>"
					+ context.getResources().getString(R.string.me) + "</font>  <font color='#aaaaaa'>" + context.getResources().getString(R.string.reply)
					+ "</font> <font color='#FF6600'>"
					+ list.get(position).getToempname() + "</font>"));
		}
		if (empid.equals(list.get(position).getToempid())) {
			holder.report_detail_listview_name.setText(Html.fromHtml("<font color='#FF6600'>"
					+ list.get(position).getRepempname()
					+ "</font>  <font color='#aaaaaa'>" + context.getResources().getString(R.string.reply)
					+ "</font>  <font color='#53A701'>" + context.getResources().getString(R.string.me) + "</font>"));
		}
		if (empid.equals(list.get(position).getRepempid())
				&& empid.equals(list.get(position).getToempid())) {
			holder.report_detail_listview_name.setText(context.getResources().getString(R.string.me) + context.getResources().getString(R.string.reply)+ context.getResources().getString(R.string.me));
		}else if(!empid.equals(list.get(position).getRepempid())
				&& !empid.equals(list.get(position).getToempid())){
			holder.report_detail_listview_name.setText(Html.fromHtml("<font color='#FF6600'>"
					+ list.get(position).getRepempname()
					+ "</font>  <font color='#aaaaaa'>" + context.getResources().getString(R.string.reply)
					+ "</font>  <font color='#FF6600'>" + list.get(position).getToempname() + "</font>"));
		}
		holder.report_detail_listview_content.setText(list.get(position)
				.getDetail());
		holder.report_detail_listview_date.setText(list.get(position)
				.getRepdate());
		
		return convertView;
	}

	class ViewHolder {
		TextView report_detail_listview_name;
		TextView report_detail_listview_content;
		TextView report_detail_listview_date;
		TextView listview_huifu;
	}
}
