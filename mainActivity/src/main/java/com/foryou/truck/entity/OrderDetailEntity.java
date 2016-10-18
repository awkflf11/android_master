package com.foryou.truck.entity;

import java.util.List;

//{
//	  "status": "Y",
//	  "code": 200,
//	  "msg": "OK",
//	  "data": {
//	    "order_sn": "205271",
//	    "insurance_image": "",
//	    "status": {
//	      "key": "9",
//	      "text": "报价收集中"
//	    },
//	    "agent": {
//	      "agent_name": "",
//	      "agent_mobile": "",
//	      "driver_name": "",
//	      "driver_mobile": "",
//	      "driver_plate": ""
//	    },
//	    "goods": {
//	      "goods_name": "444",
//	      "goods_weight": "222",
//	      "goods_cubage": "222",
//	      "pay_type": {
//	        "key": "2",
//	        "text": "现金支付（收货方付）"
//	      },
//	      "goods_loadtime": "1435852800",
//	      "goods_loadtime_desc": "08:00-08:30",
//	      "goods_unloadtime": "1435939200",
//	      "goods_unloadtime_desc": "",
//	      "order_price": "0",
//	      "receipt": "不需要",
//	      "car_model": "高栏车",
//	      "car_length": "13.5",
//	      "need_back": "0",
//	      "car_type": "2",
//	      "expect_price": "11",
//	      "pay_online_success": "0",
//	      "coupon_code": null,
//	      "coupon_value": null
//	    },
//	    "sender": {
//	      "name": "",
//	      "tel": "",
//	      "tel2": "",
//	      "start_place": "北京-东城区",
//	      "start_address": ""
//	    },
//	    "receiver": {
//	      "name": "",
//	      "tel": "",
//	      "tel2": "",
//	      "end_place": "北京-大兴区",
//	      "end_address": ""
//	    },
//	    "location": []
//	  }

public class OrderDetailEntity extends BaseEntity {
	
	public OrderDetail data;

	public static class OrderDetail {
		public String order_sn;
		//public String insurance_image;
		//public String insurance;
		public OrderStatus status;

		public String invoice_name;//’北京福佑卡车’ // 发票抬头
		public String money_tax; // 税费
		public String money_chanage_agent; // 更换经纪人费用


		public String goods_name;// ‘电子配件’ //货物名称
		public String goods_weight;// : 100 //货物重量
		public String goods_cubage;//;   //: 1 //货物体积
		public String goods_n;// : 10 //货物件数
		public String goods_loadtime;// : ‘2015­09­12’ //发货日期 goods_loadtime_desc : ‘08:00­12:00’ //具体发货时间 goods_arrivetime : ‘2015­10­12 21:09’ //送达日期 order_price : 100 //运单金额
		public String goods_loadtime_desc;
		public String goods_unloadtime;
		public String goods_unloadtime_desc;
		public String order_price;
		public String car_model;// : ‘皮卡’ //车型
		public String car_length;// : ‘12.5’ //车长
		public String car_type;// : ‘整车’ //用车类型
		public String pay_type;// : ‘在线支付’ //支付方式
		public String receipt;// : ‘需要’ //是否需要发票
		public String expect_price;// : 1000 //期望价格
		public String need_back; //: ‘是’ //是否需要回单 occupy_length : ‘17.5’ //预计占用车长 is_payed : 1 //已支付 0=
		public String occupy_length;
		public String is_payed;
		public String is_wuyou;//无忧保
		public String wuyou_pdf;
		public String remark;
		public Msg msg;
		
		public Agent agent;
		public Driver driver;
		public ButtonView button;
		public Coupon coupon;
		public Sender sender;
		public Receiver receiver;
		public List<LocationNew> location;
		public Insurance insurance;
		
		public String common_tag;
		public String coupon_amount;
	}
	
	public static class Msg{
		public String pay;
	}
	
	public static class Coupon{
		public String flag;
		public String code;
		public String value;
	}
	
	public static class ButtonView{
		public String pay;
		public String insurance;
		public String refund;
		public String pay_driver;
	}
	
	public static class Insurance{			
		public String flag;//是否有保险, 1=>是,0=>否
		public String status;//0-支付失效 1-支付未失效
		public String value;
		public String name;
		public String num;
		public String package_type;
		public String image;
		public String insurance_type;
		public String insurance_price;
		public String give_money;
	}
	
	public static class Agent{
		public String name;
		public String mobile;
	}
	
	public static class Driver{
		public String name;
		public String mobile;
		public String plate;
		public String locate;
	}
	
	public static class PayType{
		public String key;
		public String text;
	}

	public static class Sender {
		public String name;
		public String tel;
		public String tel2;
		public String address;
	}

	public static class Receiver {
		public String name;
		public String tel;
		public String tel2;
		public String address;
	}

	public static class LocationNew{
		public String lng;
		public String lat;
		public String location;
		public String time;
	}
}
