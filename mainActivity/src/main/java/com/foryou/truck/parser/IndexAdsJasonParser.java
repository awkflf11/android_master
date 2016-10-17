package com.foryou.truck.parser;

import com.foryou.truck.entity.IndexAdsEntity;

/**
 * Created by dubin on 16/3/2.
 */
public class IndexAdsJasonParser extends BaseJsonParser {
    public IndexAdsEntity entity;

    @Override
    public int parser(String json) {
        // TODO Auto-generated method stub
        int result = 0;
        try {
            entity = gson.fromJson(json, IndexAdsEntity.class);
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}