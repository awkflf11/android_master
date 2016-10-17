package com.foryou.truck.entity;



public class OrderPay2Entity extends BaseEntity{
	public OrderPayMethod data;
	public static class OrderPayMethod{
		public String payurl;
	}
}
