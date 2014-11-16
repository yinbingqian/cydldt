package com.sytm.tmkq;


import com.lnpdit.chatuidemo.R;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class MapActivity extends Activity {
	private BaiduMap mBaiduMap;
	private MapView mMapView = null;
	private InfoWindow mInfoWindow;
	private Button back;
	private TextView map_title;
	private boolean open = true;
	private Intent intent;
	private BitmapDescriptor bitmap;
	private String lng = "", lat = "",GetType = "",Addr="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		map_title = (TextView) findViewById(R.id.map_title);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		back = (Button) findViewById(R.id.back);
		SharedPreferences sp = this.getSharedPreferences("user_info",
				MODE_APPEND);
		intent = getIntent();
		lng = intent.getStringExtra("Lng");
		lat = intent.getStringExtra("Lat");
		GetType = intent.getStringExtra("GetType");
		Addr = intent.getStringExtra("Addr");
		map_title.setText(sp.getString("RealName", ""));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mBaiduMap = mMapView.getMap();
		// 普通地图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		//定义Maker坐标点  
		LatLng point = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.sendtocar_balloon);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示  
		mBaiduMap.addOverlay(option);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 16);
		mBaiduMap.setMapStatus(u);
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				View view = LayoutInflater.from(MapActivity.this).inflate(
						R.layout.mapview, null);
				TextView addr = (TextView) view.findViewById(R.id.map_addr);
				TextView style = (TextView) view.findViewById(R.id.map_sytle);
				addr.setText(Addr);
				style.setText(GetType);
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 15;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
							public void onInfoWindowClick() {
								mBaiduMap.hideInfoWindow();
							};
				};
				BitmapDescriptor arg0 = BitmapDescriptorFactory.fromView(view);
				mInfoWindow = new InfoWindow(arg0, llInfo, 0, listener);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
	}







	Dialog dialog;

	public void myDialog(String msg) {
		View v = View.inflate(this, R.layout.dialog_alone, null);
		TextView textView = (TextView) v.findViewById(R.id.dialog_title);
		Button btn = (Button) v.findViewById(R.id.dialog_button);
		dialog = new Dialog(this, R.style.dialog_style);
		dialog.setContentView(v);
		dialog.setCancelable(false);
		textView.setText(msg);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		if (open) {
			dialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		open = false;
		if (bitmap!=null) {
			bitmap.recycle();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}
