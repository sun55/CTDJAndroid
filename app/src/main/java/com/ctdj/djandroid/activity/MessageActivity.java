package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.MessageAdapter;
import com.ctdj.djandroid.audio.AudioRecorderButton;
import com.ctdj.djandroid.bean.MessageBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMessageBinding;
import com.ctdj.djandroid.view.TitleView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMValueCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    V2TIMMessage lastMessage;
    String userId;
    String targetName;
    MessageAdapter adapter;
    boolean wantCancelRecord = false; // 是否想取消录音

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#161824"));

        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        binding.etMessage.setHorizontallyScrolling(false);
        binding.etMessage.setMaxLines(4);
        binding.etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (TextUtils.isEmpty(binding.etMessage.getText())) {
                        Utils.showToast(MessageActivity.this, "请输入消息内容");
                    } else {
                        LogUtil.e("发送消息");
                        sendTextMessage(binding.etMessage.getText().toString());
                    }
                    return true;
                }

                return false;
            }
        });
        adapter = new MessageAdapter(new ArrayList<>());
        binding.rcvMessage.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
        binding.rcvMessage.setAdapter(adapter);
        Intent intent = getIntent();
        lastMessage = (V2TIMMessage) intent.getSerializableExtra("last_message");
        userId = intent.getStringExtra("user_id");
        targetName = intent.getStringExtra("target_name");
        binding.titleView.setTitle(targetName);
        if (lastMessage == null || userId == null) {

        } else {
            getHistoryMessage();
        }
        V2TIMManager.getMessageManager().addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                super.onRecvNewMessage(msg);
                LogUtil.e("onRecvNewMessage");
                adapter.addData(msg);
                binding.rcvMessage.scrollToPosition(0);
            }

            @Override
            public void onRecvC2CReadReceipt(List<V2TIMMessageReceipt> receiptList) {
                super.onRecvC2CReadReceipt(receiptList);
                LogUtil.e("onRecvC2CReadReceipt");
            }

            @Override
            public void onRecvMessageRevoked(String msgID) {
                super.onRecvMessageRevoked(msgID);
                LogUtil.e("onRecvMessageRevoked");
            }

            @Override
            public void onRecvMessageModified(V2TIMMessage msg) {
                super.onRecvMessageModified(msg);
                LogUtil.e("onRecvMessageModified");
            }
        });

        binding.rcvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideBottomViews();
                return false;
            }
        });

        binding.btnAudio.setAudioRecordStateListener(new AudioRecorderButton.AudioRecordStateListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                LogUtil.e("onFinish seconds:" + seconds + ", filePath:" + filePath);
                sendSoundMessage(filePath, (int) seconds);
            }

            @Override
            public void onNormal() {
                LogUtil.e("onNormal");
                wantCancelRecord = false;
                binding.btnAudio.setBackgroundResource(R.drawable.message_recording);
                binding.tvAudioTime.setText("按住说话");
                binding.tvAudioTime.setTextColor(Color.parseColor("#E8E8E8"));
            }

            @Override
            public void onShort() {
                LogUtil.e("onShort");
                Utils.showToast(MessageActivity.this, "录音时间太短");
            }

            @Override
            public void onRecording() {
                LogUtil.e("onRecording");
                wantCancelRecord = false;
            }

            @Override
            public void onCountTime(int second) {
                LogUtil.e("onCountTime second:" + second);
                if (wantCancelRecord) {
                    return;
                }
                binding.tvAudioTime.setTextColor(Color.WHITE);
                binding.tvAudioTime.setText(Utils.getTimeBySecond(second));
                binding.btnAudio.setBackgroundResource(R.drawable.message_recording);
            }

            @Override
            public void onWantCancel() {
                LogUtil.e("onWantCancel");
                wantCancelRecord = true;
                binding.btnAudio.setBackgroundResource(R.drawable.message_record_delete);
                binding.tvAudioTime.setTextColor(Color.parseColor("#FE2A54"));
                binding.tvAudioTime.setText("松开取消");
            }

            @Override
            public void onNoPermission() {
                ActivityCompat.requestPermissions(MessageActivity.this, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            }
        });
    }

    private void hideBottomViews() {
        if (isShowAudioLayout) {
            showAudio(null);
        }

        if (isShowMoreLayout) {
            showMore(null);
        }
    }

    private void sendTextMessage(String text) {
        V2TIMManager.getInstance().sendC2CTextMessage(text, userId, new V2TIMValueCallback<V2TIMMessage>() {
            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                LogUtil.e("发送成功：" + v2TIMMessage.getTextElem().getText());
                adapter.addData(v2TIMMessage);
                binding.rcvMessage.scrollToPosition(0);
                binding.etMessage.setText("");
            }

            @Override
            public void onError(int code, String desc) {
                LogUtil.e("发送失败：" + code + "， msg：" + desc);
            }
        });
    }

    private void sendImageMessage(String imagePath) {
        V2TIMMessage imageMessage = V2TIMManager.getMessageManager().createImageMessage(imagePath);
        V2TIMManager.getMessageManager().sendMessage(imageMessage, userId, "", 0, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onProgress(int progress) {
                LogUtil.e("发送中：" + progress);
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                adapter.addData(v2TIMMessage);
                binding.rcvMessage.scrollToPosition(0);
                binding.etMessage.setText("");
            }

            @Override
            public void onError(int code, String desc) {
                LogUtil.e("发送失败：" + code + ", desc:" + desc);
            }
        });
    }

    private void sendSoundMessage(String soundPath, int duration) {
        V2TIMMessage soundMessage = V2TIMManager.getMessageManager().createSoundMessage(soundPath, duration);
        V2TIMManager.getMessageManager().sendMessage(soundMessage, userId, "", 0, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onProgress(int progress) {
                LogUtil.e("发送中：" + progress);
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                adapter.addData(v2TIMMessage);
                binding.rcvMessage.scrollToPosition(0);
                binding.etMessage.setText("");
            }

            @Override
            public void onError(int code, String desc) {
                LogUtil.e("发送失败：" + code + ", desc:" + desc);
            }
        });
    }

    private void getHistoryMessage() {
        V2TIMManager.getMessageManager().getC2CHistoryMessageList(userId, 90, lastMessage, new V2TIMValueCallback<List<V2TIMMessage>>() {
            @Override
            public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                ArrayList<MessageBean> messageBeans = new ArrayList<>();
                for (V2TIMMessage v : v2TIMMessages) {
                    int msgType = 0;
                    if (v.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
                        if (v.getSender().equals(MyApplication.getInstance().getMid())) {
                            msgType = MessageBean.RIGHT_TXT;
                        } else {
                            msgType = MessageBean.LEFT_TXT;
                        }
                    } else if (v.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {
                        if (v.getSender().equals(MyApplication.getInstance().getMid())) {
                            msgType = MessageBean.RIGHT_IMAGE;
                        } else {
                            msgType = MessageBean.LEFT_IMAGE;
                        }
                    } else if (v.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {
                        if (v.getSender().equals(MyApplication.getInstance().getMid())) {
                            msgType = MessageBean.RIGHT_AUDIO;
                        } else {
                            msgType = MessageBean.LEFT_AUDIO;
                        }
                    }
                    messageBeans.add(new MessageBean(msgType, v));
                }
                fillView(messageBeans);
            }

            @Override
            public void onError(int code, String desc) {

            }
        });
    }

    private void fillView(ArrayList<MessageBean> messageBeans) {
        adapter.addData(messageBeans);
        binding.rcvMessage.scrollToPosition(0);
    }

    public void selectPhoto(View view) {
        if (!Utils.checkPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            LogUtil.e("请求权限");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        }
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
//                    .theme(R.style.picture_white_style)
                .isCamera(false)
                .imageEngine(GlideEngine.createGlideEngine())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
//                .isSingleDirectReturn(true)
//                .isEnableCrop(true)
//                .withAspectRatio(1, 1)
                .freeStyleCropEnabled(true)
//                .showCropFrame(true)
                .isDragFrame(false)
//                .isCompress(true)
                .maxSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .synOrAsy(true)
//                .compressQuality(80)
                .forResult(1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
        if (list == null || list.size() <= 0) {
            return;
        }
        if (requestCode == 1000) {
            String imagePath;
            LogUtil.e("path:" + list.get(0).getPath());
            if (list.get(0).getPath().contains("content://")) {
                LogUtil.e(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath())));
                imagePath = Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath()));
            } else {
                imagePath = list.get(0).getPath();
            }
            sendImageMessage(imagePath);
        }
    }

    public void pickCamera(View view) {
        if (!Utils.checkPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            LogUtil.e("请求权限");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        }
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
//                    .theme(R.style.picture_white_style)
                .isCamera(false)
                .imageEngine(GlideEngine.createGlideEngine())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
//                .isSingleDirectReturn(true)
//                .isEnableCrop(true)
//                .withAspectRatio(1, 1)
                .freeStyleCropEnabled(true)
//                .showCropFrame(true)
                .isDragFrame(false)
//                .isCompress(true)
                .maxSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .synOrAsy(true)
//                .compressQuality(80)
                .forResult(1000);
    }

    boolean isShowMoreLayout = false;
    boolean isShowAudioLayout = false;

    public void showMore(View view) {
        if (isShowAudioLayout) {
            showAudio(null);
        }
        if (isShowMoreLayout) {
            Animation animation = new RotateAnimation(45, 0, binding.ivMore.getWidth() / 2, binding.ivMore.getHeight() / 2);
            animation.setDuration(1000);
            animation.setFillAfter(true);//设置为true，动画转化结束后被应用
            binding.ivMore.startAnimation(animation);
            binding.llMore.setVisibility(View.GONE);
        } else {
            Animation animation = new RotateAnimation(0, 45, binding.ivMore.getWidth() / 2, binding.ivMore.getHeight() / 2);
            animation.setDuration(1000);
            animation.setFillAfter(true);//设置为true，动画转化结束后被应用
            binding.ivMore.startAnimation(animation);
            binding.llMore.setVisibility(View.VISIBLE);
        }
        isShowMoreLayout = !isShowMoreLayout;
    }

    public void showAudio(View view) {
        if (isShowMoreLayout) {
            showMore(null);
        }

        if (isShowAudioLayout) {
            binding.llAudio.setVisibility(View.GONE);
        } else {
            binding.llAudio.setVisibility(View.VISIBLE);
        }
        isShowAudioLayout = !isShowAudioLayout;
    }
}