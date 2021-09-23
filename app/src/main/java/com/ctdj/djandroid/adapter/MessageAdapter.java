package com.ctdj.djandroid.adapter;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.CustomMessageBean;
import com.ctdj.djandroid.bean.MessageBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSoundElem;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.component.AudioPlayer;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.ctdj.djandroid.bean.MessageBean.CUSTOM;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_AUDIO;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_CARD;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_IMAGE;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_TXT;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_AUDIO;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_CARD;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_IMAGE;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_TXT;

public class MessageAdapter extends BaseMultiItemQuickAdapter<MessageBean, BaseViewHolder> {
    private int audioPlayingIndex = -1; // 正在播放的音频消息下标

    public MessageAdapter(List<MessageBean> data) {
        super(data);
        addItemType(LEFT_TXT, R.layout.message_txt_left_item_layout);
        addItemType(RIGHT_TXT, R.layout.message_txt_right_item_layout);
        addItemType(LEFT_IMAGE, R.layout.message_image_left_item_layout);
        addItemType(RIGHT_IMAGE, R.layout.message_image_right_item_layout);
        addItemType(LEFT_AUDIO, R.layout.message_audio_left_item_layout);
        addItemType(RIGHT_AUDIO, R.layout.message_audio_right_item_layout);
        addItemType(CUSTOM, R.layout.message_custom_item_layout);
        addItemType(LEFT_CARD, R.layout.message_game_card_left_item_layout);
        addItemType(RIGHT_CARD, R.layout.message_game_card_right_item_layout);
    }

