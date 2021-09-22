package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.bean.BindGameInfoBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityBindGameAccountBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

/**
 * 关联游戏账号
 */
public class BindGameAccountActivity extends BaseActivity {

    ActivityBindGameAccountBinding binding;

    String wxName = "";
    String qqName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityBindGameAccountBinding.inflate(LayoutInflater.from(this));
        super.onCreate(savedInstanceState);
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

        binding.tvWxNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BindGameAccountActivity.this, BindGameNicknameActivity.class);
                intent.putExtra("area", 1);
                intent.putExtra("nickname", wxName);
                startActivity(intent);
            }
        });

        binding.tvQqNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BindGameAccountActivity.this, BindGameNicknameActivity.class);
                intent.putExtra("area", 2);
                intent.putExtra("nickname", qqName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpClient.getBindGameInfo(this, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                BindGameInfoBean bindGameInfoBean = new Gson().fromJson(result, BindGameInfoBean.class);
                wxName = bindGameInfoBean.getData().get(0).getWxName();
                qqName = bindGameInfoBean.getData().get(0).getQqName();
                binding.tvWxNickname.setText(TextUtils.isEmpty(wxName) ? "去关联微信区服账号" : wxName);
                binding.tvQqNickname.setText(TextUtils.isEmpty(qqName) ? "去关联企鹅区服账号" : qqName);
                binding.tvWxNickname.setTextColor(Color.parseColor(TextUtils.isEmpty(wxName) ? "#fea13f" : "#ebebed"));
                binding.tvQqNickname.setTextColor(Color.parseColor(TextUtils.isEmpty(qqName) ? "#fea13f" : "#ebebed"));
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(BindGameAccountActivity.this, msg);
            }
        });
    }
}