package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.MessageSetBean;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMessageSettingBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;

public class MessageSettingActivity extends AppCompatActivity {

    ActivityMessageSettingBinding binding;
    String userId;
    boolean isClear = false;
    MessageSetBean.Data data;
    boolean isPinned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageSettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#161824"));
        userId = getIntent().getStringExtra("user_id");

        initEvents();
        initData();
    }

    private void initEvents() {
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
                    HttpClient.delBlack(MessageSettingActivity.this, data.getBlackid(), new HttpCallback() {
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

        binding.etRemarkName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (TextUtils.isEmpty(binding.etRemarkName.getText().toString().trim())) {
                        Utils.showToast(MessageSettingActivity.this, "请输入备注名 ");
                    } else {
                        LogUtil.e("设置备注名");
                        setRemarkName(binding.etRemarkName.getText().toString().trim());

                    }
                    return true;
                }

                return false;
            }
        });
    }

    private void setRemarkName(String remarkName) {
        HttpClient.updateRemarkName(this, userId, remarkName, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.showToast(MessageSettingActivity.this, "设置成功");
                Utils.hideSoftKeyboard(MessageSettingActivity.this);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(MessageSettingActivity.this, msg);
            }
        });
    }

    private void initData() {
        V2TIMManager.getConversationManager().getConversation("c2c_" + userId, new V2TIMValueCallback<V2TIMConversation>() {
            @Override
            public void onSuccess(V2TIMConversation v2TIMConversation) {
                isPinned = v2TIMConversation.isPinned();
                binding.ivIsTop.setBackgroundResource(isPinned ? R.drawable.toggle_open : R.drawable.toggle_close);
                binding.ivIsTop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        V2TIMManager.getConversationManager().pinConversation(v2TIMConversation.getConversationID(), !isPinned, new V2TIMCallback() {
                            @Override
                            public void onSuccess() {
                                isPinned = !isPinned;
                                binding.ivIsTop.setBackgroundResource(isPinned ? R.drawable.toggle_open : R.drawable.toggle_close);
                            }

                            @Override
                            public void onError(int code, String desc) {
                                LogUtil.e("pinConversation code:" + code + ",desc:" + desc);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(int code, String desc) {
                LogUtil.e("get conversation error code:" + code + ", desc:" + desc);
            }
        });

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

    public void reportClick(View view) {
        Intent intent = new Intent(MessageSettingActivity.this, ReportActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }
}