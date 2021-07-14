package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.UpdatePersonalBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityPhoneSettingBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.net.UserInfoBean;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

/**
 * 手机号设置
 */
public class PhoneSettingActivity extends BaseActivity {

    ActivityPhoneSettingBinding binding;
    UserInfoBean userInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneSettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        LinearLayoutCompat.LayoutParams l = (LinearLayoutCompat.LayoutParams) binding.titleView.getLayoutParams();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillView();
    }

    private void fillView() {
        userInfoBean = MyApplication.getInstance().getUserInfo();
        if (!TextUtils.isEmpty(userInfoBean.mobile) && userInfoBean.mobile.length() == 11) {
            binding.tvPhoneNum.setText("86 " + userInfoBean.mobile.substring(0, 3) + "****" + userInfoBean.mobile.substring(7));
        }
        binding.ivToggle.setBackgroundResource(userInfoBean.isNote == 1 ? R.drawable.toggle_open : R.drawable.toggle_close);
        binding.ivToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpClient.updatePersonal(PhoneSettingActivity.this, 6, userInfoBean.isNote == 1 ? 0 : 1, "", new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        UpdatePersonalBean personalBean = new Gson().fromJson(result, UpdatePersonalBean.class);
                        MyApplication.getInstance().saveUserInfo(personalBean.data);
                        fillView();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.showToast(PhoneSettingActivity.this, msg);
                    }
                });
            }
        });

        binding.btnUpdatePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
    }

    public void sendCode() {
        String phoneNum = userInfoBean.mobile.substring(0, 3) + " " + userInfoBean.mobile.substring(3, 7) + " " + userInfoBean.mobile.substring(7);
        HttpClient.sendCode(this, userInfoBean.mobile, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(PhoneSettingActivity.this, SmsCodeActivity.class);
                intent.putExtra("phone_num", phoneNum);
                intent.putExtra("from", 2);
                startActivity(intent);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PhoneSettingActivity.this, msg);
            }
        });
    }
}