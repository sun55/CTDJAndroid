package com.ctdj.djandroid.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.MessageBean;
import com.tencent.imsdk.v2.V2TIMMessage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ctdj.djandroid.bean.MessageBean.LEFT_TXT;
import static com.ctdj.djandroid.bean.MessageBean.RIGHT_TXT;

public class MessageAdapter extends BaseMultiItemQuickAdapter<MessageBean, BaseViewHolder> {

    public MessageAdapter(List<MessageBean> data) {
        super(data);
        addItemType(LEFT_TXT, R.layout.message_txt_left_item_layout);
        addItemType(RIGHT_TXT, R.layout.message_txt_right_item_layout);
    }

    @Override
    protected void convert(@NonNull @NotNull BaseViewHolder helper, MessageBean item) {
        switch (helper.getItemViewType()) {
            case LEFT_TXT:
                Glide.with(mContext).load(item.getV2TIMMessage().getFaceUrl()).error(R.drawable.default_head).into((ImageView) helper.getView(R.id.iv_avatar));
                ((TextView) helper.getView(R.id.tv_content)).setText(item.getV2TIMMessage().getTextElem().getText());
                break;
            case RIGHT_TXT:
                Glide.with(mContext).load(item.getV2TIMMessage().getFaceUrl()).error(R.drawable.default_head).into((ImageView) helper.getView(R.id.iv_avatar));
                ((TextView) helper.getView(R.id.tv_content)).setText(item.getV2TIMMessage().getTextElem().getText());
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
        }
        super.addData(0, new MessageBean(msgType, v));
    }
}
