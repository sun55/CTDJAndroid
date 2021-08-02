package com.ctdj.djandroid.bean;

public class OrderDetailBean extends BaseBean{

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private int area;
        private int sta;
        private String gameType;
        private String fheadimg;
        private String orderno;
        private String headimg;
        private String gameimg;
        private String mid;
        private int challengeType;
        private String mname;
        private String fgameName;
        private int isdefier;
        private int challengeId;
        private String fmid;
        private String fmname;
        private int award;
        private String createTime;
        private String updateBy;
        private String appealimg;
        private int winType;
        private String remarks;

        public int getArea() {
            return area;
        }

        public void setArea(int area) {
            this.area = area;
        }

        public int getSta() {
            return sta;
        }

        public void setSta(int sta) {
            this.sta = sta;
        }

        public String getGameType() {
            return gameType;
        }

        public void setGameType(String gameType) {
            this.gameType = gameType;
        }

        public String getFheadimg() {
            return fheadimg;
        }

        public void setFheadimg(String fheadimg) {
            this.fheadimg = fheadimg;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public String getGameimg() {
            return gameimg;
        }

        public void setGameimg(String gameimg) {
            this.gameimg = gameimg;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public int getChallengeType() {
            return challengeType;
        }

        public void setChallengeType(int challengeType) {
            this.challengeType = challengeType;
        }

        public String getMname() {
            return mname;
        }

        public void setMname(String mname) {
            this.mname = mname;
        }

        public String getFgameName() {
            return fgameName;
        }

        public void setFgameName(String fgameName) {
            this.fgameName = fgameName;
        }

        public int getIsdefier() {
            return isdefier;
        }

        public void setIsdefier(int isdefier) {
            this.isdefier = isdefier;
        }

        public int getChallengeId() {
            return challengeId;
        }

        public void setChallengeId(int challengeId) {
            this.challengeId = challengeId;
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

        public int getAward() {
            return award;
        }

        public void setAward(int award) {
            this.award = award;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getAppealimg() {
            return appealimg;
        }

        public void setAppealimg(String appealimg) {
            this.appealimg = appealimg;
        }

        public int getWinType() {
            return winType;
        }

        public void setWinType(int winType) {
            this.winType = winType;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
