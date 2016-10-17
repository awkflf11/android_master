package com.foryou.truck.entity;


public class OrderPayRefundEntity extends BaseEntity {
    public OrderPayRefundData data;
//    order_sn: ‘202323’,  //订单号
//    order_price:100,  //运单金额
//    payment_type: ‘支付宝‘   //支付方式
//    gift_price:20,  //红包抵扣,如果没有红包抵扣,则为0
//    insurance_price:30,   //保险费用, 如果没有,则为0
//    insurance_given_price:20, //赠送保险费用
//    real_price:50,      //实际支付金额
//    refund_price:30     //可以退款金额
//    invoice_money:300/税费
//    start_place： ‘北京海淀’  , //始发地
//    end_place： ‘安徽安庆’  , //目的地

    public static class OrderPayRefundData {
        public String order_sn;
        public String order_price;//运单金额
        public String payment_type;
        public String gift_price;//红包抵扣(即是 代金券)
        public String insurance_price;//保险费用
        public String insurance_given_price;// 赠送保险费用
        public String refund_price; //可以退款金额
        public String real_price;//实际支付金额
        //public String payurl;
        //新增
        public String invoice_money; //税费
        public String start_place; //
        public String end_place; //



    }
}
