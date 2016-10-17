package com.foryou.truck.entity;

import java.util.List;


//{
//"status":"Y",
//code:
//msg:
//￼
//成功返回
//"data":{
//"hd_start_place":"3,3401,0", "hd_end_place":"8,112,0", "goods_loadtime":"2015-06-15", 
//"goods_loadtime_desc":"10:00-11:30", "goods_unloadtime":"2015-06-15",
//"goods_unloadtime_desc":"16:00-17:30", "goods_name":"娃哈哈", "goods_weight":"33", "goods_cubage":"0",
//"car_type":"2",
//"occupy_length":"0",
//"car_model":"0",
//"car_length":"0",
//"remark":"少时诵诗书 "expect_price":"1000",
//"pay_type":"2",
//"receipt":"1",
//"need_back":"0"
//}

public class ReOrderEntity extends BaseEntity{
	public ReOrderData data;
	public static class ReOrderData{
		public String goods_load_region;
		public String goods_load_address;
		public String goods_unload_region;
		public String goods_unload_address;
		public String goods_loadtime;
		public String goods_loadtime_desc;
		public String goods_unloadtime;
		public String goods_unloadtime_desc;
		public String goods_name;
		public String goods_weight;
		public String goods_cubage;
		public String car_type;
		public String occupy_length;
//		public String car_model;
//		public String car_length;
		public String remark;
		public String expect_price;
		public String pay_type;
		public String receipt;
		public String need_back;
		//
		public BaseValueKey car_model;
		public BaseValueKey car_length ;
		
		public List<BaseValueKey> common_tag;//标签

		public String businessId;
		public String businessName;
		public String invoiceType;
		public List<BusinessListEntity.PayType> paidType;
	}
	public static class BaseValueKey{
		public String key;
		public String value;
	}
	
}
