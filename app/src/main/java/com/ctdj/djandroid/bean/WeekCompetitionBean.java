package com.ctdj.djandroid.bean;

import java.io.Serializable;

public class WeekCompetitionBean implements Serializable {

    private String msg;
    private int code;
    private Data data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private LogEntryCompetition logEntryCompetition;
        private WeekCompetition weekCompetition;

        public LogEntryCompetition getLogEntryCompetition() {
            return logEntryCompetition;
        }

        public void setLogEntryCompetition(LogEntryCompetition logEntryCompetition) {
            this.logEntryCompetition = logEntryCompetition;
        }

        public WeekCompetition getWeekCompetition() {
            return weekCompetition;
        }

        public void setWeekCompetition(WeekCompetition weekCompetition) {
            this.weekCompetition = weekCompetition;
        }

        public static class LogEntryCompetition {
            private String mid;
            private int competitionId;
            private int rank;
            private int score;

            public String getMid() {
                return mid;
            }

            public void setMid(String mid) {
                this.mid = mid;
            }

            public int getCompetitionId() {
                return competitionId;
            }

            public void setCompetitionId(int competitionId) {
                this.competitionId = competitionId;
            }

            public int getRank() {
                return rank;
            }

            public void setRank(int rank) {
                this.rank = rank;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }
        }

        public static class WeekCompetition {
            private int id;
            private String title;
            private String beginTime;
            private String endTime;
            private int isEnabled;
            private String createTime;
            private String updateTime;
            private int no1;
            private int no2;
            private int no3;
            private int no4;
            private int no5;
            private int no6to10;
            private int no11to20;
            private int no21to50;
            private int no51to80;
            private int no81to100;

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

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public int getIsEnabled() {
                return isEnabled;
            }

            public void setIsEnabled(int isEnabled) {
                this.isEnabled = isEnabled;
            }

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

            public int getNo1() {
                return no1;
            }

            public void setNo1(int no1) {
                this.no1 = no1;
            }

            public int getNo2() {
                return no2;
            }

            public void setNo2(int no2) {
                this.no2 = no2;
            }

            public int getNo3() {
                return no3;
            }

            public void setNo3(int no3) {
                this.no3 = no3;
            }

            public int getNo4() {
                return no4;
            }

            public void setNo4(int no4) {
                this.no4 = no4;
            }

            public int getNo5() {
                return no5;
            }

            public void setNo5(int no5) {
                this.no5 = no5;
            }

            public int getNo6to10() {
                return no6to10;
            }

            public void setNo6to10(int no6to10) {
                this.no6to10 = no6to10;
            }

            public int getNo11to20() {
                return no11to20;
            }

            public void setNo11to20(int no11to20) {
                this.no11to20 = no11to20;
            }

            public int getNo21to50() {
                return no21to50;
            }

            public void setNo21to50(int no21to50) {
                this.no21to50 = no21to50;
            }

            public int getNo51to80() {
                return no51to80;
            }

            public void setNo51to80(int no51to80) {
                this.no51to80 = no51to80;
            }

            public int getNo81to100() {
                return no81to100;
            }

            public void setNo81to100(int no81to100) {
                this.no81to100 = no81to100;
            }
        }
    }
}
