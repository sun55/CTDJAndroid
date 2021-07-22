package com.ctdj.djandroid.bean;

import java.io.Serializable;

/**
 * @Author : Sun
 * @Time : 2021/7/22 17:50
 * @Description :
 */
public class CustomMessageBean implements Serializable {

    private int type;
    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
