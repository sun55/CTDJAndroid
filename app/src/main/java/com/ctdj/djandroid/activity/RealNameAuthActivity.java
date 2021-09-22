package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.bean.UpdatePersonalBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityRealNameAuthBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.net.UserInfoBean;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

public class RealNameAuthActivity extends BaseActivity {

    ActivityRealNameAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRealNameAuthBinding.inflate(LayoutInflater.from(this));
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
        UserInfoBean userInfoBean = MyApplication.getInstance().getUserInfo();
        if (userInfoBean.isreal == 1) {
            binding.ll1.setVisibility(View.GONE);
            binding.tvTips.setVisibility(View.GONE);
            binding.ll2.setVisibility(View.VISIBLE);
            binding.tvRealName2.setText(TextUtils.isEmpty(userInfoBean.cmzname) || userInfoBean.cmzname.length() < 2 ?
                    userInfoBean.cmzname : userInfoBean.cmzname.charAt(0) + "**");
            binding.tvIdCard2.setText(TextUtils.isEmpty(userInfoBean.cmid) || userInfoBean.cmid.length() != 18 ?
                            userInfoBean.cmid : userInfoBean.cmid.charAt(0) + "***** **** **** ***" + userInfoBean.cmid.charAt(17));
            binding.btnAuth.setVisibility(View.GONE);
            binding.tvAuthStatus.setText("实名认证已完成");
        } else {
            binding.ll1.setVisibility(View.VISIBLE);
            binding.tvTips.setVisibility(View.VISIBLE);
            binding.ll2.setVisibility(View.GONE);
            binding.btnAuth.setVisibility(View.VISIBLE);
            binding.tvAuthStatus.setText("实名认证");
            binding.btnAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(binding.etRealName.getText().toString().trim())) {
                        Utils.showToast(RealNameAuthActivity.this, "请输入真实姓名");
                        return;
                    }

                    if (TextUtils.isEmpty(binding.etIdCard.getText().toString().trim())) {
                        Utils.showToast(RealNameAuthActivity.this, "请输入真实身份证号");
                        return;
                    }
                    HttpClient.realNameAuth(RealNameAuthActivity.this,
                            binding.etIdCard.getText().toString().trim(),
                            binding.etRealName.getText().toString().trim(),
                            new HttpCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    UpdatePersonalBean bean = new Gson().fromJson(result, UpdatePersonalBean.class);
                                    MyApplication.getInstance().saveUserInfo(bean.data);
                                    Utils.showToast(RealNameAuthActivity.this, "认证成功");
                                    finish();
                                }

                                @Override
                                public void onFailure(String msg) {
                                    Utils.showToast(RealNameAuthActivity.this, msg);
                                }
                            });
                }
            });
        }

        binding.etRealName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(binding.etRealName.getText().toString().trim()) || TextUtils.isEmpty(binding.etIdCard.getText().toString().trim())) {
                    binding.btnAuth.setAlpha(0.5f);
                } else {
                    binding.btnAuth.setAlpha(1f);
                }
            }
        });
        binding.etIdCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 6 && i != 11 && i != 16 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 7 || sb.length() == 12 || sb.length() == 17) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    binding.etIdCard.setText(sb.toString());
                    binding.etIdCard.setSelection(index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(binding.etRealName.getText().toString().trim()) || TextUtils.isEmpty(binding.etIdCard.getText().toString().trim())) {
                    binding.btnAuth.setAlpha(0.5f);
                } else {
                    binding.btnAuth.setAlpha(1f);
                }
            }
        });
    }
}