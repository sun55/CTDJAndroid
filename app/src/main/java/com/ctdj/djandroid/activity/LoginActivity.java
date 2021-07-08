package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.bean.LoginBean;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityLoginBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.QuickLoginUiConfig;
import com.google.gson.Gson;
import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    QuickLogin quickLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initProtocolClick();
    }

    public void login(View view) {
        Utils.showLoadingDialog(this);
        if (quickLogin == null) {
            quickLogin = QuickLogin.getInstance(getApplicationContext(), "655d94a19101460c9191fee93f6ad6dc");
            quickLogin.setUnifyUiConfig(QuickLoginUiConfig.getUiConfig(LoginActivity.this, quickLogin));
        }
        quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                Utils.hideLoadingDialog();
                LogUtil.e("onGetMobileNumberSuccess YDToken:" + YDToken + ",mobileNumber:" + mobileNumber);
                quickLogin.onePass(new QuickLoginTokenListener() {
                    @Override
                    public void onGetTokenSuccess(String YDToken, String accessCode) {
                        LogUtil.e("onGetTokenSuccess YDToken:" + YDToken + ",accessCode:" + accessCode);
                        quickLogin.quitActivity();
                        oneKeyLogin(YDToken, accessCode);
                    }

                    @Override
                    public void onGetTokenError(String YDToken, String msg) {
                        LogUtil.e("onGetTokenError YDToken:" + YDToken + ",msg:" + msg);
                        Intent intent = new Intent(LoginActivity.this, PhoneNumActivity.class);
                        intent.putExtra("from", 1);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onGetMobileNumberError(String YDToken, String msg) {
                Utils.hideLoadingDialog();
                LogUtil.e("onGetMobileNumberError YDToken:" + YDToken + ",msg:" + msg);
                Intent intent = new Intent(LoginActivity.this, PhoneNumActivity.class);
                intent.putExtra("from", 1);
                startActivity(intent);
            }
        });
    }

    private void oneKeyLogin(String ydToken, String accessCode) {
        HttpClient.oneKeyLogin(this, ydToken, accessCode, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                LoginBean bean = new Gson().fromJson(result, LoginBean.class);
                if (bean.data.flag == 0) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("mobile", bean.data.mobile);
                    startActivity(intent);
                } else {
                    MyApplication.getInstance().saveUserInfo(bean.data.logindata);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(LoginActivity.this, msg);
            }
        });
    }

    private void initProtocolClick() {
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append("已阅读并同意《用户协议》和《隐私政策》");

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                LogUtil.d("用户协议");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#5252FE"));
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                LogUtil.d("隐私政策");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#5252FE"));
            }
        };
        style.setSpan(clickableSpan1, 6, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(clickableSpan2, 13, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvProtocol.setText(style);
        binding.tvProtocol.setHighlightColor(Color.TRANSPARENT);
        binding.tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
    }
}