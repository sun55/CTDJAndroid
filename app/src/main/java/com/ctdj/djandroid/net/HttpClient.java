package com.ctdj.djandroid.net;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

public class HttpClient {

    public static void sendCode(Context context, String mobile, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("mobile", mobile);
        HttpCaller.doPost(context, API.SEND_CODE, maps, callback);
    }

    /**
     * 一键登录
     * @param context
     * @param token
     * @param accessToken
     * @param callback
     */
    public static void oneKeyLogin(Context context, String token, String accessToken, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", token);
        maps.put("accessToken", accessToken);
        HttpCaller.doPost(context, API.ONE_KEY_LOGIN, maps, callback);
    }

    /**
     * 注册登录
     * @param context
     * @param mobile
     * @param nickname
     * @param sex
     * @param sbcode
     * @param birthday
     * @param headimg
     * @param icode
     * @param callback
     */
    public static void registerLogin(Context context, String mobile, String nickname, int sex, String sbcode, String birthday, String headimg, String icode, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("mobile", mobile);
        maps.put("nickname", nickname);
        maps.put("sex", sex);
        maps.put("sbcode", sbcode);
        maps.put("birthday", birthday);
        maps.put("headimg", headimg);
        maps.put("icode", icode);
        HttpCaller.doPost(context, API.REGISTER_LOGIN, maps, callback);
    }

    /**
     * 验证码登录
     * @param context
     * @param mobile
     * @param vcode
     * @param callback
     */
    public static void checkMobile(Context context, String mobile, String vcode, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("mobile", mobile);
        maps.put("vcode", vcode);
        HttpCaller.doPost(context, API.CHECK_MOBILE, maps, callback);
    }

    public static void uploadImage(Context context, String file, HttpCallback callback) {
        HttpCaller.uploadImageFile(context, API.UPLOAD_FILE, file, callback);
    }

    public static void uploadFiles(Context context, int type, List<String> files, HttpCallback callback) {
        HttpCaller.uploadImageFiles(context, API.UPLOAD_FILES, type, files, callback);
    }

    public static void uploadVideoFile(Context context, String file, HttpCallback callback) {
        HttpCaller.uploadVideoFile(context, API.UPLOAD_FILE, file, callback);
    }

    public static void uploadAudioFile(Context context, String file, HttpCallback callback) {
        HttpCaller.uploadAudioFile(context, API.UPLOAD_FILE, file, callback);
    }
}
