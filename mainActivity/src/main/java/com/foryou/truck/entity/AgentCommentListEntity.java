package com.foryou.truck.entity;

import java.util.List;

public class AgentCommentListEntity extends BaseEntity{
//	{
//		status : Y,
//		msg: data: {
//		total : 总数量 
//	list : [
//		￼
//		{
//		start_place 货物始发地 end_place 货物目的地 goods_name 货物品名
//		confirm_time 下单日期
//		order_sn
//		c2a_time
//		c2a_level
//		c2a_comment 评价内容
//		} ]
//		} }
	public AgentCommentData data;
	public static class AgentCommentData{
		public String total;
		public List<AgentComment> list;
	}
	public static class AgentComment{
		public String start_place;
		public String end_place;
		public String confirm_time;
		public String order_sn;
		public String c2a_time;
		public String c2a_level;
		public String c2a_comment;
	}
}
