package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.MessageAdapter;
import com.ctdj.djandroid.audio.AudioRecorderButton;
import com.ctdj.djandroid.bean.CustomMessageBean;
import com.ctdj.djandroid.bean.MatchOrderBean;
import com.ctdj.djandroid.bean.MessageBean;
import com.ctdj.djandroid.bean.UploadBean;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMessageBinding;
import com.ctdj.djandroid.dialog.InvitePlayDialog;
import com.ctdj.djandroid.dialog.PlayMoreBottomDialog;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private static int SEND_IMAGE_MESSAGE = 1000;
    private static int UPLOAD_IMAGE_FOR_GAME = 1001;

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
        binding.etMessage.requestFocus();
        binding.llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomViews();
                Utils.showSoftKeyboard(MessageActivity.this, binding.etMessage);
                binding.etMessage.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.rcvMessage.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 200);
            }
        });
        adapter = new MessageAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rcvMessage.setLayoutManager(manager);
        binding.rcvMessage.setAdapter(adapter);
        binding.titleView.setTitle(targetName);
        binding.rcvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideBottomViews();
                Utils.hideSoftKeyboard(MessageActivity.this);
                return false;
            }
        });
        if (lastMessage == null || userId == null) {

        } else {
            getHistoryMessage();
        }
        addAdvancedMsgListener();

        setAudioRecordStateListener();

        queryMatchRecord();
    }

    private void setAudioRecordStateListener() {
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
    }

    V2TIMAdvancedMsgListener v2TIMAdvancedMsgListener;

    private void addAdvancedMsgListener() {
        if (v2TIMAdvancedMsgListener == null) {
            v2TIMAdvancedMsgListener = new V2TIMAdvancedMsgListener() {
                @Override
                public void onRecvNewMessage(V2TIMMessage msg) {
                    super.onRecvNewMessage(msg);
                    if (!userId.equals(msg.getUserID())) {
                        return;
                    }
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
                        queryMatchRecord();
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
            };
        }
        V2TIMManager.getMessageManager().addAdvancedMsgListener(v2TIMAdvancedMsgListener);
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
                Utils.showToast(MessageActivity.this, "发送图片消息失败 code：" + code + "， desc" + desc);
            }
        });
    }

    private void uploadImageForGame(String filePath) {
        Utils.showLoadingDialog(this);
        HttpClient.uploadImage(this, filePath, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                UploadBean bean = new Gson().fromJson(result, UploadBean.class);
                HttpClient.submitAudit(MessageActivity.this, matchOrderBean.getData().getChallengeId(), 2, bean.url, "", new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Utils.hideLoadingDialog();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.hideLoadingDialog();
                        Utils.showToast(MessageActivity.this, msg);
                    }
                });
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(MessageActivity.this, msg);
                Utils.hideLoadingDialog();
            }
        });
    }

    private void fillView(ArrayList<MessageBean> messageBeans) {
        adapter.setNewData(messageBeans);
        binding.rcvMessage.scrollToPosition(adapter.getItemCount() - 1);
    }

    public void playStatus2Click(View view) {
        Utils.hideSoftKeyboard(this);

        switch (matchOrderBean.getData().getSta()) {
            case 0: // 立即应战
                showInviteDialog(null);
                break;
            case 1: // 上传截图
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
                        .isCamera(false)
                        .imageEngine(GlideEngine.createGlideEngine())
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.SINGLE)
                        .freeStyleCropEnabled(true)
                        .isDragFrame(false)
                        .maxSelectNum(1)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .synOrAsy(true)
                        .forResult(UPLOAD_IMAGE_FOR_GAME);
                break;
            case 2: // 查看赛果截图
            case 4: // 查看申诉截图
                if (TextUtils.isEmpty(matchOrderBean.getData().getGameimg())) {
                    Utils.showToast(MessageActivity.this, "赛果截图数据为空");
                    return;
                }
                Utils.previewSingleImage(MessageActivity.this, matchOrderBean.getData().getGameimg());
                break;
//                if (TextUtils.isEmpty(matchOrderBean.getData().getAppealimg())) {
//                    Utils.showToast(MessageActivity.this, "申诉截图数据为空");
//                    return;
//                }
//                Utils.previewSingleImage(MessageActivity.this, matchOrderBean.getData().getAppealimg());
//                break;
        }
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
                .forResult(SEND_IMAGE_MESSAGE);
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
        if (requestCode == SEND_IMAGE_MESSAGE) {
            sendImageMessage(imagePath);
        } else if (requestCode == UPLOAD_IMAGE_FOR_GAME) {
            uploadImageForGame(imagePath);
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
                .forResult(SEND_IMAGE_MESSAGE);
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
        if (matchOrderBean != null && matchOrderBean.getData().getIsdefier() == 1) {
            if (matchOrderBean.getData().getSta() == 0 || matchOrderBean.getData().getSta() == 1) {
                Utils.showToast(this, "您和对方还有未完成的订单");
                return;
            }
        }
        if (matchOrderBean == null || matchOrderBean.getData() == null || TextUtils.isEmpty(matchOrderBean.getData().getOrderno())
                || matchOrderBean.getData().getSta() == 2 || matchOrderBean.getData().getSta() == 3 || matchOrderBean.getData().getSta() == 4
                || matchOrderBean.getData().getSta() == 5 || matchOrderBean.getData().getSta() == 6
                || matchOrderBean.getData().getSta() == 99) {
            InvitePlayDialog dialog = new InvitePlayDialog(this, userId, "", 0, 1, 1, 0);
            dialog.show();
        } else {
            InvitePlayDialog dialog = new InvitePlayDialog(this, userId, matchOrderBean.getData().getOrderno(), matchOrderBean.getData().getAward(), matchOrderBean.getData().getChallengeType(), matchOrderBean.getData().getArea(), matchOrderBean.getData().getChallengeId());
            dialog.show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AudioPlayer.getInstance().isPlaying()) {
            AudioPlayer.getInstance().stopPlay();
        }
    }

    MatchOrderBean matchOrderBean;

    /**
     * 查询约战信息
     */
    private void queryMatchRecord() {
        HttpClient.queryMatchRecord(this, userId, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                matchOrderBean = new Gson().fromJson(result, MatchOrderBean.class);
                fillOrderView();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(MessageActivity.this, msg);
            }
        });
    }

    private void fillOrderView() {
        if (matchOrderBean.getData().getGameName() == null || matchOrderBean.getData().getSta() == 5 || matchOrderBean.getData().getSta() == 6 || matchOrderBean.getData().getSta() == 99) {
            binding.rlPlayInfo.setVisibility(View.GONE);
        } else {
            binding.rlPlayInfo.setVisibility(View.VISIBLE);
        }
        binding.tvPlayType.setText(matchOrderBean.getData().getChallengeType() == 1 ? "金币挑战赛" : "赏金挑战赛");
        binding.tvPlayPrice.setText((matchOrderBean.getData().getSta() == 0 ? matchOrderBean.getData().getAward() : matchOrderBean.getData().getAward() / 2) + "");
        binding.tvPlayPrice.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                matchOrderBean.getData().getChallengeType() == 1 ? R.drawable.gold_icon_2 : R.drawable.diamond_icon,
                0);
        switch (matchOrderBean.getData().getSta()) {
            case 0:
                if (matchOrderBean.getData().getIsdefier() == 1) {
                    binding.tvPlayStatus.setText("约战中");
                    binding.tvPlayStatus2.setEnabled(false);
                    binding.tvPlayStatus2.setText("等待应战");
                    binding.tvPlayStatus2.setAlpha(0.5f);
                    binding.ivPlayInfoMore.setVisibility(View.VISIBLE);
                } else {
                    binding.tvPlayStatus.setText("去应战");
                    binding.tvPlayStatus2.setEnabled(true);
                    binding.tvPlayStatus2.setText("立即应战");
                    binding.tvPlayStatus2.setAlpha(1f);
                    binding.ivPlayInfoMore.setVisibility(View.GONE);
                }
                break;
            case 1:
                binding.tvPlayStatus2.setEnabled(true);
                binding.tvPlayStatus.setText("进行中");
                binding.tvPlayStatus2.setText("上传截图");
                binding.tvPlayStatus2.setAlpha(1f);
                binding.ivPlayInfoMore.setVisibility(View.GONE);
                break;
            case 2:
                binding.tvPlayStatus2.setEnabled(true);
                binding.tvPlayStatus.setText("审核中");
                binding.tvPlayStatus2.setText("查看截图");
                binding.tvPlayStatus2.setAlpha(1f);
                binding.ivPlayInfoMore.setVisibility(MyApplication.getInstance().getMid().equals(matchOrderBean.getData().getUpdateby()) ? View.GONE : View.VISIBLE);
                break;
            case 4:
                binding.tvPlayStatus2.setEnabled(true);
                binding.tvPlayStatus.setText("申诉中");
                binding.tvPlayStatus2.setText("查看截图");
                binding.ivPlayInfoMore.setVisibility(View.VISIBLE);
                break;
        }
        binding.ivPlayInfoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMoreBottomDialog bottomDialog = new PlayMoreBottomDialog(MessageActivity.this);
                bottomDialog.show();
                bottomDialog.fillData(matchOrderBean);
            }
        });
        binding.rlPlayInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, PlayDetailActivity.class);
                intent.putExtra("orderno", matchOrderBean.getData().getOrderno());
                intent.putExtra("from", 1);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (v2TIMAdvancedMsgListener != null) {
            V2TIMManager.getMessageManager().removeAdvancedMsgListener(v2TIMAdvancedMsgListener);
        }
    }
}