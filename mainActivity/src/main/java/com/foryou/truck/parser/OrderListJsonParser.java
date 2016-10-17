package com.foryou.truck.parser;

import com.foryou.truck.entity.OrderListEntity;

public class OrderListJsonParser extends BaseJsonParser {
	public OrderListEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, OrderListEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}