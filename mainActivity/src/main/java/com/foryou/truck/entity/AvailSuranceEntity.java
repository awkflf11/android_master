package com.foryou.truck.entity;

/**
 * Created by dubin on 16/1/6.
 */
public class AvailSuranceEntity extends BaseEntity {
    public String avaiInsurance;  ////0-不允许在线购买    1-允许在线购买
    public String invoice_name;//发票抬头
    public String receipt ; // 是否需要发票 0-不需要 1-需要
    //public String is_wuyou ; // 是否是无忧宝，0无，1有

    public String insuranceType;      //1 ,//“保险类型” 1:自行购买 , 2:强制购买

    public String insuranceTypeSelect; //保险收费类型：1：保险货物价值比，2：保险运费比例，3：固定金额',
    public String insuranceGoods;     //”保险货价比”,例如：0.6
    public String insuranceTranport; //“保险运费比”,
    public String insuranceMoney;    //”保险金额”,
}
