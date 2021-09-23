package com.ctdj.djandroid.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BoxBean extends BaseBean{


    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private String createTime;
        private String updateTime;
        private int id;
        private String title;
        @SerializedName("code")
        private int codeX;
        private int sta;
        private int type;
        private double val;
        private String day;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCodeX() {
            return codeX;
        }

        public void setCodeX(int codeX) {
            this.codeX = codeX;
        }

        public int getSta() {
            return sta;
        }

        public void setSta(int sta) {
            this.sta = sta;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public double getVal() {
            return val;
        }

        public void setVal(double val) {
            this.val = val;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }
}
