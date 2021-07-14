package com.ctdj.djandroid.bean;

import java.util.List;

public class ChallengeRecordBean extends BaseBean {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private List<Game> game;
        private Record record;

        public List<Game> getGame() {
            return game;
        }

        public void setGame(List<Game> game) {
            this.game = game;
        }

        public Record getRecord() {
            return record;
        }

        public void setRecord(Record record) {
            this.record = record;
        }

        public static class Record {
            private double bonus;
            private int num;
            private String win;

            public double getBonus() {
                return bonus;
            }

            public void setBonus(double bonus) {
                this.bonus = bonus;
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            public String getWin() {
                return win;
            }

            public void setWin(String win) {
                this.win = win;
            }
        }

        public static class Game {
            private String createTime;
            private String updateTime;
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
}
