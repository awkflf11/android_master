package com.foryou.truck.entity;

public class AgreeMentEntity extends BaseEntity {
	public AgreeMent data;

	public static class AgreeMent {
		public String tplType;//模板类型，1表示普通货主模板，2表示盛丰物流模板
		public String agentName;//经纪人名称
		//
		public String customerName;
		public String driverName;
		public String driverIdCard;
		public String driverPlate;
		public String driverMobile;
		public String confirmTime;
		public String arrangeTime;
	}
}