    @Override
    protected void convert(@NonNull @NotNull BaseViewHolder helper, MessageBean item) {
        switch (helper.getItemViewType()) {
            case LEFT_TXT:
            case RIGHT_TXT:
                Glide.with(mContext).load(item.getV2TIMMessage().getFaceUrl()).error(R.drawable.default_head).into((ImageView) helper.getView(R.id.iv_avatar));
                ((TextView) helper.getView(R.id.tv_content)).setText(item.getV2TIMMessage().getTextElem().getText());
                break;
            case LEFT_IMAGE:
            case RIGHT_IMAGE:
                Glide.with(mContext).load(item.getV2TIMMessage().getFaceUrl()).error(R.drawable.default_head).into((ImageView) helper.getView(R.id.iv_avatar));
                View image = helper.getView(R.id.image);
                if (item.getV2TIMMessage().getImageElem().getImageList().get(0) != null) {
                    int width = item.getV2TIMMessage().getImageElem().getImageList().get(0).getWidth();
                    int height = item.getV2TIMMessage().getImageElem().getImageList().get(0).getHeight();
                    if (width != 0 && height != 0) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                        layoutParams.height = DisplayUtil.dip2px(mContext, 156 * height / width);
                    }

                    Glide.with(mContext).load(item.getV2TIMMessage().getImageElem().getImageList().get(0).getUrl()).
                            error(R.drawable.default_head).into((ImageView) helper.getView(R.id.image));
                    helper.getView(R.id.image).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.previewSingleImage((Activity) mContext, item.getV2TIMMessage().getImageElem().getImageList().get(0).getUrl());
                        }
                    });
                }
                break;
            case LEFT_AUDIO:
            case RIGHT_AUDIO:
                Glide.with(mContext).load(item.getV2TIMMessage().getFaceUrl()).error(R.drawable.default_head).into((ImageView) helper.getView(R.id.iv_avatar));
                V2TIMSoundElem soundElem = item.getV2TIMMessage().getSoundElem();
                ((TextView) helper.getView(R.id.tv_voice_time)).setText(item.getV2TIMMessage().getSoundElem().getDuration() + "s");
                helper.getView(R.id.ll_audio_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSound(helper.getView(R.id.iv_voice), item);
                    }
                });
                break;
            case CUSTOM:
                try {
                    LogUtil.e("自定义消息：" + new String(item.getV2TIMMessage().getCustomElem().getData()));
                    CustomMessageBean bean = new Gson().fromJson(new String(item.getV2TIMMessage().getCustomElem().getData()), CustomMessageBean.class);
                    ((TextView) helper.getView(R.id.tv_content)).setText(bean.getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case LEFT_CARD:
            case RIGHT_CARD:
                try {
                    LogUtil.e("自定义消息：" + new String(item.getV2TIMMessage().getCustomElem().getData()));
                    Glide.with(mContext).load(item.getV2TIMMessage().getFaceUrl()).error(R.drawable.default_head).into((ImageView) helper.getView(R.id.iv_avatar));
                    CustomMessageBean bean = new Gson().fromJson(new String(item.getV2TIMMessage().getCustomElem().getData()), CustomMessageBean.class);
                    ((TextView) helper.getView(R.id.tv_game_nickname)).setText(bean.getGame_nickname());
                    ((TextView) helper.getView(R.id.tv_game_nickname)).setCompoundDrawablesWithIntrinsicBounds(
                            bean.getArea() == 1 ? R.drawable.wx_white_icon : R.drawable.qq_white_icon,
                            0, R.drawable.copy_icon, 0);
                    helper.getView(R.id.tv_game_nickname).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.copy(mContext, bean.getGame_nickname());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void playSound(ImageView audioPlayImage, MessageBean item) {
        if (AudioPlayer.getInstance().isPlaying()) {
            AudioPlayer.getInstance().stopPlay();
            return;
        }
        final String[] soundPath = new String[1];
        if (TextUtils.isEmpty(item.getV2TIMMessage().getSoundElem().getPath())) {
            String path = TUIKitConstants.RECORD_DOWNLOAD_DIR + item.getV2TIMMessage().getSoundElem().getUUID();
            File file = new File(path);
            if (!file.exists()) {
                Utils.showToast(mContext, "语音文件还未下载完成");
                item.getV2TIMMessage().getSoundElem().downloadSound(path, new V2TIMDownloadCallback() {
                    @Override
                    public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                        LogUtil.e("下载音频：" + progressInfo.getCurrentSize() + " / " + progressInfo.getTotalSize());
                    }

                    @Override
                    public void onSuccess() {
                        LogUtil.e("下载音频成功");
                        soundPath[0] = path;
                    }

                    @Override
                    public void onError(int code, String desc) {
                        LogUtil.e("下载音频失败code:" + code + "desc:" + desc);
                    }
                });
                return;
            } else {
                soundPath[0] = path;
            }
        } else {
            soundPath[0] = item.getV2TIMMessage().getSoundElem().getPath();
        }
        if (item.getItemType() == LEFT_AUDIO) {
            audioPlayImage.setImageResource(R.drawable.voice_receive);
        } else {
            audioPlayImage.setImageResource(R.drawable.voice_sent);
        }
        final AnimationDrawable animationDrawable = (AnimationDrawable) audioPlayImage.getDrawable();
        animationDrawable.start();
        AudioPlayer.getInstance().startPlay(soundPath[0], new AudioPlayer.Callback() {
            @Override
            public void onCompletion(Boolean success) {
                audioPlayImage.post(new Runnable() {
                    @Override
                    public void run() {
                        animationDrawable.stop();
                        if (item.getItemType() == LEFT_AUDIO) {
                            audioPlayImage.setImageResource(R.drawable.message_audio_left_3);
                        } else {
                            audioPlayImage.setImageResource(R.drawable.message_audio_right_3);
                        }
                    }
                });
            }
        });
    }

    public void addData(V2TIMMessage v) {
        LogUtil.e("------------------------------------");
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
                msgType = RIGHT_AUDIO;
            } else {
                msgType = LEFT_AUDIO;
            }
        } else if (v.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
            CustomMessageBean bean = new Gson().fromJson(new String(v.getCustomElem().getData()), CustomMessageBean.class);
            if (bean.getType() == 1000) {
                if (v.getSender().equals(MyApplication.getInstance().getMid())) {
                    msgType = RIGHT_CARD;
                } else {
                    msgType = LEFT_CARD;
                }
            } else {
                msgType = CUSTOM;
            }
        }
        super.addData(getItemCount(), new MessageBean(msgType, v));
    }

    public int getAudioPlayingIndex() {
        return audioPlayingIndex;
    }

    public void setAudioPlayingIndex(int audioPlayingIndex) {
        this.audioPlayingIndex = audioPlayingIndex;
    }
}
