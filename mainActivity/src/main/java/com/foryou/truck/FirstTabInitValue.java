package com.foryou.truck;

import java.io.Serializable;

public class FirstTabInitValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// public String order_type; //运单类型 1 为普通客户新增,2 为合同客户新增
	public String goods_load_region; // 省市区,逗号分割(如 3,2,1)
	public String goods_load_address;
	public String goods_unload_region;
	public String goods_unload_address;
	public String goods_loadtime;
	public String goods_loadtime_index;
	public String goods_loadtime_desc;
	public String goods_loadtime_desc_index;
	public String goods_unloadtime;
	public String goods_unloadtime_index;
	public String goods_unloadtime_desc;
	public String goods_unloadtime_desc_index;
	public String goods_name;
	public String goods_cubage;//此和 占用体积的值是一致的
	public String goods_weight;
	public String car_model;//
	public String car_model_text;
	public String car_length;
	public String car_length_text;
	// 2=>运费到付;3=>合同客户结算; 5=>在线支付 ;
	public String pay_type;//付款方式：合同结算，在线支付，运费到付
	// public String pay_type_text;
	public String receipt; // 是否开票
	public String need_back;
	public String car_type; // 运输类型
	public String occupy_length;
	public String remark;
	public String expect_price;

    public String mutiAndSingleTagText;
    public String mutiAndSingleTagKey;
    
    public String businessId;
	public String businessName;
	public String pay_type1_id;
	public String pay_type1_name;
	public String pay_type2_id;
	public String pay_type2_name;
}
