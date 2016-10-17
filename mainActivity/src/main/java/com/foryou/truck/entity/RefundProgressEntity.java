package com.foryou.truck.entity;

import java.util.List;

public class RefundProgressEntity extends BaseEntity {

	public List<RefundData> data;

	public static class RefundData {
		public String status;
		public String flag;
		public String date;
	}
}
