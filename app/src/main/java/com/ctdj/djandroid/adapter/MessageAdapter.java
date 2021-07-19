package com.ctdj.djandroid.adapter;

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
import com.tencent.imsdk.v2.V2TIMMessage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ctdj.djandroid.bean.MessageBean.LEFT_AUDIO;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_IMAGE;
import static com.ctdj.djandroid.bean.MessageBean.LEFT_TXT;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_AUDIO;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_IMAGE;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_TXT;

public class MessageAdapter extends BaseMultiItemQuickAdapter<MessageBean, BaseViewHolder> {

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
                LogUtil.e("图片消息:" + item.getV2TIMMessage().getImageElem().getImageList().get(0).getUrl());
                LogUtil.e("图片消息:" + item.getV2TIMMessage().getImageElem().getImageList().get(0).getWidth());
                LogUtil.e("图片消息:" + item.getV2TIMMessage().getImageElem().getImageList().get(0).getHeight());
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
                break;
        }
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
        super.addData(0, new MessageBean(msgType, v));
    }
}
