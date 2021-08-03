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
     * @param type     1 修改头像 2 修改昵称 3 修改出生日期 4 地区（拼接，省-市） 5 手机号 6 是否赛事短信提醒（1是 0否） 7 是否隐藏战绩（1是0否）
     * @param param
     * @param vcode    验证码（只用于修改手机号）
     * @param callback
     */
    public static void updatePersonal(Context context, int type, Object param, String vcode, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("type", type);
        maps.put("param", param);
        maps.put("vcode", vcode);
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
     * 实名认证
     *
     * @param context
     * @param cmid
     * @param cmzname
     * @param callback
     */
    public static void realNameAuth(Context context, String cmid, String cmzname, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("cmid", cmid);
        maps.put("cmzname", cmzname);
        HttpCaller.doPost(context, API.ID_CODE_VAILD, maps, callback);
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

    /**
     * 校验验证码
     *
     * @param context
     * @param callback
     */
    public static void checkVcode(Context context, String mobile, String vcode, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("mobile", mobile);
        maps.put("vcode", vcode);
        HttpCaller.doPost(context, API.CHECK_V_CODE, maps, callback);
    }

    /**
     * 查询个人主页
     *
     * @param context
     * @param callback
     */
    public static void queryPersonal(Context context, String fmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("fmid", fmid);
        HttpCaller.doPost(context, API.QUERY_PERSONAL, maps, callback);
    }

    /**
     * 查询个人信息
     *
     * @param context
     * @param callback
     */
    public static void intoPersonal(Context context, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        HttpCaller.doPost(context, API.INTO_PERSONAL, maps, callback);
    }

    /**
     * 查询战绩
     *
     * @param context
     * @param callback
     */
    public static void queryChallengeRecord(Context context, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        HttpCaller.doPost(context, API.QUERY_CHALLENGE_RECORD, maps, callback);
    }

    /**
     * 查询王者战绩和绑定游戏账号信息
     *
     * @param context
     * @param callback
     */
    public static void queryChallengeAndGame(Context context, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        HttpCaller.doPost(context, API.QUERY_CHALLENGE_AND_GAME, maps, callback);
    }

    /**
     * 关注粉丝列表
     *
     * @param context
     * @param type     1 关注 2 粉丝
     * @param page
     * @param size
     * @param callback
     */
    public static void followList(Context context, int type, int page, int size, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("type", type);
        maps.put("page", page);
        maps.put("size", size);
        HttpCaller.doPost(context, API.FOLLOW_LIST, maps, callback);
    }

    /**
     * 添加关注
     *
     * @param context
     * @param fmid
     * @param callback
     */
    public static void addFollow(Context context, String fmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("fmid", fmid);
        HttpCaller.doPost(context, API.ADD_FOLLOW, maps, callback);
    }

    /**
     * 取消关注
     *
     * @param context
     * @param fmid
     * @param callback
     */
    public static void deleteFollow(Context context, String fmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("fmid", fmid);
        HttpCaller.doPost(context, API.DELETE_FOLLOW, maps, callback);
    }

    /**
     * 发起约战
     */
    public static void createMatchRecord(Context context, String gameName, int area, int challengeType, int award, String fmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("gameName", gameName);
        maps.put("area", area);
        maps.put("challengeType", challengeType);
        maps.put("award", award);
        maps.put("fmid", fmid);
        maps.put("gameType", "王者荣耀");
        HttpCaller.doPost(context, API.CREATE_MATCH_RECORD, maps, callback);
    }

    /**
     * 取消匹配
     */
    public static void closeMatch(Context context, int challengeId, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("challengeId", challengeId);
        HttpCaller.doPost(context, API.CLOSE_MATCH, maps, callback);
    }

    /**
     * 取消比赛
     */
    public static void closeChallenge(Context context, int challengeId, int sta, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("challengeId", challengeId);
        maps.put("sta", sta);
        HttpCaller.doPost(context, API.CLOSE_CHALLENGE, maps, callback);
    }

    /**
     * 接受或拒绝约战
     */
    public static void receiveOrRefuseChallenge(Context context, int challengeId, int type, String fgameName, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("challengeId", challengeId);
        maps.put("type", type);
        maps.put("fgameName", fgameName);
        HttpCaller.doPost(context, API.RECEIVE_OR_REFUSE_CHALLENGE, maps, callback);
    }

    /**
     * 提交待审
     *
     * @param sta 2 游戏审核 4 申诉审核
     */
    public static void submitAudit(Context context, int challengeId, int sta, String img, String remarks, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("challengeId", challengeId);
        maps.put("sta", sta);
        maps.put("img", img);
        maps.put("remarks", remarks);
        HttpCaller.doPost(context, API.SUBMIT_AUDIT, maps, callback);
    }

    /**
     * 查询匹配记录
     */
    public static void queryMatchRecord(Context context, String fmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("fmid", fmid);
        HttpCaller.doPost(context, API.QUERY_MATCH_RECORD, maps, callback);
    }

    /**
     * 查询匹配记录
     */
    public static void queryMatchRecordList(Context context, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        HttpCaller.doPost(context, API.QUERY_MATCH_RECORD_LIST, maps, callback);
    }

    /**
     * 查询订单详情
     *
     * @param context
     * @param callback
     */
    public static void queryChallengeOrderDtl(Context context, String orderno, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("orderno", orderno);
        HttpCaller.doPost(context, API.QUERY_CHALLENGE_ORDER_DTL, maps, callback);
    }

    /**
     * 获取聊天设置
     * @param context
     * @param fmid
     * @param callback
     */
    public static void getChatSetInfo(Context context, String fmid, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("fmid", fmid);
        HttpCaller.doPost(context, API.GET_CHAT_SET_INFO, maps, callback);
    }

    /**
     * 进入举报页面
     * @param context
     * @param callback
     */
    public static void reportIntoData(Context context, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        HttpCaller.doPost(context, API.REPORT_INTO_DATA, maps, callback);
    }

    /**
     * 添加举报
     * @param context
     * @param brmid
     * @param typeno
     * @param img
     * @param remarks
     * @param callback
     */
    public static void addReport(Context context, String brmid, int typeno, String img, String remarks, HttpCallback callback) {
        HashMap<String, Object> maps = new HashMap<>();
        maps.put("token", MyApplication.getInstance().getToken());
        maps.put("brmid", brmid);
        maps.put("typeno", typeno);
        maps.put("img", img);
        maps.put("remarks", remarks);
        HttpCaller.doPost(context, API.ADD_REPORT, maps, callback);
    }
}
