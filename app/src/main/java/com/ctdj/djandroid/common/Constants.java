package com.ctdj.djandroid.common;

import android.os.Environment;

import androidx.multidex.BuildConfig;

import java.io.File;

public class Constants {

    public static final String APP_PATH = "com.ctdj.djandroid";
    public static final String INSIDE_VERSION = BuildConfig.VERSION_CODE + "";

    public static final String SP_APP_CONFIGURES = "app-configures";
    public static final String SP_IM_TOKEN = "im-token";

    public static final String LOGOUT_BROADCAST = "logout-broadcast";
    public static final String RTM_MSG_BROADCAST = "rtm_msg_broadcast";
    public static final String RTM_CHANNEL_BROADCAST = "rtm_channel_broadcast";
    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_FILE = "DEVICE_ID";
    public static final String DEVICE_DIR = "ctdj";
    public static final String API_SIGN = "ctdj";
    public static final int IM_APP_ID = 1400548123;

    // 接口错误码
    public static final int ERROR_NAME_IS_EXIST = 101; // 昵称已存在

    public static final String LOCAL_LOGIN_TABLE = "LOGIN_INFO";
    public static final String LOCAL_USER_ACCOUNT = "USER_ACOUNT";
    public static final String LOCAL_USER_TOKEN = "USER_TOKEN";

    public static final String OPTION_TABLE = "OPTION_TABLE";
    public static final String OPTION_KEYBOARD_HEIGHT = "OPTION_KEYBOARD_HEIGHT";
    /**
     * APP 缓存文件夹根目录
     */
    public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator+"ctdj";

    public static final String APP_CACHE_AUDIO = APP_CACHE_PATH + File.separator + "audio";

    public static final String APP_CACHE_IMAGE = APP_CACHE_PATH + File.separator + "image";

    public static final String APP_CACHE_VIDEO = APP_CACHE_PATH + File.separator + "video";
}
