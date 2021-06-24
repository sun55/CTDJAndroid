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

import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.databinding.ActivityLoginBinding;
import com.netease.nis.quicklogin.QuickLogin;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initProtocolClick();
        QuickLogin login = QuickLogin.getInstance(getApplicationContext(), "b38130ee38f240ce819036daa08f14e4");
    }

    public void login(View view) {
        startActivity(new Intent(LoginActivity.this, LoginSelectActivity.class));
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