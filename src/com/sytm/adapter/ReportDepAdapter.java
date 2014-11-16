package com.sytm.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.lnpdit.chatuidemo.R;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.sytm.bean.ConteactModel;
import com.sytm.common.Constant;
import com.sytm.common.DepnameModel;
import com.sytm.util.ImageUtils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportDepAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DepnameModel> modes;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListenerImpl mImageLoadingListenerImpl;
	public ReportDepAdapter(Context context){
		this.context = context;
//		 this.mImageLoader = imageLoader;
//		    int defaultImageId = R.drawable.nan_04;
//		    mDisplayImageOptions = new DisplayImageOptions.Builder()
//		                       .showStubImage(defaultImageId)
//		                       .showImageForEmptyUri(defaultImageId)
//		                       .showImageOnFail(defaultImageId)
//		                       .preProcessor(new BitmapProcessor() {
//								
//								@Override
//								public Bitmap process(Bitmap arg0) {
//									return ImageUtils.toRoundBitmap(arg0);
//								}
//							})
//		                       .cacheInMemory()
//		                       .cacheOnDisc()
//		                       .resetViewBeforeLoading()
//		                       .build();
//		    mImageLoadingListenerImpl=new ImageLoadingListenerImpl();
	}
	
	
	public void setData(ArrayList<DepnameModel> modes){
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.alphalistview_item1, null);
			
			holder.fistAlphaTextView = (TextView) convertView.findViewById(R.id.first_alpha);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.name);
			holder.fenxian =(TextView) convertView.findViewById(R.id.fenxian);
			convertView.setTag(holder);
		}else{
			
			holder = (ViewHolder) convertView.getTag();
			
		}

		if(modes != null && modes.size() > 0){
			DepnameModel conteactMode = modes.get(position);
			if(conteactMode != null){
				
				//名称
				String name = conteactMode.getDepname();
					holder.nameTextView.setText(name);
	
				//首字母(前后两项对比字母是否相同，如果相同则过滤，否则添加进来)
				String currentAlpha = conteactMode.getFirstAlpha();
				DepnameModel mode = (position - 1) >= 0 ? modes.get(position - 1)  : null;
				String previewStr = "";
				if(mode != null){
					previewStr = mode.getFirstAlpha();
				}
				
				if (!previewStr.equals(currentAlpha)) {
					holder.fenxian.setVisibility(View.GONE);
					holder.fistAlphaTextView.setVisibility(View.VISIBLE);
					holder.fistAlphaTextView.setText(currentAlpha);
				}else{
					holder.fenxian.setVisibility(View.VISIBLE);
					holder.fistAlphaTextView.setVisibility(View.GONE);
				}
			}
		}
		
		
		return convertView;
	}
	 //监听图片异步加载
	  public class ImageLoadingListenerImpl extends SimpleImageLoadingListener {

	    public final List<String> displayedImages = 
	          Collections.synchronizedList(new LinkedList<String>());

	    @Override
	    public void onLoadingComplete(String imageUri, View view,Bitmap bitmap) {
	      if (bitmap != null) {
//	        ImageView imageView = (ImageView) view;
	        boolean isFirstDisplay = !displayedImages.contains(imageUri);
	        if (isFirstDisplay) {
	          //图片的淡入效果
//	          FadeInBitmapDisplayer.animate(imageView, 500);
	          displayedImages.add(imageUri);
	        }
	        if (bitmap.isRecycled()) {
				bitmap.recycle();
			}
	      }
	      bitmap =null;
	    }
	  } 
	class ViewHolder{
		TextView fistAlphaTextView;
		TextView nameTextView;
		TextView fenxian;
	}

}
