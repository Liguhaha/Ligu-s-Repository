package com.zy.iparking;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
import com.zy.adapter.ParkingAdapter;
import com.zy.model.ParkInfo;
import com.zy.utils.MyJSONUtils;

public class ParkingActivity extends Activity implements OnItemClickListener {

	private String city = null;
	private ListView lvParking = null;
	private ParkingAdapter parkingAdapter = null;
	private double latitude;
	private double longitude;
	private List<ParkInfo> parkInfos = null;
	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parking);
		lvParking = (ListView) findViewById(R.id.lv_parking);
		Intent intent = getIntent();
		latitude = intent.getDoubleExtra("latitude", 0);
		longitude = intent.getDoubleExtra("longitude", 0);
		city = intent.getStringExtra("city");
		getData();
		lvParking.setOnItemClickListener(this);
	}

	private void getData() {
		progressDialog = ProgressDialog.show(this, "请稍后", "正在请求数据...");
		Parameters params = new Parameters();
		params.add("kw", city);
		params.add("lat", latitude);
		params.add("lon", longitude);
		params.add("dtype", "json");
		JuheData.executeWithAPI(this, 133,
				"http://api2.juheapi.com/park/query", JuheData.GET, params,
				new DataCallBack() {
					public void onFailure(int statusCode,
							String responseString, Throwable throwable) {
						Toast.makeText(getApplicationContext(), "数据请求失败",
								Toast.LENGTH_SHORT).show();
					}

					public void onFinish() {
						progressDialog.dismiss();
					}

					public void onSuccess(int statusCode, String responseString) {
						if (statusCode == 200) {
							parkInfos = MyJSONUtils
									.jsonToParkList(responseString);
							parkingAdapter = new ParkingAdapter(
									ParkingActivity.this, parkInfos);
							lvParking.setAdapter(parkingAdapter);
						}
					}
				});
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, ParkMarkerActivity.class);
		intent.putExtra("latitude",
				parkInfos.get(position).getLatLng().latitude);
		intent.putExtra("longitude",
				parkInfos.get(position).getLatLng().longitude);
		startActivity(intent);
	}
}
