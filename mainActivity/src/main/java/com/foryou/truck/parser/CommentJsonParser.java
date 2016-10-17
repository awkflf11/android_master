package com.foryou.truck.parser;

import com.foryou.truck.entity.CommentEntity;

public class CommentJsonParser extends BaseJsonParser{
	public CommentEntity entity;
	@Override
	public int parser(String json) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			entity = gson.fromJson(json, CommentEntity.class);
			result = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
