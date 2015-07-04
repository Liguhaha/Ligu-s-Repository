package com.zy.iparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.zy.tools.ScaleView;
import com.zy.tools.ZoomControlView;

public class MainActivity extends Activity implements OnClickListener {

	private MapView mMapView = null;
	private ZoomControlView mZoomControlView = null;
	private ScaleView mScaleView = null;
	private BaiduMap mBaiduMap = null;
	private LinearLayout llMapView = null;
	private int width, height;
	private LocationClient mClient = null;
	private boolean isFirstLoc = true;// 是否首次定位
	private Button btParking = null;
	private Button btNavi = null;
	private LatLng ll = null;// 当前坐标
	private String city = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		btParking = (Button) findViewById(R.id.bt_parking);
		btNavi = (Button) findViewById(R.id.bt_navi);
		btParking.setOnClickListener(this);
		btNavi.setOnClickListener(this);
		llMapView = (LinearLayout) findViewById(R.id.ll_map_view);
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.scaleControlEnabled(true);
		// 初始化地图状态
		MapStatus mapStatus = new MapStatus.Builder().zoom(12.0f).build();
		mapOptions.zoomControlsEnabled(false).scaleControlEnabled(false)
				.mapStatus(mapStatus);
		mMapView = new MapView(this, mapOptions);
		llMapView.addView(mMapView);

		mScaleView = (ScaleView) findViewById(R.id.scaleview);
		mZoomControlView = (ZoomControlView) findViewById(R.id.zoomcontrolview);
		mBaiduMap = mMapView.getMap();

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mClient = new LocationClient(getApplicationContext());
		mClient.registerLocationListener(new MyLocationListener());
		// 百度定位
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mClient.setLocOption(option);
		mClient.start();

		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		ScaleView.setMapView(mMapView, width, height);
		mZoomControlView.setMapView(mMapView);

		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			public void onMapStatusChange(MapStatus status) {
				refreshScaleAndZoomControl();
			}

			public void onMapStatusChangeFinish(MapStatus status) {
			}

			public void onMapStatusChangeStart(MapStatus status) {
			}
		});
	}

	/**
	 * 更新缩放按钮的状态
	 */
	public void refreshScaleAndZoomControl() {
		mZoomControlView.refreshZoomButtonStatus(mBaiduMap.getMapStatus().zoom);
		mScaleView
				.refreshScaleView((int) Math.ceil(mBaiduMap.getMapStatus().zoom));
	}

	/**
	 * 用于自动定位
	 */
	private void autoLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mClient.setLocOption(option);
		mClient.start();
	}

	private class MyLocationListener implements BDLocationListener {

		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				ll = new LatLng(location.getLatitude(), location.getLongitude());
				city = location.getCity();
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_parking:
			if (ll == null) {
				Toast.makeText(this, "请等待定位成功", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(this, ParkingActivity.class);
				intent.putExtra("latitude", ll.latitude);
				intent.putExtra("longitude", ll.longitude);
				intent.putExtra("city", city);
				startActivity(intent);
			}
			break;
		case R.id.bt_navi:
			startActivity(new Intent(this, NaviActivity.class));
			break;
		}
	}

}
