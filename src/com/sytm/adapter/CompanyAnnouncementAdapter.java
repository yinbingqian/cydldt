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
import com.sytm.bean.CompanyAnnouncementMode;

public class CompanyAnnouncementAdapter extends BaseAdapter {

	Context context;
	List<CompanyAnnouncementMode> list = new ArrayList<CompanyAnnouncementMode>();

	public CompanyAnnouncementAdapter(Context context,
			List<CompanyAnnouncementMode> list) {
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

	CompanyAnnouncementMode Mode;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.companyannouncement_item, null);
			holder = new ViewHolder();
			holder.lin_top = (LinearLayout) convertView
					.findViewById(R.id.lin_top);
			holder.company_top_day = (TextView) convertView
					.findViewById(R.id.company_top_day);
			holder.company_top_date = (TextView) convertView
					.findViewById(R.id.company_top_date);
			holder.company_title = (TextView) convertView
					.findViewById(R.id.company_title);
			holder.company_sheng = (TextView) convertView
					.findViewById(R.id.company_sheng);
			holder.company_reply_num = (TextView) convertView
					.findViewById(R.id.company_reply_num);
			holder.company_time = (TextView) convertView
					.findViewById(R.id.company_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!list.get(position).getDay().equals("")) {
			holder.lin_top.setVisibility(View.VISIBLE);
			holder.company_top_day.setText(list.get(position).getDay() + "");
			holder.company_top_date.setText(list.get(position).getYear() + "."
					+ list.get(position).getMonth());
			holder.lin_top.setBackgroundResource(R.drawable.hb_02);
		} else {
			holder.lin_top.setVisibility(View.GONE);
		}
		holder.company_title.setText(list.get(position).getTitle());
		holder.company_sheng.setText(list.get(position).getTypename());
		if (list.get(position).getIsread().equals("1")) {
			holder.company_reply_num.setVisibility(View.GONE);
		} else {
			holder.company_reply_num.setVisibility(View.VISIBLE);
			holder.company_reply_num.setText(context.getResources().getString(R.string.new_word));
		}
		holder.company_time
				.setText(list
						.get(position)
						.getCreatetime()
						.substring(
								list.get(position).getCreatetime().indexOf(" ") + 1));
		return convertView;
	}

	class ViewHolder {
		LinearLayout lin_top;
		TextView company_top_day;
		TextView company_top_date;
		TextView company_title;
		TextView company_sheng;
		TextView company_reply_num;
		TextView company_time;
	}
}
