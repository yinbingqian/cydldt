package lnpdit.stategrid.informatization.adapter;

import java.util.List;

import com.lnpdit.chatuidemo.activity.NewsContentActivity;
import com.lnpdit.chatuidemo.R;
import lnpdit.stategrid.informatization.data.MessengerService;
import lnpdit.stategrid.informatization.tools.AsyncImageLoader;
import lnpdit.stategrid.informatization.tools.AsyncImageLoader.ImageCallback;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ImageAndTextNewsListAdapter extends ArrayAdapter<ImageAndTextNewsList> {

	private ListView listView;
	private AsyncImageLoader asyncImageLoader;
	Context mContext;

	public ImageAndTextNewsListAdapter(Activity activity,
			List<ImageAndTextNewsList> imageAndTexts, ListView listView,
			Context context) {
		super(activity, 0, imageAndTexts);
		this.listView = listView;
		this.mContext = context;
		asyncImageLoader = new AsyncImageLoader();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

		// Inflate the views from XML
		View rowView = convertView;
		ViewCacheNews viewCache;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_in_news, null);
			viewCache = new ViewCacheNews(rowView);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewCacheNews) rowView.getTag();
		}
		ImageAndTextNewsList imageAndText = getItem(position);

		// Load the image and set it on the ImageView
		String imageUrl = MessengerService.PIC_FILE + imageAndText.getImageUrl();
		ImageView imageView = viewCache.getImageView();
		imageView.setTag(imageUrl);
		Drawable cachedImage1 = asyncImageLoader.loadDrawable(imageUrl,
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
			imageView.setImageDrawable(cachedImage1);
		}
		// Set the text on the TextView
		TextView textViewTime = viewCache.getTextViewTime();
		textViewTime.setText(imageAndText.getTime());
		TextView textViewContent = viewCache.getTextViewContent();
		textViewContent.setText(imageAndText.getNewsTitle());

		rowView.setOnClickListener(new AdapterListener(position, imageAndText.getWebId(), imageAndText.getTitle(), imageAndText.getTime()));
		return rowView;
	}

	class AdapterListener implements OnClickListener {
		private int position;

		String id;
		String title;
		String time;

		public AdapterListener(int pos, String _id, String _title, String _time) {
			// TODO Auto-generated constructor stub
			position = pos;
			id = _id;
			title = _title;
			time = _time;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(mContext, NewsContentActivity.class);
			intent.putExtra("id", id);
			intent.putExtra("title", title);
			intent.putExtra("time", time);
			mContext.startActivity(intent);
		}

	}
}
