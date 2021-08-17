package com.ctdj.djandroid.bean;

import java.util.List;

public class SendMatchBean extends BaseBean{

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private int sta;
        private String headimg;
        private int sex;
        private String mobile;
        private String mid;
        private int id;
        private String mname;
        private String sbcode;

        public int getSta() {
            return sta;
        }

        public void setSta(int sta) {
            this.sta = sta;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMname() {
            return mname;
        }

        public void setMname(String mname) {
            this.mname = mname;
        }

        public String getSbcode() {
            return sbcode;
        }

        public void setSbcode(String sbcode) {
            this.sbcode = sbcode;
        }
    }
}
