package com.ctdj.djandroid.bean;

public class MessageSetBean extends BaseBean {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private boolean isFollow;
        private String headimg;
        private boolean isBlack;
        private String blackid;
        private int isFriend;
        private String remarkName;
        private String mname;

        public boolean isIsFollow() {
            return isFollow;
        }

        public void setIsFollow(boolean isFollow) {
            this.isFollow = isFollow;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public boolean isIsBlack() {
            return isBlack;
        }

        public void setIsBlack(boolean isBlack) {
            this.isBlack = isBlack;
        }

        public String getBlackid() {
            return blackid;
        }

        public void setBlackid(String blackid) {
            this.blackid = blackid;
        }

        public int getIsFriend() {
            return isFriend;
        }

        public void setIsFriend(int isFriend) {
            this.isFriend = isFriend;
        }

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }

        public String getMname() {
            return mname;
        }

        public void setMname(String mname) {
            this.mname = mname;
        }
    }
}
