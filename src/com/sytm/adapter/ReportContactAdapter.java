package com.sytm.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.sytm.bean.ReportContactModel;
import com.sytm.common.Constant;
import com.sytm.util.ImageUtils;

public class ReportContactAdapter extends BaseAdapter {

	private Context context;
	private List<ReportContactModel> modes;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoadingListenerImpl mImageLoadingListenerImpl;

	public ReportContactAdapter(Context context, ImageLoader imageLoader) {
		this.context = context;
		this.mImageLoader = imageLoader;
		int defaultImageId = R.drawable.contact_icon;
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(defaultImageId)
				.showImageForEmptyUri(defaultImageId)
				.showImageOnFail(defaultImageId)
				.cacheInMemory().cacheOnDisc().resetViewBeforeLoading()
				.build();
		mImageLoadingListenerImpl = new ImageLoadingListenerImpl();
	}

	public void setData(List<ReportContactModel> modes) {
		this.modes = modes;
	}

	@Override
	public int getCount() {

		return modes != null && modes.size() > 0 ? modes.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return modes != null && modes.size() > 0 ? modes.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	int item;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.report_contact_item, null);

//			holder.fistAlphaTextView = (TextView) convertView
//					.findViewById(R.id.first_alpha);
			holder.nameTextView = (TextView) convertView
					.findViewById(R.id.name);
			holder.depnameTextView = (TextView) convertView
					.findViewById(R.id.depname);
			holder.phoneTextView = (TextView) convertView
					.findViewById(R.id.phone);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.contactimg);
//			holder.fenxian = (TextView) convertView.findViewById(R.id.fenxian);
			holder.call_tel = (ImageView) convertView
					.findViewById(R.id.call_phone);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();

		}
		if (modes != null && modes.size() > 0) {
			ReportContactModel conteactMode = modes.get(position);
			if (conteactMode != null) {
				//閫変腑
				if (conteactMode.getTag()==0) {
					holder.call_tel.setBackgroundResource(R.drawable.xz_05);
				}else {
					holder.call_tel.setBackgroundResource(R.drawable.xz_08);
				}
				
				// 鍚嶇О
				String name = conteactMode.getName();
					holder.nameTextView.setText(name);

				// 鐢佃瘽
				String phone = conteactMode.getPhone();
					holder.phoneTextView.setText(phone);
				// 閮ㄩ棬
				String depname = conteactMode.getDepname();
					holder.depnameTextView.setText(depname);
				if (conteactMode.getHead().equals("")) {
					String gender = conteactMode.getGender();
					if (!TextUtils.isEmpty(gender)) {
						if (gender.equals("1")) {
							holder.imageView
									.setImageResource(R.drawable.contact_icon);
						} else {
							holder.imageView
									.setImageResource(R.drawable.contact_icon);
						}
					}
				} else {
					if (holder.imageView != null) {
						try {
							// 鍔犺浇鍥剧墖
							mImageLoader.displayImage(Constant.Service_Domain
									+ conteactMode.getHead(), holder.imageView,
									mDisplayImageOptions,
									mImageLoadingListenerImpl);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

				// 棣栧瓧姣�鍓嶅悗涓ら」瀵规瘮瀛楁瘝鏄惁鐩稿悓锛屽鏋滅浉鍚屽垯杩囨护锛屽惁鍒欐坊鍔犺繘鏉�
				String currentAlpha = conteactMode.getFirstAlpha();
				ReportContactModel mode = (position - 1) >= 0 ? modes
						.get(position - 1) : null;
				String previewStr = "";
				if (mode != null) {
					previewStr = mode.getFirstAlpha();
				}

//				if (!previewStr.equals(currentAlpha)) {
//					holder.fenxian.setVisibility(View.GONE);
////					holder.fistAlphaTextView.setVisibility(View.VISIBLE);
////					holder.fistAlphaTextView.setText(currentAlpha);
//				} else {
//					holder.fenxian.setVisibility(View.VISIBLE);
////					holder.fistAlphaTextView.setVisibility(View.GONE);
//				}
			}
		}

		return convertView;
	}
	 //鐩戝惉鍥剧墖寮傛鍔犺浇
	  public class ImageLoadingListenerImpl extends SimpleImageLoadingListener {

	    public final List<String> displayedImages = 
	          Collections.synchronizedList(new LinkedList<String>());

	    @Override
	    public void onLoadingComplete(String imageUri, View view,Bitmap bitmap) {
	      if (bitmap != null) {
	        ImageView imageView = (ImageView) view;
	        boolean isFirstDisplay = !displayedImages.contains(imageUri);
	        if (isFirstDisplay) {
	          //鍥剧墖鐨勬贰鍏ユ晥鏋�
	          FadeInBitmapDisplayer.animate(imageView, 500);
	          displayedImages.add(imageUri);
	          System.out.println("===> loading "+imageUri);
	        }
	      }
	    }
	  } 
	class ViewHolder {
//		TextView fistAlphaTextView;
		TextView nameTextView;
		TextView phoneTextView;
		TextView depnameTextView;
		ImageView imageView;
//		TextView fenxian;
		ImageView call_tel;
	}

}
