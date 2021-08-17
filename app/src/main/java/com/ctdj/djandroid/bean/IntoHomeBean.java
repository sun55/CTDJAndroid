package com.ctdj.djandroid.bean;

import java.util.List;

public class IntoHomeBean extends BaseBean{

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private int gold;
        private double star;
        private double balance;
        private List<?> weekCompetition;
        private List<AdvList> advList;
        private List<GoldChallenge> goldChallenge;
        private List<BalanceChallenge> balanceChallenge;

        public int getGold() {
            return gold;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }

        public double getStar() {
            return star;
        }

        public void setStar(double star) {
            this.star = star;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public List<?> getWeekCompetition() {
            return weekCompetition;
        }

        public void setWeekCompetition(List<?> weekCompetition) {
            this.weekCompetition = weekCompetition;
        }

        public List<AdvList> getAdvList() {
            return advList;
        }

        public void setAdvList(List<AdvList> advList) {
            this.advList = advList;
        }

        public List<GoldChallenge> getGoldChallenge() {
            return goldChallenge;
        }

        public void setGoldChallenge(List<GoldChallenge> goldChallenge) {
            this.goldChallenge = goldChallenge;
        }

        public List<BalanceChallenge> getBalanceChallenge() {
            return balanceChallenge;
        }

        public void setBalanceChallenge(List<BalanceChallenge> balanceChallenge) {
            this.balanceChallenge = balanceChallenge;
        }

        public static class AdvList {
            private int id;
            private String url;
            private int type;
            private int sta;
            private String img;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getSta() {
                return sta;
            }

            public void setSta(int sta) {
                this.sta = sta;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }
        }

        public static class GoldChallenge {
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

        public static class BalanceChallenge {
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
}
