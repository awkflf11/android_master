package com.foryou.truck.parser;

import com.foryou.truck.entity.OrderPay2Entity;

public class OrderPay2JsonParser extends BaseJsonParser {
	public OrderPay2Entity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, OrderPay2Entity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}