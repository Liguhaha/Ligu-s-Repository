package com.zy.model;

import com.baidu.mapapi.model.LatLng;

public class ParkInfo {
	private int id;// ID
	private String name;// 名称
	private String address;// 地址
	private LatLng latLng;// 坐标
	private String city;// 所在城市
	private int emptyPark;// 空车位
	private int totalPark;// 总车位

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getEmptyPark() {
		return emptyPark;
	}

	public void setEmptyPark(int emptyPark) {
		this.emptyPark = emptyPark;
	}

	public int getTotalPark() {
		return totalPark;
	}

	public void setTotalPark(int totalPark) {
		this.totalPark = totalPark;
	}

}
