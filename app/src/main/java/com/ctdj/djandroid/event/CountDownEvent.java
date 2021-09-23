package com.ctdj.djandroid.event;

public class CountDownEvent {
    private int second;

    public CountDownEvent(int second) {
        this.second = second;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
