package com.foryou.truck.parser;

import com.foryou.truck.entity.BusinessListEntity;

/**
 * Created by dubin on 16/7/25.
 */
public class BusinessListJsonParser  extends BaseJsonParser{
    public BusinessListEntity entity;
    @Override
    public int parser(String json) {
        // TODO Auto-generated method stub
        int result = 0;
        try {
            entity = gson.fromJson(json, BusinessListEntity.class);
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
