package com.ctdj.djandroid.bean;

import java.util.List;

public class UserListBean extends BaseBean {

    private int total;
    private List<Rows> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public static class Rows {
        private int id;
        private String mid;
        private String mname;
        private String headimg;
        private int sex;
        private String fmid;
        private String fmname;
        private String fheadimg;
        private int fsex;
        private String remark;
        private String remarkName;
        private int ftype;

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

        public String getMname() {
            return mname;
        }

        public void setMname(String mname) {
            this.mname = mname;
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

        public String getFmid() {
            return fmid;
        }

        public void setFmid(String fmid) {
            this.fmid = fmid;
        }

        public String getFmname() {
            return fmname;
        }

        public void setFmname(String fmname) {
            this.fmname = fmname;
        }

        public String getFheadimg() {
            return fheadimg;
        }

        public void setFheadimg(String fheadimg) {
            this.fheadimg = fheadimg;
        }

        public int getFsex() {
            return fsex;
        }

        public void setFsex(int fsex) {
            this.fsex = fsex;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }

        public int getFtype() {
            return ftype;
        }

        public void setFtype(int ftype) {
            this.ftype = ftype;
        }
    }
}
