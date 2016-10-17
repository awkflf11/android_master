package com.foryou.truck.entity;

public class UserDetailEntity extends BaseEntity {
	public UserDetail data;

	public static class UserDetail {
		public String name;
		public String mobile;
		public String portrait;// 肖像路径
		public String gender;
		public String type; // 0:普通客户 1:合同客户
		public String isOnlyContract;// 是否只支持合同结算(int) 1表示是（仅显示合同结算）,0表示否
		// public AccountType account_type; //账户类型：1.货主 2.第三方物流 3配货站
		public String give_insurance;
		//public String vip;
		public String code;
		// public static class AccountType{
		// public String key;
		// public String text;
		// }

		//新增的字段
		public String uid;//用户ID
		public String check_status; //审核状态:0=未审核,1=已审核,-1=拒绝
		public String is_quote;//是否可以询价:0=不可以,1=可以
		public String is_fuyou;//1=>是福佑公司 2=>其他公司



	}
}
