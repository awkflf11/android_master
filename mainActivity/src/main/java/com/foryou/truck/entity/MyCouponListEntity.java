package com.foryou.truck.entity;

import java.util.List;

//我的代金券
public class MyCouponListEntity extends BaseEntity {

	public List<MyCoupon> data;
	public String total;

 
	// data:[
	// {
	// ‘min_price_limit’ : ‘500’, //运单运费满500可用，如果为空则无该限制
	// ‘region_limit’:’北京,南京,江门’, //发货地限制，如果为空则无该限制
	// ‘code’ : ‘AJD138556’,
	// ‘value’ : ‘100’,
	// ‘start_day’ : ‘2015-05-20’,
	// ‘end_day’ : ‘2015-06-20 ’
	// },
	// ]
	public static class MyCoupon {
		public String min_price_limit;
		public String region_limit;//发货地限制，如果为空则无该限制
		public String code;
		public String value;//100
		public String start_day;
		public String end_day;

	}

}
