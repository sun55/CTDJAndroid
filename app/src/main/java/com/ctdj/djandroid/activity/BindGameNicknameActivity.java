package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityBindGameNicknameBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;

/**
 * 关联游戏昵称
 */
public class BindGameNicknameActivity extends BaseActivity {

    ActivityBindGameNicknameBinding binding;
    int area;
    String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBindGameNicknameBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) binding.titleView.getLayoutParams();
        l.topMargin = DisplayUtil.getStatusBarHeight(this);
        area = getIntent().getIntExtra("area", 1);
        nickname = getIntent().getStringExtra("nickname");
        binding.tvGameArea.setText(area == 1 ? "微信区服" : "企鹅区服");
        binding.etGameNickname.setText(nickname);
        if (TextUtils.isEmpty(nickname)) {
            binding.llTips.setVisibility(View.VISIBLE);
            binding.btnBind.setText("完成关联");
            binding.btnBind.setAlpha(0.5f);
        } else {
            binding.llTips.setVisibility(View.GONE);
            binding.btnBind.setText("更换关联");
        }

        binding.etGameNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    binding.btnBind.setAlpha(0.5f);
                } else {
                    binding.btnBind.setAlpha(1f);
                }
            }
        });

        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });

        binding.ivAboutBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BindGameNicknameActivity.this, AboutBindGameActivity.class));
            }
        });

        binding.btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.etGameNickname.getText().toString().trim())) {
                    Utils.showToast(BindGameNicknameActivity.this, "请输入游戏昵称");
                } else {
                    HttpClient.bindGame(BindGameNicknameActivity.this,
                            area == 1 ? binding.etGameNickname.getText().toString().trim() : "",
                            area == 1 ? "" : binding.etGameNickname.getText().toString().trim(),
                            new HttpCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    Utils.showToast(BindGameNicknameActivity.this, "设置成功");
                                    finish();
                                }

                                @Override
                                public void onFailure(String msg) {
                                    Utils.showToast(BindGameNicknameActivity.this, msg);
                                }
                            });
                }
            }
        });
    }
}