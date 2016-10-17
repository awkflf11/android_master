package com.foryou.truck.parser;

import com.foryou.truck.entity.AvailSuranceEntity;

/**
 * Created by dubin on 16/1/6.
 */
public class AvailSuranceJasonParser extends BaseJsonParser {
    public AvailSuranceEntity entity;

    @Override
    public int parser(String json) {
        // TODO Auto-generated method stub
        int result = 0;
        try {
            entity = gson.fromJson(json, AvailSuranceEntity.class);
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
