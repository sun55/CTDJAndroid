package com.ctdj.djandroid.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.MessageActivity;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.view.CircleImageView;
import com.google.android.exoplayer2.C;
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
        if (message != null) {
            if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
                txt = message.getTextElem().getText();
            } else if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {
                txt = "[图片]";
            } else if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {
                txt = "[语音]";
            } else if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {
                txt = "[视频]";
            } else if (message.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                txt = "[系统消息]";
            }
            helper.tvLastMsg.setText(txt);
            helper.tvUnReadCount.setVisibility(item.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
            helper.tvUnReadCount.setText(item.getUnreadCount() + "");
            helper.tvTime.setText(DateTimeUtil.getConversationTime(new Date(message.getTimestamp() * 1000)));
            helper.ivTop.setVisibility(item.isPinned() ? View.VISIBLE : View.GONE);
        }
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("target_name", item.getShowName());
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
        helper.tvToTop.setText(item.isPinned() ? "取消置顶" : "置顶");
        helper.tvToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e(item.isPinned() ? "取消置顶" : "置顶");
                helper.popupWindow.dismiss();
                V2TIMManager.getConversationManager().pinConversation(item.getConversationID(), !item.isPinned(), new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        Utils.showToast(mContext, item.isPinned() ? "取消置顶成功" : "置顶成功");
                    }

                    @Override
                    public void onError(int code, String desc) {
                        LogUtil.e("pinConversation code:" + code + ",desc:" + desc);
                    }
                });
            }
        });
        helper.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("删除");
                helper.popupWindow.dismiss();
                V2TIMManager.getConversationManager().deleteConversation(item.getConversationID(), new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        Utils.showToast(mContext, "删除成功");
                        remove(helper.getAdapterPosition());
                    }

                    @Override
                    public void onError(int code, String desc) {

                    }
                });
            }
        });
        helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                int[] location = new int[2];
//                v.getLocationInWindow(location);
//                if (DisplayUtil.getScreenHightPx(mContext) - DisplayUtil.getNavigationBarHeight(mContext) - location[1] < DisplayUtil.dip2px(mContext, 80)) {
//                    helper.popupWindow.showAsDropDown(v, DisplayUtil.getScreenWidthPx(mContext) / 4 - (int) DisplayUtil.dip2px(mContext, 40), -v.getHeight() - (int) DisplayUtil.dip2px(mContext, 80));
//                } else {
////                popupView.showAsDropDown(v, MyUtils.getScreenWidth(context) / 2 - (int) MyUtils.dip2px(context, 40), 0);
//                    helper.popupWindow.showAsDropDown(v, DisplayUtil.getScreenWidthPx(mContext) / 4 - (int) DisplayUtil.dip2px(mContext, 40), -v.getHeight());
//                }
                helper.popupWindow.showAsDropDown(v, 0, -v.getHeight() - DisplayUtil.dip2px(mContext, 31));
                return true;
            }
        });
    }

    public class ConversationHolder extends BaseViewHolder {

        CircleImageView ivHead;
        TextView tvName;
        TextView tvLastMsg;
        TextView tvTime;
        TextView tvUnReadCount;
        ImageView ivTop;
        PopupWindow popupWindow;
        View popView;
        TextView tvToTop;
        TextView tvDelete;

        public ConversationHolder(View view) {
            super(view);
            ivHead = view.findViewById(R.id.iv_head);
            tvName = view.findViewById(R.id.tv_name);
            tvLastMsg = view.findViewById(R.id.tv_last_message);
            tvTime = view.findViewById(R.id.tv_time);
            tvUnReadCount = view.findViewById(R.id.tv_un_read);
            ivTop = view.findViewById(R.id.iv_top);

            popView = LayoutInflater.from(mContext).inflate(R.layout.message_pop_view, (ViewGroup) view, false);
            ViewGroup.LayoutParams layoutParams = popView.getLayoutParams();
            layoutParams.width = DisplayUtil.getScreenWidthPx(mContext);
            popView.setMinimumWidth(DisplayUtil.getScreenWidthPx(mContext));
            tvToTop = popView.findViewById(R.id.tv_to_top);
            tvDelete = popView.findViewById(R.id.tv_delete);
            popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        }
    }
}
