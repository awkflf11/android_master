package com.foryou.truck.entity;

import java.util.List;

public class AddressListEntity extends BaseEntity {

	public List<AddressData> data;

	public static class AddressData {
		public String id;
		public String region;
		public String address;
	}
}
