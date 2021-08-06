package com.ctdj.djandroid.bean;

/**
 * @Author : Sun
 * @Time : 2021/7/27 15:29
 * @Description :
 */
public class MatchOrderBean extends BaseBean {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
//        "isdefier":1,                //类型：Number  必有字段  备注：是否挑战者 1是
//        "sta":0,                //类型：Number  必有字段  备注：状态 0新增 1进行中 2比赛审核中 3取消中 4申诉审核中 5比赛结束 6拒绝取消 99作废
//        "challengeId":14,                //类型：Number  必有字段  备注：订单id
//        "award":100,                //类型：Number  必有字段  备注：奖励
//        "orderno":"CT202107271734361001",                //类型：String  必有字段  备注：订单
//        "gameType":"王者荣耀",                //类型：String  必有字段  备注：游戏类型
//        "updateby":"mock",                //类型：String  必有字段  备注：修改人id
//        "challengeType":1,                //类型：Number  必有字段  备注：挑战类型
//        "area":"mock",                //类型：String  必有字段  备注：区服 1 微信 2 QQ 0 全区
//        "gameimg":"mock",                //类型：String  必有字段  备注：赛果截图
//        "appealimg":"mock",                //类型：String  必有字段  备注：申诉截图
//        "gameName":"mock",                //类型：String  必有字段  备注：挑战方游戏昵称
//        "fgameName":"mock"                //类型

        private int isdefier; // 是否挑战方
        private int sta; //类型：Number  必有字段  备注：0新增 1进行中 2比赛审核中 3取消中 4申诉审核中 5比赛结束 6拒绝取消 99作废
        private int challengeId;//类型：String  必有字段  备注：订单id
        private int award;//类型：Number  必有字段  备注：奖励
        private String orderno; //类型：Number  必有字段  备注：单号
        private String gameType; //类型：String  必有字段  备注：游戏类型"王者荣耀"
        private String gameName;//类型：String  必有字段  备注：挑战方游戏昵称
        private String fgameName;//类型：String  必有字段  备注：接受方游戏昵称
        private int challengeType; //类型：Number  必有字段  备注：比赛类型
        private String updateby;//类型：String  上传图片者的id
        private int area;
        private String gameimg; // 赛果截图
        private String appealimg; // 申诉截图
        public int getIsdefier() {
            return isdefier;
        }

        public void setIsdefier(int isdefier) {
            this.isdefier = isdefier;
        }

        public int getSta() {
            return sta;
        }

        public void setSta(int sta) {
            this.sta = sta;
        }

        public int getChallengeId() {
            return challengeId;
        }

        public void setChallengeId(int challengeId) {
            this.challengeId = challengeId;
        }

        public int getAward() {
            return award;
        }

        public void setAward(int award) {
            this.award = award;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public int getChallengeType() {
            return challengeType;
        }

        public void setChallengeType(int challengeType) {
            this.challengeType = challengeType;
        }

        public String getUpdateby() {
            return updateby;
        }

        public void setUpdateby(String updateby) {
            this.updateby = updateby;
        }

        public int getArea() {
            return area;
        }

        public void setArea(int area) {
            this.area = area;
        }

        public String getGameimg() {
            return gameimg;
        }

        public void setGameimg(String gameimg) {
            this.gameimg = gameimg;
        }

        public String getAppealimg() {
            return appealimg;
        }

        public void setAppealimg(String appealimg) {
            this.appealimg = appealimg;
        }

        public String getGameType() {
            return gameType;
        }

        public void setGameType(String gameType) {
            this.gameType = gameType;
        }

        public String getFgameName() {
            return fgameName;
        }

        public void setFgameName(String fgameName) {
            this.fgameName = fgameName;
        }
    }
}
