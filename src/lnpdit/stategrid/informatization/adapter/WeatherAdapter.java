package lnpdit.stategrid.informatization.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.lnpdit.chatuidemo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherAdapter extends BaseAdapter {
	private class buttonViewHolder {
		TextView apptemp;
		TextView appdate;
		TextView apphum;
		TextView appwind;
		TextView appwindspeed;
		RelativeLayout apptemplayout;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private buttonViewHolder holder;

	public WeatherAdapter(Context c,
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
			convertView = mInflater.inflate(R.layout.list_in_weather, null);
			holder = new buttonViewHolder();
			holder.apptemp = (TextView) convertView.findViewById(R.id.temp_tv);
			holder.appdate = (TextView) convertView.findViewById(R.id.date_tv);
			holder.apphum = (TextView) convertView.findViewById(R.id.hum_tv);
			holder.appwind = (TextView) convertView.findViewById(R.id.wind_tv);
			holder.appwindspeed = (TextView) convertView
					.findViewById(R.id.windspeed_tv);
			holder.apptemplayout = (RelativeLayout) convertView
					.findViewById(R.id.left_layout);
			convertView.setTag(holder);
		}
		HashMap<String, Object> appInfo = mAppList.get(position);
		if (appInfo != null) {
			String id = (String) appInfo.get(keyString[0]);
			String date = (String) appInfo.get(keyString[1]);
			String crtime = (String) appInfo.get(keyString[2]);
			String temp = (String) appInfo.get(keyString[3]);
			String hum = (String) appInfo.get(keyString[4]);
			String wind = (String) appInfo.get(keyString[5]);
			String windspeed = (String) appInfo.get(keyString[6]);
			String alarmlevel = (String) appInfo.get(keyString[7]);
			String userid = (String) appInfo.get(keyString[8]);

			holder.appdate.setText(date);
			holder.apptemp.setText(temp + "℃");
			holder.apphum.setText("湿度：" + hum + "%");
			holder.appwind.setText("风向：" + wind);
			holder.appwindspeed.setText("风速：" + windspeed);
			if (alarmlevel.equals("1")) {//yellow
				holder.apptemplayout.setBackgroundResource(R.color.weather_yellow);
			} else if(alarmlevel.equals("2")){//orange
				holder.apptemplayout.setBackgroundResource(R.color.weather_orange);
			} else if(alarmlevel.equals("3")){//red
				holder.apptemplayout.setBackgroundResource(R.color.weather_red);
			} else if(alarmlevel.equals("4")){//blue
				holder.apptemplayout.setBackgroundResource(R.color.weather_blue);
			}
			// convertView.setOnClickListener(new AdapterListener(position, id,
			// title, content, time, mark, aging, status, count));
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
		private String title;
		private String content;
		private String time;
		private String mark;
		private String aging;
		private String status;
		private String count;

		public AdapterListener(int pos, String _id, String _title,
				String _content, String _time, String _mark, String _aging,
				String _status, String _count) {
			// TODO Auto-generated constructor stub
			position = pos;
			id = _id;
			title = _title;
			content = _content;
			time = _time;
			mark = _mark;
			aging = _aging;
			status = _status;
			count = _count;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Intent intent = new Intent();
			// intent.setClass(mContext, TopicReplyActivity.class);
			// intent.putExtra("Webid", Webid);
			// intent.putExtra("Title", Title);
			// intent.putExtra("Content", Content);
			// intent.putExtra("Time", Time);
			// intent.putExtra("Number", Number);
			// mContext.startActivity(intent);
			// Intent intent = new Intent();
			// intent.setClass(mContext, QuestionContentActivity.class);
			// intent.putExtra("id", id);
			// intent.putExtra("title", title);
			// intent.putExtra("content", content);
			// intent.putExtra("time", time);
			// intent.putExtra("mark", mark);
			// intent.putExtra("aging", aging);
			// intent.putExtra("status", status);
			// intent.putExtra("count", count);
			// mContext.startActivity(intent);
		}
	}
}
