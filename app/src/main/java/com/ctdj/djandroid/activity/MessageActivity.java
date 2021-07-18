package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.adapter.MessageAdapter;
import com.ctdj.djandroid.bean.MessageBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityMessageBinding;
import com.ctdj.djandroid.view.TitleView;
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageReceipt;
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

    private void getHistoryMessage() {
        V2TIMManager.getMessageManager().getC2CHistoryMessageList(userId, 20, lastMessage, new V2TIMValueCallback<List<V2TIMMessage>>() {
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

//    private void possiblyResizeChildOfContent() {
//        int usableHeightNow = computeUsableHeight();
//        if (usableHeightNow != usableHeightPrevious) {
//            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
//            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
//            if (heightDifference > (usableHeightSansKeyboard/4)) {
//                // keyboard probably just became visible
//                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//            } else {
//                // keyboard probably just became hidden
//                frameLayoutParams.height = usableHeightSansKeyboard;
//            }
//            mChildOfContent.requestLayout();
//            usableHeightPrevious = usableHeightNow;
//        }
//    }
//
//    private int computeUsableHeight() {
//        Rect r = new Rect();
//        mChildOfContent.getWindowVisibleDisplayFrame(r);
//        return (r.bottom - r.top);
//    }

}