package com.foryou.truck.parser;


import com.foryou.truck.entity.BankNameListEntity;

public class BankNameListJsonParser extends BaseJsonParser{
	public BankNameListEntity entity;
	@Override
	public int parser(String json) {
		int result = 0;
		try {
			entity = gson.fromJson(json, BankNameListEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}