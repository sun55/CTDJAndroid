package com.ctdj.djandroid;

import android.app.ActivityManager;
import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.ctdj.djandroid.common.AppConfig;
import com.ctdj.djandroid.common.AppFrontBackHelper;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.net.UserInfoBean;
import com.netease.yunxin.login.OneKeyLoginManager;
import com.netease.yunxin.login.listener.InitListener;
import com.scwang.smart.refresh.header.ClassicsHeader;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MyApplication extends MultiDexApplication {

    private Settings.Global mGlobal;
    private boolean hasNewMsg = false;
    private Handler handler;
    public static MyApplication instance;

    static {
        ClassicsHeader.REFRESH_HEADER_PULLING = "下拉刷新...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "释放刷新...";
        ClassicsHeader.REFRESH_HEADER_UPDATE = "上次更新 HH:mm";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    private void init() {
        OneKeyLoginManager.getInstance().init(getApplicationContext(), "b38130ee38f240ce819036daa08f14e4", new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
                LogUtil.e("一键登录：" + code + "，，" + result);
            }
        });
        try {
            HttpResponseCache.install(new File(getApplicationContext().getCacheDir(), "http"), 1024 * 1024 * 128);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int pId = Process.myPid();
        String currentProcessName = getProcessNameById(pId);
        if (TextUtils.isEmpty(currentProcessName) || !currentProcessName.equals(getPackageName())) {
            return;
        }
        new AppFrontBackHelper().register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {

            }

            @Override
            public void onBack() {
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private String getProcessNameById(int pId) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List processes = activityManager.getRunningAppProcesses();
        Iterator iterator = processes.iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) iterator.next();
            if (info.pid == pId) {
                return info.processName;
            }
        }
        return null;
    }

    public boolean isHasNewMsg() {
        return hasNewMsg;
    }

    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static MyApplication getInstance() {
        return instance;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        if (TextUtils.isEmpty(value) || "null".equals(value)) {
            value = "";
        }
        AppConfig.getAppConfig(this).set(key, value);
    }

    public void saveUserInfo(UserInfoBean user) {
        setProperty("user.id", String.valueOf(user.id));
        setProperty("user.mname", user.mname);
        setProperty("user.mid", user.mid);
        setProperty("user.sex", String.valueOf(user.sex));
        setProperty("user.age", String.valueOf(user.age));
        setProperty("user.mobile", user.mobile);
        setProperty("user.sta", String.valueOf(user.sta));
        setProperty("user.cmid", user.cmid);
        setProperty("user.cmzname", user.cmzname);
        setProperty("user.headimg", user.headimg);
        setProperty("user.province", user.province);
        setProperty("user.city", user.city);
        setProperty("user.token", user.token);
        setProperty("user.ip", user.ip);
        setProperty("user.devicecode", user.devicecode);
        setProperty("user.onlinesta", String.valueOf(user.onlinesta));
        setProperty("user.gold", user.gold);
        setProperty("user.star", user.star);
        setProperty("user.challengeVolume", user.challengeVolume);
        setProperty("user.isNote", String.valueOf(user.isNote));
        setProperty("user.birthday", user.birthday);
    }

    public UserInfoBean getUserInfo() {
        UserInfoBean bean = new UserInfoBean();
        bean.id = Integer.parseInt(getProperty("user.id"));
        bean.mname = getProperty("user.mname");
        bean.mid = getProperty("user.mid");
        bean.sex = Integer.parseInt(getProperty("user.sex"));
        bean.age = Integer.parseInt(getProperty("user.age"));
        bean.mobile = getProperty("user.mobile");
        bean.sta = Integer.parseInt(getProperty("user.sta"));
        bean.cmid = getProperty("user.cmid");
        bean.cmzname = getProperty("user.cmzname");
        bean.headimg = getProperty("user.headimg");
        bean.province = getProperty("user.province");
        bean.city = getProperty("user.city");
        bean.token = getProperty("user.token");
        bean.ip = getProperty("user.ip");
        bean.devicecode = getProperty("user.devicecode");
        bean.onlinesta = Integer.parseInt(getProperty("user.onlinesta"));
        bean.gold = getProperty("user.gold");
        bean.star = getProperty("user.star");
        bean.challengeVolume = getProperty("user.challengeVolume");
        bean.isNote = Integer.parseInt(getProperty("user.isNote"));
        bean.birthday = getProperty("user.birthday");
        return bean;
    }

    public String getMid() {
        return getProperty("user.mid");
    }

    public String getToken() {
        return getProperty("user.token");
    }

    /**
     * 清除登录信息
     */
    public void cleanUserInfo() {
        removeProperty(
                "user.id",
                "user.mname",
                "user.mid",
                "user.sex",
                "user.age",
                "user.mobile",
                "user.sta",
                "user.cmid",
                "user.cmzname",
                "user.headimg",
                "user.province",
                "user.city",
                "user.token",
                "user.ip",
                "user.devicecode",
                "user.onlinesta",
                "user.gold",
                "user.star",
                "user.challengeVolume",
                "user.isNote"
        );
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

}
