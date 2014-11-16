package lnpdit.stategrid.informatization.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.lnpdit.chatuidemo.R;
import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.tools.AsyncImageLoader;
import lnpdit.stategrid.informatization.tools.AsyncImageLoader.ImageCallback;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class JobBackAdapter extends BaseAdapter {
	private class buttonViewHolder {
		TextView apptime;
		TextView appsender;
		TextView appcontent;
		ImageView appimg;
	}

	private ArrayList<HashMap<String, Object>> mAppList;
	private LayoutInflater mInflater;
	private Context mContext;
	private String[] keyString;
	private buttonViewHolder holder;
	private AsyncImageLoader asyncImageLoader;
	private ListView listView;

	public JobBackAdapter(Context c,
			ArrayList<HashMap<String, Object>> appList, int resource,
			String[] from, int[] to, ListView _listview) {
		mAppList = appList;
		mContext = c;
		listView = _listview;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		keyString = new String[from.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
		asyncImageLoader = new AsyncImageLoader();
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
			convertView = mInflater.inflate(R.layout.list_in_jobback, null);
			holder = new buttonViewHolder();
			holder.apptime = (TextView) convertView.findViewById(R.id.time_tv);
			holder.appsender = (TextView) convertView.findViewById(R.id.sender_tv);
			holder.appcontent = (TextView) convertView.findViewById(R.id.content_tv);
			holder.appimg = (ImageView) convertView.findViewById(R.id.pic_img);
			convertView.setTag(holder);
		}
		HashMap<String, Object> appInfo = mAppList.get(position);
		if (appInfo != null) {
			String FromName = (String) appInfo.get(keyString[0]);
			String ToName = (String) appInfo.get(keyString[1]);
			String Id = (String) appInfo.get(keyString[2]);
			String FromUser = (String) appInfo.get(keyString[3]);
			String ToUser = (String) appInfo.get(keyString[4]);
			String Content = (String) appInfo.get(keyString[5]);
			String Photo = (String) appInfo.get(keyString[6]);
			String Type = (String) appInfo.get(keyString[7]);
			String Crtime = (String) appInfo.get(keyString[8]);

			holder.apptime.setText(Crtime);
			holder.appsender.setText("发送人"+FromName);
			holder.appcontent.setText(Content);
			
			String img_url = MessengerService.PIC_JOB + Photo;
			holder.appimg.setTag(img_url);
			Drawable cachedImage1 = asyncImageLoader.loadDrawable(img_url,
					new ImageCallback() {
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
							if (imageViewByTag != null) {
								imageViewByTag.setImageDrawable(imageDrawable);
							}
						}
					});
			if (cachedImage1 == null) {
				// imageView.setImageResource(R.drawable.default_microhot);
			} else {
				holder.appimg.setImageDrawable(cachedImage1);
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
