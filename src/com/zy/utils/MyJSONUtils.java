package com.zy.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.zy.model.ParkInfo;

public class MyJSONUtils {

	public static List<ParkInfo> jsonToParkList(String content) {
		JSONObject jsonObject = JSONObject.fromObject(content);
		JSONArray array = jsonObject.getJSONArray("result");
		List<ParkInfo> parkInfos = new ArrayList<ParkInfo>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject parkObj = array.getJSONObject(i);
			Iterator<?> it = parkObj.keys();
			ParkInfo parkInfo = new ParkInfo();
			while (it.hasNext()) {
				String key = it.next().toString();
				if (key.equals("CCID")) {
					parkInfo.setId(parkObj.getInt("CCID"));
					continue;
				}
				if (key.equals("CCMC")) {
					parkInfo.setName(parkObj.getString("CCMC"));
					continue;
				}
				if (key.equals("CCDZ")) {
					parkInfo.setAddress(parkObj.getString("CCDZ"));
					continue;
				}
				if (key.equals("QYCS")) {
					parkInfo.setCity(parkObj.getString("QYCS"));
					continue;
				}
				if (key.equals("CKW")) {
					parkInfo.setEmptyPark(parkObj.getInt("CKW"));
					continue;
				}
				if (key.equals("ZCW")) {
					parkInfo.setTotalPark(parkObj.getInt("ZCW"));
					continue;
				}
				if (key.equals("LOC")) {
					JSONObject loc = parkObj.getJSONObject("LOC");
					double lat = loc.getDouble("lat");
					double lon = loc.getDouble("lon");
					LatLng latLng = new LatLng(lat, lon);
					parkInfo.setLatLng(latLng);
					continue;
				}
			}
			parkInfos.add(parkInfo);
		}
		return parkInfos;
	}
}
