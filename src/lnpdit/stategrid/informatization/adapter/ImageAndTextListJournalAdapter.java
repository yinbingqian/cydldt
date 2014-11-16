package lnpdit.stategrid.informatization.adapter;

import java.util.List;

import com.lnpdit.chatuidemo.activity.JournalInfoActivity;
import com.lnpdit.chatuidemo.R;
import lnpdit.stategrid.informatization.data.ToDoDB;
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

public class ImageAndTextListJournalAdapter extends ArrayAdapter<ImageAndTextJournal> {

	private ListView listView;
	private AsyncImageLoader asyncImageLoader;
	Context mContext;

	public ImageAndTextListJournalAdapter(Activity activity,
			List<ImageAndTextJournal> imageAndTexts, ListView listView, Context context) {
		super(activity, 0, imageAndTexts);
		this.listView = listView;
		this.mContext = context;
		asyncImageLoader = new AsyncImageLoader();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

		// Inflate the views from XML
		View rowView = convertView;
		ViewCacheJournal viewCache;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_in_journallist, null);
			viewCache = new ViewCacheJournal(rowView);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewCacheJournal) rowView.getTag();
		}
		ImageAndTextJournal imageAndText = getItem(position);

		// Load the image and set it on the ImageView
		String imageUrl1 = imageAndText.getImageUrl1();
		ImageView imageView1 = viewCache.getImageView1();
		String content1 = imageAndText.getContent1();
		String imageUrl2 = imageAndText.getImageUrl2();
		ImageView imageView2 = viewCache.getImageView2();
		String content2 = imageAndText.getContent2();
		imageView1.setTag(imageUrl1);
		imageView2.setTag(imageUrl2);
		Drawable cachedImage1 = asyncImageLoader.loadDrawable(imageUrl1,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(imageDrawable);
						}
					}
				});
		Drawable cachedImage2 = asyncImageLoader.loadDrawable(imageUrl2,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(imageDrawable);
						}
					}
				});
		if (cachedImage1 == null) {
			imageView1.setImageResource(R.drawable.default_microhot);
		} else {
			imageView1.setImageDrawable(cachedImage1);
		}
		if (cachedImage2 == null) {
			imageView2.setImageResource(R.drawable.default_microhot);
		} else {
			imageView2.setImageDrawable(cachedImage2);
		}
		// Set the text on the TextView
		TextView textViewTitle1 = viewCache.getTextViewText1();
		textViewTitle1.setText(imageAndText.getTitle1());
		TextView textViewTitle2 = viewCache.getTextViewText2();
		textViewTitle2.setText(imageAndText.getTitle2());
		
		if(imageAndText.getTitle2().toString().equals("none")){
			imageView2.setVisibility(8);
			textViewTitle2.setVisibility(8);
		}
		imageView1.setOnClickListener(new AdapterListener(position, imageView1, imageUrl1, imageAndText.getWebId1(), content1));
		imageView2.setOnClickListener(new AdapterListener(position, imageView2, imageUrl2, imageAndText.getWebId2(), content2));

		return rowView;
	}

	class AdapterListener implements OnClickListener {
		private int position;
		private ImageView iv;
		private String p;
		private String id;
		private String content;

		public AdapterListener(int pos, ImageView _iv, String _p, String _id, String _content) {
			// TODO Auto-generated constructor stub
			position = pos;
			iv = _iv;
			p = _p;
			id = _id;
			this.content = _content;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(mContext, JournalInfoActivity.class);
			intent.putExtra("PIC", p);
			intent.putExtra("WEBID", id);
			intent.putExtra("CONTENT", content);
			ToDoDB tdd = new ToDoDB(mContext);
			tdd.clearjournalinfo();
			mContext.startActivity(intent);
		}

	}

}
