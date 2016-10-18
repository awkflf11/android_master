package com.foryou.truck.entity;

import java.util.List;

//常用的标签
public class CommonTagEntity extends BaseEntity {
	// 一装一卸，一装两卸，两装一卸，两装两卸；
	public CommomTag data;

	public static class CommomTag {
		public List<BaseKeyValue> tag;// 标签
		public List<BaseKeyValue> load_type;

	}

	public static class BaseKeyValue {
		public String key;
		public String value;
	}
	
 
}
