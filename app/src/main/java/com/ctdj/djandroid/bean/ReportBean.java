package com.ctdj.djandroid.bean;

import java.util.List;

public class ReportBean extends BaseBean{

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private int id;
        private String typeName;
        private String typeNo;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeNo() {
            return typeNo;
        }

        public void setTypeNo(String typeNo) {
            this.typeNo = typeNo;
        }
    }
}
