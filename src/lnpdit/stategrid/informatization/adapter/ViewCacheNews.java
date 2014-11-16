package lnpdit.stategrid.informatization.adapter;

import com.lnpdit.chatuidemo.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCacheNews {

	private View baseView;
	private ImageView headImage;
	private TextView contentText;
	private TextView timeText;
	
	public ViewCacheNews(View baseView) {
		this.baseView = baseView;
	}

	public ImageView getImageView() {
		if (headImage == null) {
			headImage = (ImageView) baseView.findViewById(R.id.list_in_news_icon);
		}
		return headImage;
	}
	
	public TextView getTextViewContent() {
		if (contentText == null) {
			contentText = (TextView) baseView.findViewById(R.id.list_in_news_content);
		}
		return contentText;
	}

	public TextView getTextViewTime() {
		if (timeText == null) {
			timeText = (TextView) baseView.findViewById(R.id.list_in_news_time);
		}
		return timeText;
	}

}
