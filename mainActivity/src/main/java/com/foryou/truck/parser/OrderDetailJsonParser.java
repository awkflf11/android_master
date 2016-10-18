package com.foryou.truck.parser;

import com.foryou.truck.entity.OrderDetailEntity;
import com.google.gson.JsonParseException;

public class OrderDetailJsonParser extends BaseJsonParser {
	public OrderDetailEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, OrderDetailEntity.class);
			result = 1;

		} catch (JsonParseException jsonEx) {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}