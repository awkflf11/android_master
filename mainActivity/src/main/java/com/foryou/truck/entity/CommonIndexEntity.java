package com.foryou.truck.entity;

import java.util.List;

public class CommonIndexEntity extends BaseEntity{
	
	public CommonIndex data;
	public static class CommonIndex{
		public String agentCnt;
		public List<Banner> banner;
	}
	
	public static class Banner{
		public String imgUrl;
		public String href;
	}
}
