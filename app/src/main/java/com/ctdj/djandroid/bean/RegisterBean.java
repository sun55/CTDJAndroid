package com.ctdj.djandroid.bean;

import java.io.Serializable;

public class RegisterBean implements Serializable {

    public String msg;
    public String code;
    public Data data;

    public static class Data implements Serializable {
        public int id;
        public String mname;
        public String mid;
        public String mno;
        public int sex;
        public int age;
        public String mobile;
        public int sta;
        public String cmid;
        public Object cmzname;
        public int balance;
        public String headimgs;
        public String address;
        public int secrecysta;
        public String token;
        public String ip;
        public Object devicecode;
        public int onlinesta;
    }
}
