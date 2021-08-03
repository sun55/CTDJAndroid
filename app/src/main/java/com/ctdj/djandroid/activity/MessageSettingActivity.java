package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.MessageSetBean;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMessageSettingBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;

public class MessageSettingActivity extends AppCompatActivity {

    ActivityMessageSettingBinding binding;
    String userId;
    boolean isClear = false;
    MessageSetBean.Data data;

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
        initData();
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
                        Utils.showToast(MessageSettingActivity.this, "code:" + code + ", desc:" + desc);
                    }
                });
            }
        });
        binding.ivAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.isIsFollow()) {
                    HttpClient.deleteFollow(MessageSettingActivity.this, userId, new HttpCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Utils.showToast(MessageSettingActivity.this, "取消关注成功");
                            initData();
                        }

                        @Override
                        public void onFailure(String msg) {
                            Utils.showToast(MessageSettingActivity.this, msg);
                        }
                    });
                } else {
                    HttpClient.addFollow(MessageSettingActivity.this, userId, new HttpCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Utils.showToast(MessageSettingActivity.this, "添加关注成功");
                            initData();
                        }

                        @Override
                        public void onFailure(String msg) {
                            Utils.showToast(MessageSettingActivity.this, msg);
                        }
                    });
                }
            }
        });

        binding.ivBlackToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.isIsBlack()) {
                    HttpClient.delBlack(MessageSettingActivity.this, data.getBlackid(), MyApplication.getInstance().getMid(), userId, new HttpCallback() {
                        @Override
                        public void onSuccess(String result) {
                            initData();
                        }

                        @Override
                        public void onFailure(String msg) {
                            Utils.showToast(MessageSettingActivity.this, msg);
                        }
                    });
                } else {
                    HttpClient.addBlack(MessageSettingActivity.this, MyApplication.getInstance().getMid(), userId, new HttpCallback() {
                        @Override
                        public void onSuccess(String result) {
                            initData();
                        }

                        @Override
                        public void onFailure(String msg) {
                            Utils.showToast(MessageSettingActivity.this, msg);
                        }
                    });
                }
            }
        });
    }

    private void initData() {
        HttpClient.getChatSetInfo(this, userId, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                MessageSetBean bean = new Gson().fromJson(result, MessageSetBean.class);
                data = bean.getData();
                fillView();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(MessageSettingActivity.this, msg);
            }
        });
    }

    private void fillView() {
        Glide.with(this).load(data.getHeadimg()).into(binding.ivAvatar);
        binding.tvNickname.setText(data.getMname());
        binding.etRemarkName.setText(data.getRemarkName());
        binding.ivAddFriend.setBackgroundResource(data.isIsFollow() ? R.drawable.friend_added_icon : R.drawable.friend_add_icon);
        binding.ivBlackToggle.setBackgroundResource(data.isIsBlack() ? R.drawable.toggle_open : R.drawable.toggle_close);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessageSettingActivity.this, MessageActivity.class);
        intent.putExtra("is_clear", isClear);
        setResult(101, intent);
        finish();
    }
}