package com.ctdj.djandroid.bean;

import java.util.List;

public class PushRecordBean extends BaseBean{

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
        private String title;
        private String content;
        private String createTime;
        private String sender;
        private String accounts;
        private int type;

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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getAccounts() {
            return accounts;
        }

        public void setAccounts(String accounts) {
            this.accounts = accounts;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
