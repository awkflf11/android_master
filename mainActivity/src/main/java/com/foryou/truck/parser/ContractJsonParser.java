package com.foryou.truck.parser;

import com.foryou.truck.entity.ContractEntity;

public class ContractJsonParser extends BaseJsonParser {
	public ContractEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, ContractEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
