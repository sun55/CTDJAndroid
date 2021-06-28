package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivitySmsCodeBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;

public class SmsCodeActivity extends BaseActivity {

    ActivitySmsCodeBinding binding;
    String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySmsCodeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        LinearLayout.LayoutParams l = (LinearLayout.LayoutParams) binding.titleView.getLayoutParams();
        l.topMargin = DisplayUtil.getStatusBarHeight(this);
        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });

        mobile = getIntent().getStringExtra("phone_num");
        binding.tvPhoneNum.setText(Html.fromHtml("已发送验证码至 <font color=#5252FE>" + mobile + "</font>"));
        binding.titleView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showSoftKeyboard(SmsCodeActivity.this, binding.smsCode);
            }
        }, 100);
        countdown();
        binding.smsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                     checkMobile(s.toString().trim());
                }
            }
        });
    }

    private void checkMobile(String smsCode) {
        HttpClient.checkMobile(this, mobile, smsCode, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(SmsCodeActivity.this, RegisterActivity.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("sms_code", smsCode);
                startActivity(intent);

                finish();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(SmsCodeActivity.this, msg);
            }
        });
    }

    /**
     * 重新发送验证码
     * @param view
     */
    public void reSendCode(View view) {
        countdown();
    }

    CountDownTimer timer;
    private void countdown() {
        binding.tvResendCode.setEnabled(false);
        binding.tvResendCode.setTextColor(Color.parseColor("#80EBEBED"));
        if (timer == null) {
            timer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    LogUtil.e("---------onTick" + millisUntilFinished);
                    binding.tvResendCode.setText("重新获取（" + millisUntilFinished / 1000 + "s）");
                }

                @Override
                public void onFinish() {
                    LogUtil.e("----------onFinish");
                    binding.tvResendCode.setEnabled(true);
                    binding.tvResendCode.setTextColor(Color.parseColor("#EBEBED"));
                    binding.tvResendCode.setText("重新获取验证码");
                }
            };
        }
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }
}