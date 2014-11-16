package com.sytm.widget;

import java.util.HashMap;

import com.lnpdit.chatuidemo.R;
import com.sytm.widget.AlphabetView.OnTouchingLetterChangedListener;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlphabetListView extends RelativeLayout {
	private Context mContext;
	private RTPullListView mListView;
	private AlphabetView mAlphabetView;
	private HashMap<String, Integer> alphaIndexer;
	private TextView overlay;
	private Handler handler;
	private OverlayThread overlayThread;
	public  WindowManager windowManager;
	private static WindowManager.LayoutParams lp;

	public AlphabetListView(Context context) {
		super(context);
		init(context);
	}

	public AlphabetListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AlphabetListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {

		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.alphabet_list, this, true);
		mListView = (RTPullListView) findViewById(R.id.list_view);
		mAlphabetView = (AlphabetView) findViewById(R.id.alphabet_view);
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
		mAlphabetView
				.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

					public void onTouchingLetterChanged(String s) {

						if (alphaIndexer == null) {
							// throw new
							// RuntimeException("setAlphabetIndex鏂规硶鏈祴鍊�);
						} else {
							final Integer position = alphaIndexer.get(s);
							if (position != null) {
								mListView.setSelection(position);
								overlay.setText(s);
								overlay.setVisibility(View.VISIBLE);
								handler.removeCallbacks(overlayThread);
								// 寤惰繜涓�鍚庢墽琛岋紝璁﹐verlay涓轰笉鍙
								handler.postDelayed(overlayThread, 1500);
							} else if ("#".equals(s)) {
								mListView.setSelection(0);
								overlay.setText(s);
								overlay.setVisibility(View.VISIBLE);
								handler.removeCallbacks(overlayThread);
								// 寤惰繜涓�鍚庢墽琛岋紝璁﹐verlay涓轰笉鍙
								handler.postDelayed(overlayThread, 1500);
							}
						}
					}
				});
	}

	/**
	 * 璁剧疆棣栧瓧姣峵itle鍐峀istView涓殑浣嶇疆
	 * 
	 * @param alphaIndexer
	 */
	public void setAlphabetIndex(HashMap<String, Integer> alphaIndexer) {
		this.alphaIndexer = alphaIndexer;
	}

	public void onRefreshComplete() {
		mListView.onRefreshComplete();
	}

	private void initOverlay() {
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	public void remove() {
		windowManager.removeView(overlay);
	}

	public void removetoadd() {
		windowManager.addView(overlay, lp);
	}

	/**
	 * 璁剧疆overlay涓嶅彲瑙�
	 */
	public class OverlayThread implements Runnable {

		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	public void setListViewBackgroundResource(int resid) {
		if (mListView != null) {
			mListView.setBackgroundResource(resid);
		}
	}

	@SuppressWarnings("deprecation")
	public void setListViewBackgroundDrawable(Drawable d) {
		if (mListView != null) {
			mListView.setBackgroundDrawable(d);
		}
	}

	public void setListViewDivider(Drawable divider) {
		if (mListView != null) {
			mListView.setDivider(divider);
		}
	}

	public void setAdapter(BaseAdapter adapter) {
		if (mListView != null) {
			mListView.setAdapter(adapter);
		}
	}

	public void setListViewsetVisibility(int visibility) {
		if (mListView != null) {
			mListView.setVisibility(visibility);
		}
	}

	/**
	 * 璁剧疆瀛楁瘝鍒楄〃榛樿棰滆壊
	 * 
	 * @param color
	 */
	public void setDefaultColor(int color) {
		if (mAlphabetView != null) {
			mAlphabetView.setDefaultColor(color);
		}
	}

	/**
	 * 璁剧疆瀛楁瘝鍒楄〃閫変腑棰滆壊
	 * 
	 * @param color
	 */
	public void setSelectColor(int color) {
		if (mAlphabetView != null) {
			mAlphabetView.setSelectColor(color);
		}
	}

	/**
	 * 璁剧疆瀛椾綋澶у皬
	 * 
	 * @param size
	 */
	public void setTextSize(float size) {
		if (mAlphabetView != null) {
			mAlphabetView.setTextSize(size);
		}
	}

	/**
	 * 璁剧疆鎼滅储icon
	 * 
	 * @param resid
	 */
	public void setSearchIcon(int resid) {
		if (mAlphabetView != null) {
			mAlphabetView.setSearchIcon(resid);
		}
	}

	/**
	 * 璁剧疆鎼滅储icon
	 * 
	 * @param resid
	 */
	public void setSearchIcon(Drawable drawable) {
		if (mAlphabetView != null) {
			mAlphabetView.setSearchIcon(drawable);
		}
	}

	// /**
	// * 璁剧疆鏄惁鏄剧ず鎼滅储icon
	// *
	// * @param isShowSearchIcon
	// */
	// public void setShowSearchIcon(boolean isShowSearchIcon) {
	// if (mAlphabetView != null) {
	// mAlphabetView.setShowSearchIcon(isShowSearchIcon);
	// }
	// }

	/**
	 * 璁剧疆绱㈠紩娴獥瀛楀彿
	 * 
	 * @param size
	 */
	public void setOverlayTextSize(float size) {
		if (overlay != null) {
			overlay.setTextSize(size);
		}
	}

	/**
	 * 璁剧疆绱㈠紩娴獥瀛椾綋棰滆壊
	 * 
	 * @param color
	 */
	public void setOverlayTextColor(int color) {
		if (overlay != null) {
			overlay.setTextColor(color);
		}
	}

	/**
	 * 璁剧疆绱㈠紩娴獥鑳屾櫙
	 * 
	 * @param resId
	 */
	public void setOverlayBackground(int resid) {
		if (overlay != null) {
			overlay.setBackgroundResource(resid);
		}
	}

	public void setOnItemClickListener(final OnItemClickListener listener) {
		if (mListView != null) {
			mListView
					.setOnItemClickListener(new ListView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
							if (listener != null) {
								listener.onItemClick(arg0, arg1, arg2, arg3);
							}

						}
					});
		}
	}

	public void setOnItemLongClickListener(
			final OnItemLongClickListener listener) {
		if (mListView != null) {
			mListView
					.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (listener != null) {
								listener.onItemLongClick(arg0, arg1, arg2, arg3);
							}
							return false;
						}
					});
		}
	}

	public interface OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id);
	}

	public interface OnItemLongClickListener {
		public void onItemLongClick(AdapterView<?> parent, View view,
				int position, long id);
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void setonRefreshListener(final OnRefreshListener listener) {
		if (mListView != null) {
			mListView
					.setonRefreshListener(new RTPullListView.OnRefreshListener() {

						@Override
						public void onRefresh() {
							if (listener!=null) {
								listener.onRefresh();
							}
						}

					});
		}
	}

	public void setAlphabetViewVisibility(int visibility) {
		if (mAlphabetView != null) {
			mAlphabetView.setVisibility(visibility);
		}
	}

}
