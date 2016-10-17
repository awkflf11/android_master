package com.foryou.truck.parser;

import com.foryou.truck.entity.MyCouponListEntity;

public class MyCouponListParser extends BaseJsonParser{
	public MyCouponListEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, MyCouponListEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}