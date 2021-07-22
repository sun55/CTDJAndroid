package com.ctdj.djandroid.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.tencent.imsdk.v2.V2TIMMessage;

public class MessageBean implements MultiItemEntity {
    public static final int LEFT_TXT = 0;
    public static final int RIGHT_TXT = 1;
    public static final int LEFT_IMAGE = 2;
    public static final int RIGHT_IMAGE = 3;
    public static final int LEFT_AUDIO = 4;
    public static final int RIGHT_AUDIO = 5;
    public static final int CUSTOM = 6;
    //item类型
    private int fieldType;
    private V2TIMMessage v2TIMMessage;
    private String MediaPath;

    public MessageBean(V2TIMMessage v2TIMMessage) {
        this.v2TIMMessage = v2TIMMessage;
    }

    public MessageBean(int fieldType, V2TIMMessage v2TIMMessage) {
        //将传入的type赋值
        this.fieldType = fieldType;
        this.v2TIMMessage = v2TIMMessage;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public int getItemType() {
        return fieldType;
    }

    public V2TIMMessage getV2TIMMessage() {
        return v2TIMMessage;
    }

    public String getMediaPath() {
        return MediaPath;
    }

    public void setMediaPath(String mediaPath) {
        MediaPath = mediaPath;
    }
}
