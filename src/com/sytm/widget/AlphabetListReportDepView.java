package com.sytm.widget;

import java.util.HashMap;

import com.lnpdit.chatuidemo.R;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sytm.widget.AlphabetView.OnTouchingLetterChangedListener;

public class AlphabetListReportDepView extends RelativeLayout{
	private Context mContext;
	private RTPullListView mListView;
	private AlphabetView mAlphabetView;
	private HashMap<String, Integer> alphaIndexer;
	private static TextView overlay;
	private Handler handler;
	private OverlayThread overlayThread;
	public static WindowManager windowManager;
	private static WindowManager.LayoutParams lp;

	public AlphabetListReportDepView(Context context) {
		super(context);
		init(context);
	}

	public AlphabetListReportDepView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AlphabetListReportDepView(Context context, AttributeSet attrs,
			int defStyle) {
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
		mAlphabetView.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

					public void onTouchingLetterChanged(String s) {

						if (alphaIndexer == null) {
							// throw new
							// RuntimeException("setAlphabetIndex閺傝纭堕張顏囩ゴ閸婏拷);
						} else {
							final Integer position = alphaIndexer.get(s);
							if (position != null) {
								mListView.setSelection(position);
								overlay.setText(s);
								overlay.setVisibility(View.VISIBLE);
								handler.removeCallbacks(overlayThread);
								// 瀵ゆ儼绻滄稉锟筋瀾閸氬孩澧界悰宀嬬礉鐠侊箰verlay娑撹桨绗夐崣顖濐瀫
								handler.postDelayed(overlayThread, 1500);
							} else if ("#".equals(s)) {
								mListView.setSelection(0);
								overlay.setText(s);
								overlay.setVisibility(View.VISIBLE);
								handler.removeCallbacks(overlayThread);
								// 瀵ゆ儼绻滄稉锟筋瀾閸氬孩澧界悰宀嬬礉鐠侊箰verlay娑撹桨绗夐崣顖濐瀫
								handler.postDelayed(overlayThread, 1500);
							}
						}
					}
				});
	}

	/**
	 * 鐠佸墽鐤嗘＃鏍х摟濮ｅ车itle閸愬硛istView娑擃厾娈戞担宥囩枂
	 * 
	 * @param alphaIndexer
	 */
	public void setAlphabetIndex(HashMap<String, Integer> alphaIndexer) {
		this.alphaIndexer = alphaIndexer;
	}

	public void onRefreshComplete() {
		mListView.onRefreshComplete();
	}

	// 閸掓繂顬婇崠鏍ㄧ溄鐠囶厽瀚鹃棅鎶筋湚鐎涙鐦濆鐟板毉閹绘劗銇氬锟�	
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

	public static void remove() {
		Log.i("鍘绘帀", overlay+">>>");
		try {
			windowManager.removeView(overlay);
		} catch (Exception e) {
			
		}
		
	}

	public static void removetoadd() {
		windowManager.addView(overlay, lp);
	}

	/**
	 * 鐠佸墽鐤唎verlay娑撳秴褰茬憴锟�	 */
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
	 * 鐠佸墽鐤嗙�妤佺槤閸掓銆冩妯款吇妫版粏澹�
	 * 
	 * @param color
	 */
	public void setDefaultColor(int color) {
		if (mAlphabetView != null) {
			mAlphabetView.setDefaultColor(color);
		}
	}

	/**
	 * 鐠佸墽鐤嗙�妤佺槤閸掓銆冮柅澶夎厬妫版粏澹�
	 * 
	 * @param color
	 */
	public void setSelectColor(int color) {
		if (mAlphabetView != null) {
			mAlphabetView.setSelectColor(color);
		}
	}

	/**
	 * 鐠佸墽鐤嗙�妞剧秼婢堆冪毈
	 * 
	 * @param size
	 */
	public void setTextSize(float size) {
		if (mAlphabetView != null) {
			mAlphabetView.setTextSize(size);
		}
	}

	/**
	 * 鐠佸墽鐤嗛幖婊呭偍icon
	 * 
	 * @param resid
	 */
	public void setSearchIcon(int resid) {
		if (mAlphabetView != null) {
			mAlphabetView.setSearchIcon(resid);
		}
	}

	/**
	 * 鐠佸墽鐤嗛幖婊呭偍icon
	 * 
	 * @param resid
	 */
	public void setSearchIcon(Drawable drawable) {
		if (mAlphabetView != null) {
			mAlphabetView.setSearchIcon(drawable);
		}
	}

	/**
	 * 鐠佸墽鐤嗙槐銏犵穿濞搭喚鐛ョ�妤�娇
	 * 
	 * @param size
	 */
	public void setOverlayTextSize(float size) {
		if (overlay != null) {
			overlay.setTextSize(size);
		}
	}

	/**
	 * 鐠佸墽鐤嗙槐銏犵穿濞搭喚鐛ョ�妞剧秼妫版粏澹�
	 * 
	 * @param color
	 */
	public void setOverlayTextColor(int color) {
		if (overlay != null) {
			overlay.setTextColor(color);
		}
	}

	/**
	 * 鐠佸墽鐤嗙槐銏犵穿濞搭喚鐛ラ懗灞炬珯
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

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
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

	public void setAlphabetViewVisibility(int visibility) {
		if (mAlphabetView != null) {
			mAlphabetView.setVisibility(visibility);
		}
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
}
