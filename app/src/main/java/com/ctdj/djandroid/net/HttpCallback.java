package com.ctdj.djandroid.net;

public abstract class HttpCallback {

    public abstract void onSuccess(String result);

    public abstract void onFailure(String msg);

    public void onFailure(int code, String msg, String result) {

    }
}
