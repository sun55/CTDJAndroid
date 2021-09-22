package com.ctdj.djandroid.bean;

import java.io.Serializable;

public class NoticeCustomContent implements Serializable{

    private int challengeType;
    private String sender;
    private String mname;
    private String headimg;
    private int type;

    public NoticeCustomContent(int challengeType, String sender, String mname, String headimg, int type) {
        this.challengeType = challengeType;
        this.sender = sender;
        this.mname = mname;
        this.headimg = headimg;
        this.type = type;
    }

    public int getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(int challengeType) {
        this.challengeType = challengeType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
