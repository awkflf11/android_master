package com.foryou.truck.sendproduct;

import android.content.Context;

import com.foryou.truck.FirstTabInitValue;
import com.foryou.truck.entity.BusinessListEntity;

/**
 * Created by dubin on 16/7/19.
 */
public class SendProductContract {
    interface View{
        public void showCompanyStrategy(BusinessListEntity.BusinessData businessData);
        public void showProgressDialog();
        public void cancelProgressDialog();
        public Context getContext();
    }

    interface Present{
        public void init(FirstTabInitValue initValue);
    }
}
