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
}
