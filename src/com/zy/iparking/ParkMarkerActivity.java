package com.zy.iparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.zy.tools.ScaleView;
import com.zy.tools.ZoomControlView;

public class ParkMarkerActivity extends Activity {

	private double lat;
	private double lon;
	private BitmapDescriptor bd = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding);
	private MapView mMapView = null;
	private LinearLayout llMapView = null;
	private BaiduMap mBaiduMap = null;
	private ZoomControlView mZoomControlView = null;
	private ScaleView mScaleView = null;
	private int width, height;
	private LatLng targetLatLng = null;
	private InfoWindow mInfoWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_park_marker);
		Intent intent = getIntent();
		lat = intent.getDoubleExtra("latitude", 0);
		lon = intent.getDoubleExtra("longitude", 0);
		targetLatLng = toBDLatlng(new LatLng(lat, lon));
		initMap();

	}

	private void initMap() {
		llMapView = (LinearLayout) findViewById(R.id.ll_map_view);
		BaiduMapOptions mapOptions = new BaiduMapOptions();
		mapOptions.scaleControlEnabled(false);
		// 初始化地图状态
		MapStatus mapStatus = new MapStatus.Builder().zoom(12.0f)
				.target(targetLatLng).build();
		mapOptions.zoomControlsEnabled(false).scaleControlEnabled(false)
				.mapStatus(mapStatus);
		mMapView = new MapView(this, mapOptions);
		llMapView.addView(mMapView);

		mScaleView = (ScaleView) findViewById(R.id.scaleview);
		mZoomControlView = (ZoomControlView) findViewById(R.id.zoomcontrolview);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.popup);
				button.setText("到这里去");
				button.setTextColor(Color.BLACK);
				OnInfoWindowClickListener listener = null;
				listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						Toast.makeText(ParkMarkerActivity.this, "aaa", 0).show();
					}
				};
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(button), targetLatLng, -47, listener);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return false;
			}
		});

		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		ScaleView.setMapView(mMapView, width, height);
		mZoomControlView.setMapView(mMapView);
		initMarker();

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

	private void initMarker() {
		OverlayOptions overlayOptions = new MarkerOptions()
				.position(targetLatLng).icon(bd).zIndex(5);
		mBaiduMap.addOverlay(overlayOptions);
	}

	/**
	 * 将其它坐标转换为百度坐标
	 * 
	 * @param sourceLatLng
	 * @return
	 */
	private LatLng toBDLatlng(LatLng sourceLatLng) {
		// 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.COMMON);
		// sourceLatLng待转换坐标
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		return desLatLng;

	}

	/**
	 * 更新缩放按钮的状态
	 */
	public void refreshScaleAndZoomControl() {
		mZoomControlView.refreshZoomButtonStatus(mBaiduMap.getMapStatus().zoom);
		mScaleView
				.refreshScaleView((int) Math.ceil(mBaiduMap.getMapStatus().zoom));
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		// 回收 bitmap 资源
		bd.recycle();
	}
}
