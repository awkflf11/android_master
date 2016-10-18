package com.foryou.truck.parser;

import com.foryou.truck.entity.RefundProgressEntity;

public class RefundProgressParser extends BaseJsonParser{
	public RefundProgressEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, RefundProgressEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
