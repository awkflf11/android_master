package com.foryou.truck.parser;

import com.foryou.truck.entity.CarLoadEntity;

public class CarLoadParser extends BaseJsonParser{
	public CarLoadEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, CarLoadEntity.class);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}