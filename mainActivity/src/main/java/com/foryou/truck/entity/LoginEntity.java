package com.foryou.truck.entity;

public class LoginEntity extends BaseEntity {

    public UserInfo data;
    public String flag;

    public static class UserInfo {
        public String uid;
        public String key;
    }
}
