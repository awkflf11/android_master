package com.foryou.truck.entity;

import java.util.List;

public class OrderListEntity extends BaseEntity {
	public Content data;

	public static class Content {
		public String total;
		public List<OrderContent> list;
	}
	
	public static class OrderContent{
		public String id;
		public String order_sn;
		public OrderStatus status;
		public String goods_name;
	//	public String goods_weight;
	//	public String goods_cubage;
//		public String start_place;
//		public String end_place;
		public String start_place_short ;
		public String end_place_short;
		public String agent_id;
		public String goods_loadtime;
		public String goods_loadtime_desc;
//		public String agent_name;
//		public String agent_mobile;
//		public String driver_name;
//		public String driver_mobile;
		public String quote_num;
		public String quote_min;

		public String biddingType;	// : 2,//集合竞价，
		public String expireTime;	// : 111111,//剩余秒数
	}
}
