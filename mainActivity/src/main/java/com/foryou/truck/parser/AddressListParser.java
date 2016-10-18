package com.foryou.truck.parser;

import com.foryou.truck.entity.AddressListEntity;

public class AddressListParser extends BaseJsonParser{
	public AddressListEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, AddressListEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}