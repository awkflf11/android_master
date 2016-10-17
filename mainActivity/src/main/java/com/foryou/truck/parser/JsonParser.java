package com.foryou.truck.parser;

import com.google.gson.Gson;




public interface JsonParser {
	/**
	 * 序列号json数据类
	 */
	Gson gson = new Gson();
	int parser(String json);
	
}
