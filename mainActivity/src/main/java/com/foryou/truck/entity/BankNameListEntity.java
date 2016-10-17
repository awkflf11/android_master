package com.foryou.truck.entity;

import java.util.List;


public class BankNameListEntity extends BaseEntity {

	public BankNameList data;

	public static class BankNameList {
		public List<BankNameItem> list;
		public String total;
	}

	public static class BankNameItem {
		public String id;
		public String name;

	}
}
