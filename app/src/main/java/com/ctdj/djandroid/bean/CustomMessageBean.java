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
    private int area;
    private String game_nickname;

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

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getGame_nickname() {
        return game_nickname;
    }

    public void setGame_nickname(String game_nickname) {
        this.game_nickname = game_nickname;
    }

    @Override
    public String toString() {
        return "CustomMessageBean{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", area=" + area +
                ", game_nickname='" + game_nickname + '\'' +
                '}';
    }
}
