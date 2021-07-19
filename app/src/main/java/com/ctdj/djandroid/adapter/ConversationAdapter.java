package com.ctdj.djandroid.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.MessageActivity;
import com.ctdj.djandroid.view.CircleImageView;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;

import java.util.Date;
import java.util.List;

public class ConversationAdapter extends BaseQuickAdapter<V2TIMConversation, ConversationAdapter.ConversationHolder> {

    public ConversationAdapter(List<V2TIMConversation> data) {
        super(R.layout.conversation_item_layout, data);
    }

    @Override
    protected void convert(@NonNull ConversationAdapter.ConversationHolder helper, V2TIMConversation item) {
        Glide.with(mContext).load(item.getFaceUrl()).error(R.drawable.default_head).into(helper.ivHead);
        V2TIMMessage message = item.getLastMessage();
        helper.tvName.setText(item.getShowName());
        String txt = "";
        if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
            txt = message.getTextElem().getText();
        } else if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {
            txt = "[图片]";
        } else if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {
            txt = "[语音]";
        } else if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {
            txt = "[视频]";
        }
        helper.tvLastMsg.setText(txt);
        helper.tvUnReadCount.setVisibility(item.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
        helper.tvUnReadCount.setText(item.getUnreadCount() + "");
        helper.tvTime.setText(DateTimeUtil.getTimeFormatText(new Date(message.getTimestamp() * 1000)));
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("target_name", item.getShowName());
                intent.putExtra("last_message", message);
                intent.putExtra("user_id", item.getUserID());
                mContext.startActivity(intent);
                if (item.getUnreadCount() > 0) {
                    V2TIMManager.getMessageManager().markC2CMessageAsRead(item.getUserID(), new V2TIMCallback() {
                        @Override
                        public void onSuccess() {
                            helper.tvUnReadCount.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(int code, String desc) {

                        }
                    });
                }
            }
        });
    }

    public class ConversationHolder extends BaseViewHolder {

        CircleImageView ivHead;
        TextView tvName;
        TextView tvLastMsg;
        TextView tvTime;
        TextView tvUnReadCount;

        public ConversationHolder(View view) {
            super(view);
            ivHead = view.findViewById(R.id.iv_head);
            tvName = view.findViewById(R.id.tv_name);
            tvLastMsg = view.findViewById(R.id.tv_last_message);
            tvTime = view.findViewById(R.id.tv_time);
            tvUnReadCount = view.findViewById(R.id.tv_un_read);
        }
    }
}
