package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.databinding.ActivityKingRecordBinding;
import com.ctdj.djandroid.view.TitleView;

public class KingRecordActivity extends BaseActivity {

    ActivityKingRecordBinding binding;

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
                intent.putExtra("nickname", "");
                startActivity(intent);
            }
        });

        binding.tvQqNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KingRecordActivity.this, BindGameNicknameActivity.class);
                intent.putExtra("area", "企鹅区服");
                intent.putExtra("nickname", "");
                startActivity(intent);
            }
        });
    }
}