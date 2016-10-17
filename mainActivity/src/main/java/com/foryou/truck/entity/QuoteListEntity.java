package com.foryou.truck.entity;

import java.util.List;

public class QuoteListEntity extends BaseEntity {
	public QuoteList data;

	public static class QuoteList {
		public List<QuoteContent> list;
	}

	public static class QuoteContent {
		public String id;
		public String order_sn;
		public OrderStatus status;
		public String goods_name;
		public String goods_weight;
		public String goods_cubage;
		public String start_place;
		public String end_place;
		public String goods_loadtime;
		public String goods_loadtime_desc;
		public String car_model;
		public String car_length;
		public String pay_type;
		public String receipt;
		public String quote_num;
		public String quote_min;
	}
}
