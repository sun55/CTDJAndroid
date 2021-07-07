package com.ctdj.djandroid.net;

import android.content.Context;

import com.ctdj.djandroid.MyApplication;

import java.util.HashMap;
import java.util.List;

public class HttpClient {

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

    public static void sendCode(Context context, String mobile, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("mobile", mobile);
        HttpCaller.doPost(context, API.SEND_CODE, maps, callback);
    }

    /**
     * 一键登录
     *
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
     *
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
     *
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

    /**
     * 判断昵称是否存在
     *
     * @param context
     * @param nickname
     * @param callback
     */
    public static void isExistName(Context context, String nickname, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("nickname", nickname);
        HttpCaller.doPost(context, API.IS_EXIST_NAME, maps, callback);
    }

    /**
     * 修改用户信息
     *
     * @param context
     * @param type
     * @param param
     * @param callback
     */
    public static void updatePersonal(Context context, int type, String param, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("type", type);
        maps.put("param", param);
        HttpCaller.doPost(context, API.UPDATE_PERSONAL, maps, callback);
    }

    /**
     * 获取关联游戏信息
     *
     * @param context
     * @param callback
     */
    public static void getBindGameInfo(Context context, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        HttpCaller.doPost(context, API.GET_BIND_GAME_INFO, maps, callback);
    }

    /**
     * 绑定游戏昵称
     *
     * @param context
     * @param wqName
     * @param qqName
     * @param callback
     */
    public static void bindGame(Context context, String wqName, String qqName, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("wqName", wqName);
        maps.put("qqName", qqName);
        HttpCaller.doPost(context, API.BIND_GAME, maps, callback);
    }

    /**
     * 查询黑名单列表
     *
     * @param context
     * @param page
     * @param size
     * @param callback
     */
    public static void queryBlackList(Context context, int page, int size, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("page", page);
        maps.put("size", size);
        HttpCaller.doPost(context, API.QUERY_BLACK_LIST, maps, callback);
    }

    /**
     * 添加黑名单
     *
     * @param context
     * @param mid
     * @param blackmid
     * @param callback
     */
    public static void addBlack(Context context, String mid, String blackmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("mid", mid);
        maps.put("blackmid", blackmid);
        HttpCaller.doPost(context, API.ADD_BLACK, maps, callback);
    }

    /**
     * 移除黑名单
     *
     * @param context
     * @param blackid
     * @param mid
     * @param blackmid
     * @param callback
     */
    public static void delBlack(Context context, String blackid, String mid, String blackmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("blackid", blackid);
        maps.put("mid", mid);
        maps.put("blackmid", blackmid);
        HttpCaller.doPost(context, API.DEL_BLACK, maps, callback);
    }


    /**
     * 意见反馈
     *
     * @param context
     * @param callback
     */
    public static void opinion(Context context, String opinion, String img, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("opinion", opinion);
        maps.put("img", img);
        HttpCaller.doPost(context, API.OPINION, maps, callback);
    }
}
