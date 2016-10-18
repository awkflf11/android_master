package com.foryou.truck.parser;

import com.foryou.truck.entity.CouponEntity;

public class CouponParser extends BaseJsonParser {
	public CouponEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, CouponEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

