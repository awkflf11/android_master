package com.foryou.truck.parser;

import com.foryou.truck.entity.CommonIndexEntity;

public class CommonIndexParser extends BaseJsonParser {
	public CommonIndexEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, CommonIndexEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
