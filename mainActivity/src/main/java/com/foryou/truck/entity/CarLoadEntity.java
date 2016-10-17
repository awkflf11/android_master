package com.foryou.truck.entity;

import java.util.List;

public class CarLoadEntity extends BaseEntity {
	public List<CarLoad> data;

	public static class CarLoad {
		public String key;
		public String value;
		public List<LoadInfo> load_info;
	}

	public static class LoadInfo {
		public String key;
		public String value;
		public String carload;
	}

}
