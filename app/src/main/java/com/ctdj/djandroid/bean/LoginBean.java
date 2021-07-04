package com.ctdj.djandroid.bean;

import com.ctdj.djandroid.net.UserInfoBean;

public class LoginBean extends BaseBean {
    public OneKeyLoginData data;

    public class OneKeyLoginData {
        public int flag;
        public String mobile;
        public UserInfoBean logindata;
    }
}
