package com.foryou.truck.entity;

//{
//"status": "Y",
//"code": 200,
//"msg": "OK",
//"data": {
//"order_sn": "202395",
//"confirm_time": "1436006496",
//"start_place": "北京-崇文区",
//"end_place": "澳门-澳门-澳门",
//"goods_name": "123",
//"agent_name": "asd",
//"c2a_id": "9",评论 id,客户评论经纪人,首次评论为空 
//"c2a_level": "4",评论得分,1-5
//"c2a_comment": "独爱的姑娘开始独爱的姑 llll", 
//"c2d_id": ‘’, 评论 id,客户评论司机,首次评论为空 
//"c2d_level": 0,
//"c2d_comment": "",
//"a2c_id": ‘’, 评论 id,经纪人评论客户,首次评论为空 
//"a2c_level": 0,
//"a2c_comment": "",
//"a2c_time": "16632 天前" }}
public class CommentEntity extends BaseEntity{
	public ComtentData data;
	public static class ComtentData{
		public String order_sn;
		public String confirm_time;
//		public String start_place;
//		public String end_place;
		public String start_place_short ;
		public String end_place_short ;
		public String goods_name;
		public String agent_name;
		public String c2a_id;
		public String c2a_level;
		public String c2a_comment;
		public String c2d_id;
		public String c2d_level;
		public String c2d_comment;
		public String a2c_id;
		public String a2c_level;
		public String a2c_comment;
		public String a2c_time;
	}
}
