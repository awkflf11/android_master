package com.foryou.truck.parser;

import com.foryou.truck.entity.AgentListEntity;

public class AgentListJsonParser extends BaseJsonParser {
	public AgentListEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, AgentListEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}

