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
	public ReportDepAdapter(Context context ,ArrayList<DepnameModel> modes){
		this.context = context;
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
			holder.tm_dept = (TextView) convertView.findViewById(R.id.tm_dept);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tm_dept.setText(modes.get(position).getDepname());
		return convertView;
	}
	class ViewHolder{
		TextView tm_dept;
	}

}
