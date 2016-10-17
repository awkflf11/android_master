package com.foryou.truck.entity;

import java.util.List;

public class AreasEntity extends BaseEntity{
	public List<Province> data;
	public static class Province{
		public String id;
		public String name;
		public List<City> city;
	}
	public static class City{
		public String id;
		public String name;
		public List<District> district;
	}
	
	public static class District{
		public String id;
		public String name;
	}
}
