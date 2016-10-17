package com.foryou.truck.parser;

import com.foryou.truck.entity.UserContractEntity;

public class GetUserContractJsonParser extends BaseJsonParser {
	public UserContractEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, UserContractEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
