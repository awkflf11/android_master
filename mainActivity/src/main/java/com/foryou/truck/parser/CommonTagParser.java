package com.foryou.truck.parser;

import com.foryou.truck.entity.CommonTagEntity;

public class CommonTagParser extends BaseJsonParser {
	public CommonTagEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, CommonTagEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
