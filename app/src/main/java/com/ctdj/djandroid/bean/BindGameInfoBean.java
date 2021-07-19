package com.ctdj.djandroid.bean;

import java.util.List;

/**
 * @ProjectName : 超团
 * @Author : Sun
 * @Time : 2021/7/7 19:11
 * @Description :
 */
public class BindGameInfoBean extends BaseBean {

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private String createTime;
        private int id;
        private String mid;
        private String wxName;
        private String qqName;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getWxName() {
            return wxName;
        }

        public void setWxName(String wxName) {
            this.wxName = wxName;
        }

        public String getQqName() {
            return qqName;
        }

        public void setQqName(String qqName) {
            this.qqName = qqName;
        }
    }
}
