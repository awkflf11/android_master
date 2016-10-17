package com.foryou.truck.parser;

import com.foryou.truck.entity.CommonConfigEntity;

public class CommonConfigJsonParser extends BaseJsonParser {
	public CommonConfigEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, CommonConfigEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
