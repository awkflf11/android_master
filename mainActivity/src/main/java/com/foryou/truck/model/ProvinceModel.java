package com.foryou.truck.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ProvinceModel{
	private String name;
	private String id;
	private List<CityModel> cityList;
	
	public ProvinceModel() {
		super();
	}
	
	public ProvinceModel(String name, List<CityModel> cityList) {
		super();
		this.name = name;
		this.cityList = cityList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId(){
		return id;
	}

	public List<CityModel> getCityList() {
		return cityList;
	}

	public void setCityList(List<CityModel> cityList) {
		this.cityList = cityList;
	}

	@Override
	public String toString() {
		return "ProvinceModel [name=" + name + ", cityList=" + cityList + "]";
	}
}
