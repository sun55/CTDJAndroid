package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
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

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.MessageAdapter;
import com.ctdj.djandroid.audio.AudioRecorderButton;
import com.ctdj.djandroid.bean.CustomMessageBean;
import com.ctdj.djandroid.bean.MessageBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMessageBinding;
import com.ctdj.djandroid.dialog.InvitePlayDialog;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tim.uikit.component.AudioPlayer;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    V2TIMMessage lastMessage;
    String userId;
    String targetName;
    MessageAdapter adapter;
    boolean wantCancelRecord = false; // 是否想取消录音
    AnimationDrawable leftGrayAnim;
    AnimationDrawable leftRedAnim;
    AnimationDrawable rightGrayAnim;
    AnimationDrawable rightRedAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#161824"));
        Intent intent = getIntent();
        lastMessage = (V2TIMMessage) intent.getSerializableExtra("last_message");
        userId = intent.getStringExtra("user_id");
        targetName = intent.getStringExtra("target_name");

        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(MessageActivity.this, MessageSettingActivity.class);
                intent.putExtra("user_id", userId);
                startActivityForResult(intent, 101);
            }
        });
        leftGrayAnim = (AnimationDrawable) getDrawable(R.drawable.gray_left_sound_anim);
        leftRedAnim = (AnimationDrawable) getDrawable(R.drawable.red_left_sound_anim);
        rightGrayAnim = (AnimationDrawable) getDrawable(R.drawable.gray_right_sound_anim);
        rightRedAnim = (AnimationDrawable) getDrawable(R.drawable.red_right_sound_anim);
        binding.etMessage.setHorizontallyScrolling(false);
        binding.etMessage.setMaxLines(4);
        binding.etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (TextUtils.isEmpty(binding.etMessage.getText())) {
                        Utils.showToast(MessageActivity.this, "请输入消息内容");
                    } else {
                        LogUtil.e("发送文本消息");
                        sendTextMessage(binding.etMessage.getText().toString());
                    }
                    return true;
                }

                return false;
            }
        });
        adapter = new MessageAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        manager.setStackFromEnd(true);
        binding.rcvMessage.setLayoutManager(manager);
        binding.rcvMessage.setAdapter(adapter);
        binding.titleView.setTitle(targetName);
        if (lastMessage == null || userId == null) {

        } else {
            getHistoryMessage();
        }
        V2TIMManager.getMessageManager().addAdvancedMsgListener(new V2TIMAdvancedMsgListener() {
            @Override
            public void onRecvNewMessage(V2TIMMessage msg) {
                super.onRecvNewMessage(msg);
                adapter.addData(msg);
                binding.rcvMessage.scrollToPosition(adapter.getItemCount() - 1);
                V2TIMManager.getMessageManager().markC2CMessageAsRead(msg.getUserID(), null);
                if (msg.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {
                    String path = TUIKitConstants.RECORD_DOWNLOAD_DIR + msg.getSoundElem().getUUID();
                    File file = new File(path);
                    if (!file.exists()) {
                        msg.getSoundElem().downloadSound(path, new V2TIMDownloadCallback() {
                            @Override
                            public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                                LogUtil.e("下载音频：" + progressInfo.getCurrentSize() + " / " + progressInfo.getTotalSize());
                            }

                            @Override
                            public void onSuccess() {
                                LogUtil.e("下载音频成功");
                            }

                            @Override
                            public void onError(int code, String desc) {
                                LogUtil.e("下载音频失败code:" + code + "desc:" + desc);
                            }
                        });
                    }
                } else if (msg.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                    CustomMessageBean bean = new Gson().fromJson(new String(msg.getCustomElem().getData()), CustomMessageBean.class);
                    LogUtil.e("新的自定义消息：" + bean.toString());

                }
            }

            @Override
            public void onRecvC2CReadReceipt(List<V2TIMMessageReceipt> receiptList) {
                super.onRecvC2CReadReceipt(receiptList);
            }

            @Override
            public void onRecvMessageRevoked(String msgID) {
                super.onRecvMessageRevoked(msgID);
            }

            @Override
            public void onRecvMessageModified(V2TIMMessage msg) {
                super.onRecvMessageModified(msg);
            }
        });

        binding.rcvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideBottomViews();
                Utils.hideSoftKeyboard(MessageActivity.this);
                return false;
            }
        });

        binding.btnAudio.setAudioRecordStateListener(new AudioRecorderButton.AudioRecordStateListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                sendSoundMessage(filePath, (int) seconds);
            }

            @Override
            public void onNormal() {
                Utils.vibrator();
                wantCancelRecord = false;
                binding.btnAudio.setBackgroundResource(R.drawable.message_recording);
                binding.tvAudioTime.setText("按住说话");
                binding.tvAudioTime.setTextColor(Color.parseColor("#E8E8E8"));
                binding.ivLeftAnim.setBackgroundResource(0);
                binding.ivRightAnim.setBackgroundResource(0);
                if (leftGrayAnim != null) {
                    if (leftGrayAnim.isRunning()) {
                        leftGrayAnim.stop();
                    }
                    leftGrayAnim = null;
                }
                if (leftRedAnim != null) {
                    if (leftRedAnim.isRunning()) {
                        leftRedAnim.stop();
                    }
                    leftRedAnim = null;
                }
                if (rightGrayAnim != null) {
                    if (rightGrayAnim.isRunning()) {
                        rightGrayAnim.stop();
                    }
                    rightGrayAnim = null;
                }
                if (rightRedAnim != null) {
                    if (rightRedAnim.isRunning()) {
                        rightRedAnim.stop();
                    }
                    rightRedAnim = null;
                }
            }

            @Override
            public void onShort() {
                Utils.showToast(MessageActivity.this, "录音时间太短");
            }

            @Override
            public void onRecording() {
                wantCancelRecord = false;
                LogUtil.e("onRecording");
                if (leftRedAnim != null && leftRedAnim.isRunning()) {
                    leftRedAnim.stop();
                    leftRedAnim = null;
                }
                if (rightRedAnim != null && rightRedAnim.isRunning()) {
                    rightRedAnim.stop();
                    rightRedAnim = null;
                }
                if (leftGrayAnim == null) {
                    leftGrayAnim = (AnimationDrawable) getDrawable(R.drawable.gray_left_sound_anim);
                }
                if (rightGrayAnim == null) {
                    rightGrayAnim = (AnimationDrawable) getDrawable(R.drawable.gray_right_sound_anim);
                }
                binding.ivLeftAnim.setBackground(leftGrayAnim);
                binding.ivRightAnim.setBackground(rightGrayAnim);
                leftGrayAnim.start();
                rightGrayAnim.start();
            }

            @Override
            public void onCountTime(int second) {
                if (wantCancelRecord) {
                    return;
                }
                binding.tvAudioTime.setTextColor(Color.WHITE);
                binding.tvAudioTime.setText(Utils.getTimeBySecond(second));
                binding.btnAudio.setBackgroundResource(R.drawable.message_recording);
            }

            @Override
            public void onWantCancel() {
                wantCancelRecord = true;
                binding.btnAudio.setBackgroundResource(R.drawable.message_record_delete);
                binding.tvAudioTime.setTextColor(Color.parseColor("#FE2A54"));
                binding.tvAudioTime.setText("松开取消");
                if (leftGrayAnim != null && leftGrayAnim.isRunning()) {
                    leftGrayAnim.stop();
                    leftGrayAnim = null;
                }
                if (rightGrayAnim != null && rightGrayAnim.isRunning()) {
                    rightGrayAnim.stop();
                    rightGrayAnim = null;
                }
                if (leftRedAnim == null) {
                    leftRedAnim = (AnimationDrawable) getDrawable(R.drawable.red_left_sound_anim);
                }
                if (rightRedAnim == null) {
                    rightRedAnim = (AnimationDrawable) getDrawable(R.drawable.red_right_sound_anim);
                }
                binding.ivLeftAnim.setBackground(leftRedAnim);
                binding.ivRightAnim.setBackground(rightRedAnim);
                leftRedAnim.start();
                rightRedAnim.start();
            }

            @Override
            public void onNoPermission() {
                ActivityCompat.requestPermissions(MessageActivity.this, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            }
        });

        binding.etMessage.requestFocus();

        queryMatchRecord();
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
                binding.rcvMessage.scrollToPosition(adapter.getItemCount() - 1);
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
                binding.rcvMessage.scrollToPosition(adapter.getItemCount() - 1);
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
                binding.rcvMessage.scrollToPosition(adapter.getItemCount() - 1);
                binding.etMessage.setText("");
            }

            @Override
            public void onError(int code, String desc) {
                LogUtil.e("发送失败：" + code + ", desc:" + desc);
            }
        });
    }

    private void getHistoryMessage() {
        V2TIMManager.getMessageManager().getC2CHistoryMessageList(userId, 90, null, new V2TIMValueCallback<List<V2TIMMessage>>() {
            @Override
            public void onSuccess(List<V2TIMMessage> v2TIMMessages) {
                ArrayList<MessageBean> messageBeans = new ArrayList<>();

                Collections.sort(v2TIMMessages, new Comparator<V2TIMMessage>() {
                    @Override
                    public int compare(V2TIMMessage o1, V2TIMMessage o2) {
                        if (o1.getTimestamp() > o2.getTimestamp()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                for (V2TIMMessage v : v2TIMMessages) {
                    MessageBean bean = new MessageBean(v);
                    int msgType = 0;
                    if (v.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
                        if (v.getSender().equals(MyApplication.getInstance().getMid())) {
                            msgType = MessageBean.RIGHT_TXT;
                        } else {
                            msgType = MessageBean.LEFT_TXT;
                        }
                        LogUtil.e("文本消息：" + v.getTextElem().getText() + "----id:" + v.getUserID());
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
                        if (TextUtils.isEmpty(v.getSoundElem().getPath())) {
                            String path = TUIKitConstants.RECORD_DOWNLOAD_DIR + v.getSoundElem().getUUID();
                            File file = new File(path);
                            if (!file.exists()) {
                                v.getSoundElem().downloadSound(path, new V2TIMDownloadCallback() {
                                    @Override
                                    public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                                        LogUtil.e("下载音频：" + progressInfo.getCurrentSize() + " / " + progressInfo.getTotalSize());
                                    }

                                    @Override
                                    public void onSuccess() {
                                        LogUtil.e("下载音频成功");
                                        bean.setMediaPath(path);
                                    }

                                    @Override
                                    public void onError(int code, String desc) {
                                        LogUtil.e("下载音频失败code:" + code + "desc:" + desc);
                                    }
                                });
                            } else {
                                bean.setMediaPath(path);
                            }
                        }
                    } else if (v.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                        msgType = MessageBean.CUSTOM;
                    }
                    bean.setFieldType(msgType);
                    messageBeans.add(bean);
                }
                fillView(messageBeans);
            }

            @Override
            public void onError(int code, String desc) {

            }
        });
    }

    private void fillView(ArrayList<MessageBean> messageBeans) {
        adapter.setNewData(messageBeans);
        binding.rcvMessage.scrollToPosition(adapter.getItemCount() - 1);
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
        Utils.hideSoftKeyboard(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (101 == requestCode) {
            if (data.getExtras().getBoolean("is_clear", false)) {
                getHistoryMessage();
            }
            return;
        }
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == 1000) {
            List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
            if (list == null || list.size() <= 0) {
                return;
            }
            String imagePath;
            LogUtil.e("path:" + list.get(0).getPath());
            if (list.get(0).getPath().contains("content://")) {
                LogUtil.e(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath())));
                imagePath = Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath()));
            } else {
                imagePath = list.get(0).getPath();
            }
            sendImageMessage(imagePath);
        } else if (requestCode == 101) { // 聊天设置页面

        }
    }

    public void pickCamera(View view) {
        Utils.hideSoftKeyboard(this);
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
        Utils.hideSoftKeyboard(this);
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
        Utils.hideSoftKeyboard(this);
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

    public void editMessage(View view) {
        if (binding.llMore.getVisibility() == View.VISIBLE) {
            binding.llMore.setVisibility(View.GONE);
        }
        if (binding.llAudio.getVisibility() == View.VISIBLE) {
            binding.llAudio.setVisibility(View.GONE);
        }
    }

    public void showInviteDialog(View view) {
        InvitePlayDialog dialog = new InvitePlayDialog(this);
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AudioPlayer.getInstance().isPlaying()) {
            AudioPlayer.getInstance().stopPlay();
        }
    }

    /**
     * 查询约战信息
     */
    private void queryMatchRecord() {
        HttpClient.queryMatchRecord(this, userId, new HttpCallback() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}