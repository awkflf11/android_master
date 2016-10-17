package com.foryou.truck.entity;

import java.util.List;
//{
//	  "status": "Y",
//	  "code": 200,
//	  "msg": "OK",
//	  "data": {
//	    "start_place": "北京-东城区",
//	    "end_place": "北京-大兴区",
//	    "hd_start_place": "2,52",
//	    "hd_end_place": "2,52",
//	    "list": [
//	      {
//	        "id": "206105",
//	        "agent_id": "505",
//	        "name": "dd",
//	        "mobile": "18618360214",
//	        "credit_point": "100%",
//	        "photo": "http:\\/\\/thuo.fuyoukache.com\\/static\\/images\\/avatar.jpg",
//	        "price": "5555",
//	        "expire_time": "1436990676",
//	        "reopen": "0",
//	        "remark": "",
//	        "time": "1436861076",
//	        "order_m": "1",
//	        "order_a": "9",
//	        "comment": 0
//	      }
//	    ]
//	  }

public class AgentListEntity extends BaseEntity {
	public AgentList data;

	public String load_region;// "北京-东城区",
	public String load_address;// : "",
	public String unload_region;// ": "甘肃-兰州-皋兰县",
	public String unload_address;// ": "",
	public String pay_type;// ": "1", pay_type:2：货到付款 3:合同结算 5:在线支付
	public String timestamp;

	public String is_in_aging;//1，是否在竞价时效内
	public String reference_price;//:1000,//实时参考价,
	public String expire_time;// ： 123456,//失效时间戳

	public static class AgentList {
		public List<AgentInfo> list;
		public String total;
	}

	public static class AgentInfo {
		public String id;
		public String agent_id;
		public String quote_price;//报价金额,总的报价
		public String remark;
		public String expire_time;
		public String name;
		public String photo;
		public String mobile;
		public String credit_point;
		public String order_m;
		public String order_a;
		public String comment;
		public String coupon_amount; //“5” ; //可用代金券数量
		public String time;
		public String reopen; //0:激活报价 2:等待激活报价
		// 新增：
		public String insurance_money ;//保险金额
		public String invoice_money ;//税费
		public String order_money ;//运费
		public String gift_insurance ;//赠送保险


	}
}
