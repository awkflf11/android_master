package com.foryou.truck.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dubin on 16/7/25.
 */
public class BusinessListEntity extends BaseEntity{

    public BusinessData data;

    public static class BusinessData{
        public String total;
        public String is_fuyou;//1=》福佑  2＝》其他公司
        public List<BusinessItem> list;
    }

    public static class BusinessItem implements Serializable{
        public String id;//业务id
        public String name;//‘北京－上海干线’，//业务名称
        public String invoiceType;//开票类型 , 1:自行选择,2:强制开票,3:强制不开票
        public List<PayType> paidType;
    }

    public static class PayType{
        public String id; //付款方式   2:运费到付,3:合同结算
        public String name;//,//付款方式名称
    }
}
