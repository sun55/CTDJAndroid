package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMessageSettingBinding;
import com.ctdj.djandroid.view.TitleView;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;

public class MessageSettingActivity extends AppCompatActivity {

    ActivityMessageSettingBinding binding;
    String userId;
    boolean isClear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageSettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#161824"));
        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                Intent intent = new Intent(MessageSettingActivity.this, MessageActivity.class);
                intent.putExtra("is_clear", isClear);
                setResult(101, intent);
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        userId = getIntent().getStringExtra("user_id");
        binding.tvClearMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                V2TIMManager.getMessageManager().clearC2CHistoryMessage(userId, new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        Utils.showToast(MessageSettingActivity.this, "删除成功");
                        isClear = true;
                    }

                    @Override
                    public void onError(int code, String desc) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessageSettingActivity.this, MessageActivity.class);
        intent.putExtra("is_clear", isClear);
        setResult(101, intent);
        finish();
    }
}