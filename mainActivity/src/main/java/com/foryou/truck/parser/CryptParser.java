package com.foryou.truck.parser;

import com.foryou.truck.entity.CryptEntity;

/**
 * Created by dubin on 16/5/25.
 */
public class CryptParser extends BaseJsonParser {
    public CryptEntity entity;

    @Override
    public int parser(String json) {
        // TODO Auto-generated method stub
        int result = 0;

        if (json.length() == 0) {
            return result;
        }
        try {
            entity = gson.fromJson(json, CryptEntity.class);
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

