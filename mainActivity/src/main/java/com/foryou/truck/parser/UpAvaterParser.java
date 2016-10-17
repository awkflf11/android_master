package com.foryou.truck.parser;

import com.foryou.truck.entity.UpAvaterEntity;

/**
 * @des:完善资料
 * @by:wkf
 */
public class UpAvaterParser extends BaseJsonParser {
	public UpAvaterEntity entity;

	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		if (json.length() == 0) {
			return result;
		}
		try {
			entity = gson.fromJson(json, UpAvaterEntity.class);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}