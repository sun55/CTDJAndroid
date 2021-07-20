package com.ctdj.djandroid.adapter;

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
import com.ctdj.djandroid.bean.MessageBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.component.AudioPlayer;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ctdj.djandroid.bean.MessageBean.LEFT_AUDIO;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_IMAGE;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_TXT;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_AUDIO;
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
    }

    @Override
    protected void convert(@NonNull @NotNull BaseViewHolder helper, MessageBean item) {
        Glide.with(mContext).load(item.getV2TIMMessage().getFaceUrl()).error(R.drawable.default_head).into((ImageView) helper.getView(R.id.iv_avatar));
        switch (helper.getItemViewType()) {
            case LEFT_TXT:
            case RIGHT_TXT:
                ((TextView) helper.getView(R.id.tv_content)).setText(item.getV2TIMMessage().getTextElem().getText());
                break;
            case LEFT_IMAGE:
            case RIGHT_IMAGE:
                View image = helper.getView(R.id.image);
                int width = item.getV2TIMMessage().getImageElem().getImageList().get(0).getWidth();
                int height = item.getV2TIMMessage().getImageElem().getImageList().get(0).getHeight();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                layoutParams.height = DisplayUtil.dip2px(mContext, 156 * height / width);

                Glide.with(mContext).load(item.getV2TIMMessage().getImageElem().getImageList().get(0).getUrl()).
                        error(R.drawable.default_head).into((ImageView) helper.getView(R.id.image));
                break;
            case LEFT_AUDIO:
            case RIGHT_AUDIO:
                ((TextView) helper.getView(R.id.tv_voice_time)).setText(item.getV2TIMMessage().getSoundElem().getDuration() + "s");
                helper.getView(R.id.ll_audio_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSound(helper.getView(R.id.iv_voice), item);
                    }
                });
                break;
        }
    }

    private void playSound(ImageView audioPlayImage, MessageBean item) {
        if (AudioPlayer.getInstance().isPlaying()) {
            AudioPlayer.getInstance().stopPlay();
            return;
        }
        if (TextUtils.isEmpty(item.getV2TIMMessage().getSoundElem().getPath())) {
            Utils.showToast(mContext, "语音文件还未下载完成");
            return;
        }
        if (item.getItemType() == LEFT_AUDIO) {
            audioPlayImage.setImageResource(R.drawable.voice_receive);
        } else {
            audioPlayImage.setImageResource(R.drawable.voice_sent);
        }
        final AnimationDrawable animationDrawable = (AnimationDrawable) audioPlayImage.getDrawable();
        animationDrawable.start();
        AudioPlayer.getInstance().startPlay(item.getV2TIMMessage().getSoundElem().getPath(), new AudioPlayer.Callback() {
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
