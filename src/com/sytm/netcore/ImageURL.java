package com.sytm.netcore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.lnpdit.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.sytm.common.Constant;
import com.sytm.util.ImageUtils;

public class ImageURL {
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoadingListenerImpl mImageLoadingListenerImpl;
	private BitmapProcessor bitmapProcessor = new BitmapProcessor() {

		@Override
		public Bitmap process(Bitmap arg0) {
			return ImageUtils.toRoundBitmap(arg0);
		}
	};

	public Bitmap returnBitMap(String path) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(Constant.Service_Domain + path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (myFileUrl != null) {
			try {

				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public void loadImage(String url, ImageView imageView) {
		mImageLoader = ImageLoader.getInstance();
		int defaultImageId = R.drawable.nan_04;
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(defaultImageId)
				.showImageForEmptyUri(defaultImageId)
				.showImageOnFail(defaultImageId).preProcessor(bitmapProcessor)
				// .postProcessor(bitmapProcessor)
				.cacheInMemory().cacheOnDisc().resetViewBeforeLoading().build();
		mImageLoadingListenerImpl = new ImageLoadingListenerImpl();
		try {
			// 加载图片
			mImageLoader.displayImage(Constant.Service_Domain + url, imageView,
					mDisplayImageOptions, mImageLoadingListenerImpl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 监听图片异步加载
	public class ImageLoadingListenerImpl extends SimpleImageLoadingListener {

		public final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
			if (bitmap != null) {
//				ImageView imageView = (ImageView) view;
				boolean isFirstDisplay = !displayedImages.contains(imageUri);
				if (isFirstDisplay) {
					// 图片的淡入效果
//					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
//					System.out.println("===> loading " + imageUri);
				}
				if (bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
			 bitmap =null;
		}
	}
}
