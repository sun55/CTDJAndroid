package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.bean.ChallengeRecordBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityKingRecordBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

public class KingRecordActivity extends BaseActivity {

    ActivityKingRecordBinding binding;
    String wxName = "";
    String qqName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKingRecordBinding.inflate(LayoutInflater.from(this));
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
                Intent intent = new Intent(KingRecordActivity.this, BindGameNicknameActivity.class);
                intent.putExtra("area", "微信区服");
                intent.putExtra("nickname", wxName);
                startActivity(intent);
            }
        });

        binding.tvQqNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KingRecordActivity.this, BindGameNicknameActivity.class);
                intent.putExtra("area", "企鹅区服");
                intent.putExtra("nickname", qqName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpClient.queryChallengeRecord(this, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                ChallengeRecordBean bean = new Gson().fromJson(result, ChallengeRecordBean.class);
                qqName = bean.getData().getGame().get(0).getQqName();
                wxName = bean.getData().getGame().get(0).getWxName();
                binding.tvCount.setText(bean.getData().getRecord().getNum() + "");
                binding.tvWin.setText(bean.getData().getRecord().getWin());
                binding.tvBonus.setText(bean.getData().getRecord().getBonus() + "");
                binding.tvWxNickname.setText(TextUtils.isEmpty(wxName) ? "去关联微信区服账号" : wxName);
                binding.tvQqNickname.setText(TextUtils.isEmpty(qqName) ? "去关联企鹅区服账号" : qqName);
                binding.tvWxNickname.setTextColor(Color.parseColor(TextUtils.isEmpty(wxName) ? "#fea13f" : "#ebebed"));
                binding.tvQqNickname.setTextColor(Color.parseColor(TextUtils.isEmpty(qqName) ? "#fea13f" : "#ebebed"));
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(KingRecordActivity.this, msg);
            }
        });
    }
}