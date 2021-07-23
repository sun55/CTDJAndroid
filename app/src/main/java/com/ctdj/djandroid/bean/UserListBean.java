package com.ctdj.djandroid.bean;

import java.util.List;

public class UserListBean extends BaseBean {


    private int total;
    private List<Row> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public class Row {
        private int id;
        private String mid;
        private String fmid;
        private String fmname;
        private String fheadimg;
        private int fsex;
        private String remark;
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

        public int getFtype() {
            return ftype;
        }

        public void setFtype(int ftype) {
            this.ftype = ftype;
        }

        @Override
        public String toString() {
            return "Row{" +
                    "id=" + id +
                    ", mid='" + mid + '\'' +
                    ", fmid='" + fmid + '\'' +
                    ", fmname='" + fmname + '\'' +
                    ", fheadimg='" + fheadimg + '\'' +
                    ", fsex=" + fsex +
                    ", remark='" + remark + '\'' +
                    ", ftype=" + ftype +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserListBean{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
