package com.ctdj.djandroid.net;

public class API {
    public static final String BASE_URL = "http://42.193.149.5:10010";

    public static final String UPLOAD_FILE = "/api/common/upload";
    public static final String UPLOAD_FILES = "/api/upload/files";
    public static final String SIGN_IN = "/api/login/signin";

    public static final String ONE_KEY_LOGIN = "/api/login/oneclickLogin";
    public static final String SEND_CODE = "/api/login/sendcode";
    public static final String REGISTER_LOGIN = "/api/login/registerLogin";
    public static final String CHECK_MOBILE = "/api/login/checkMobile";
    public static final String IS_EXIST_NAME = "/api/login/isExistName";
    public static final String UPDATE_PERSONAL = "/api/mydata/updatepersonal";
    public static final String OPINION = "/api/opinion/add"; // 意见反馈
     public static final String GET_BIND_GAME_INFO = "/api/mydata/getBindGameInfo"; // 获取关联游戏账号
    public static final String BIND_GAME = "/api/mydata/bindGame"; // 绑定游戏昵称
    public static final String QUERY_BLACK_LIST = "/api/black/queryblacklist"; // 查询黑名单列表
    public static final String ADD_BLACK = "/api/black/add"; // 添加黑名单
    public static final String DEL_BLACK = "/api/black/del"; // 移除黑名单
    public static final String ID_CODE_VAILD = "/api/mydata/IdCodeVaild"; // 实名认证
    public static final String CHECK_V_CODE = "/api/login/checkVcode"; // 校验验证码
    public static final String QUERY_PERSONAL = "/api/mydata/personal"; // 查询个人主页
    public static final String INTO_PERSONAL = "/api/mydata/intopersonal"; // 查询个人信息
    public static final String QUERY_CHALLENGE_RECORD = "/api/challenge/queryChallengeRecord"; // 查询战绩
    public static final String QUERY_CHALLENGE_AND_GAME = "/api/challenge/queryChallengeAndGame"; // 查询战绩绑定游戏账号信息
    public static final String FOLLOW_LIST = "/api/follow/followList"; // 关注或粉丝列表
    public static final String ADD_FOLLOW = "/api/follow/addFollow"; // 添加关注
    public static final String DELETE_FOLLOW = "/api/follow/delFollow"; //  移除关注
    public static final String CREATE_MATCH_RECORD = "/api/challenge/createMatchRecord"; // 发起约战
    public static final String CLOSE_MATCH = "/api/challenge/closeMatch"; // 取消约战
    public static final String CLOSE_CHALLENGE = "/api/challenge/closeChallenge"; // 取消比赛
    public static final String RECEIVE_OR_REFUSE_CHALLENGE = "/api/challenge/recevieOrRefuseChallenge"; // 接受或拒绝比赛
    public static final String SUBMIT_AUDIT = "/api/challenge/submitAudit"; // 提交待审
    public static final String QUERY_MATCH_RECORD = "/api/challenge/queryMatchOrderById"; // 查询匹配记录
    public static final String QUERY_MATCH_RECORD_LIST = "/api/challenge/queryMatchRecordList"; // 查询用户匹配列表列表
    public static final String QUERY_CHALLENGE_ORDER_DTL = "/api/challenge/queryChallengeOrderDtl"; // 查询订单详情
    public static final String GET_CHAT_SET_INFO = "/api/chat/getChatSetInfo"; // 获取聊天设置
    public static final String REPORT_INTO_DATA = "/api/report/intodata"; // 进入举报页面
    public static final String ADD_REPORT = "/api/report/addreport"; // 举报
}
