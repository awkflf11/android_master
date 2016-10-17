package com.foryou.truck.parser;

import com.foryou.truck.entity.UserDetailEntity;

public class UserDetailJsonParser extends BaseJsonParser{
	public UserDetailEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, UserDetailEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

