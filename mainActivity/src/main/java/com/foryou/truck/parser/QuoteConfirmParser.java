package com.foryou.truck.parser;

import com.foryou.truck.entity.QuoteConfirmEntity;

/**
 * Created by dubin on 16/1/5.
 */
public class QuoteConfirmParser extends BaseJsonParser {
    public QuoteConfirmEntity entity;

    @Override
    public int parser(String json) {
        // TODO Auto-generated method stub
        int result = 0;
        try {
            entity = gson.fromJson(json, QuoteConfirmEntity.class);
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}