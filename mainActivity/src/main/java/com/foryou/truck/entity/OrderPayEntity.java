package com.foryou.truck.entity;



public class OrderPayEntity extends BaseEntity{
	public OrderPayData data;
	public static class OrderPayData{
		public String order_sn;
		public String order_price;
		public String gift_price;//红包抵扣,如果没有红包抵扣,则为0
		public String insurance_price;//保险费用, 如果没有,则为0
		public String insurance_given_price;//赠送保险费用
		public String real_price;
		//public String payurl;
		//新增
		public String invoice_money;//税费
		public String start_place;
		public String end_place;

	}
}
