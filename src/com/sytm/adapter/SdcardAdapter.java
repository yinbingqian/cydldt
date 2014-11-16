package com.sytm.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lnpdit.chatuidemo.R;
import com.sytm.bean.UpFileModel;
import com.sytm.common.Constant;
import com.sytm.util.DateTimeUtils;

public class SdcardAdapter extends BaseAdapter {
	List<UpFileModel> list = new ArrayList<UpFileModel>();
	Context context;
	int num= 0;
	public SdcardAdapter(Context context,List<UpFileModel> list,int num){
		this.context = context;
		this.list = list;
		this.num = num;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_itemkq, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
			holder.file_modify = (TextView) convertView.findViewById(R.id.file_modify);
			holder.file_len = (TextView) convertView.findViewById(R.id.file_len);
			holder.file_option=(ImageView) convertView.findViewById(R.id.file_option);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).getFile().isDirectory()) {
			holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.file_icon));
			holder.file_len.setVisibility(View.GONE);
			holder.file_option.setVisibility(View.GONE);
		}else {
			holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.unknown_icon));
			holder.file_len.setVisibility(View.VISIBLE);
			holder.file_option.setVisibility(View.GONE);
			if (list.get(position).getFile().length()/1024>=1024) {
				holder.file_len.setText(list.get(position).getFile().length()/1024/1024+"MB");
			}else {
				holder.file_len.setText(list.get(position).getFile().length()/1024+"KB");
			}
			if (Constant.WordFormat.contains(list.get(position).getFile().getName().substring(list.get(position).getFile().getName().lastIndexOf(".")+1))) {
				holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.word_icon));
				holder.file_option.setVisibility(View.VISIBLE);
				if (list.get(position).getTAG()==0) {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_05));
					if (list.get(position).getNum()==num) {
						holder.file_option.setVisibility(View.GONE);
					}else {
						holder.file_option.setVisibility(View.VISIBLE);
					}
				}else {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_08));
				}
			}else if (Constant.XlsFormat.contains(list.get(position).getFile().getName().substring(list.get(position).getFile().getName().lastIndexOf(".")+1))) {
				holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.xls_icon));
				holder.file_option.setVisibility(View.VISIBLE);
				if (list.get(position).getTAG()==0) {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_05));
					if (list.get(position).getNum()==num) {
						holder.file_option.setVisibility(View.GONE);
					}else {
						holder.file_option.setVisibility(View.VISIBLE);
					}
				}else {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_08));
				}
			}else if (Constant.PptFormat.contains(list.get(position).getFile().getName().substring(list.get(position).getFile().getName().lastIndexOf(".")+1))) {
				holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ppt_icon));
				holder.file_option.setVisibility(View.VISIBLE);
				if (list.get(position).getTAG()==0) {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_05));
					if (list.get(position).getNum()==num) {
						holder.file_option.setVisibility(View.GONE);
					}else {
						holder.file_option.setVisibility(View.VISIBLE);
					}
				}else {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_08));
				}
			}else if (Constant.TxtFormat.contains(list.get(position).getFile().getName().substring(list.get(position).getFile().getName().lastIndexOf(".")+1))) {
				holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.txt_icon));
				holder.file_option.setVisibility(View.VISIBLE);
				if (list.get(position).getTAG()==0) {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_05));
					if (list.get(position).getNum()==num) {
						holder.file_option.setVisibility(View.GONE);
					}else {
						holder.file_option.setVisibility(View.VISIBLE);
					}
				}else {
					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_08));
				}
			}else if (Constant.ImgFormat.contains(list.get(position).getFile().getName().substring(list.get(position).getFile().getName().lastIndexOf(".")+1))) {
				holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.img_icon));
//				holder.file_option.setVisibility(View.VISIBLE);
//				if (list.get(position).getTAG()==0) {
//					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_05));
//					if (list.get(position).getNum()==num) {
//						holder.file_option.setVisibility(View.GONE);
//					}else {
//						holder.file_option.setVisibility(View.VISIBLE);
//					}
//				}else {
//					holder.file_option.setImageDrawable(context.getResources().getDrawable(R.drawable.xz_08));
//				}
			}
		}
		holder.file_modify.setText(DateTimeUtils.getDate("yyyy-MM-dd HH:mm:ss", new Date(list.get(position).getFile().lastModified())));
		holder.file_name.setText(list.get(position).getFile().getName());
		return convertView;
	}
	class ViewHolder{
		ImageView icon;
		ImageView file_option;
		TextView file_name;
		TextView file_modify;
		TextView file_len;
	}
}
