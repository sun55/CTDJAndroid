package com.ctdj.djandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.AddressBookActivity;
import com.ctdj.djandroid.activity.NotifyListActivity;
import com.ctdj.djandroid.adapter.ConversationAdapter;
import com.ctdj.djandroid.bean.NoticeCustomContent;
import com.ctdj.djandroid.bean.PushRecordBean;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.FragmentMessageBinding;
import com.ctdj.djandroid.dialog.ReceiveInviteDialog;
import com.ctdj.djandroid.event.StopRadarEvent;
import com.ctdj.djandroid.event.UpdatePlayNotifyEvent;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.CircleImageView;
import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMConversationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    FragmentMessageBinding binding;
    ConversationAdapter adapter;
    View playNotifyView; // 赛事通知
    View systemNotifyView; // 系统通知
    CircleImageView playNotifyIcon;
    CircleImageView systemNotifyIcon;
    TextView tvPlayNotifyTitle;
    TextView tvSystemNotifyTitle;
    TextView tvPlayNotifyLastMsg;
    TextView tvSystemNotifyLastMsg;
    TextView tvPlayNotifyTime;
    TextView tvSystemNotifyTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        EventBus.getDefault().register(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        binding.ivContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddressBookActivity.class));
//                V2TIMManager.getInstance().sendC2CTextMessage("ce shi text" + new Random().nextInt(), "100015", new V2TIMValueCallback<V2TIMMessage>() {
//                    @Override
//                    public void onSuccess(V2TIMMessage v2TIMMessage) {
//                        LogUtil.e("发送成功：" + v2TIMMessage.getMessage().getMessageKey());
//
//                    }
//
//                    @Override
//                    public void onError(int code, String desc) {
//                        LogUtil.e("发送失败：" + code + "， msg：" + desc);
//                    }
//                });
            }
        });

        binding.rcvConversation.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new ConversationAdapter(new ArrayList<>());
        binding.rcvConversation.setAdapter(adapter);
        playNotifyView = getLayoutInflater().from(getActivity()).inflate(R.layout.conversation_item_layout, null);
        systemNotifyView = getLayoutInflater().from(getActivity()).inflate(R.layout.conversation_item_layout, null);
        playNotifyIcon = playNotifyView.findViewById(R.id.iv_head);
        systemNotifyIcon = systemNotifyView.findViewById(R.id.iv_head);
        tvPlayNotifyTitle = playNotifyView.findViewById(R.id.tv_name);
        tvSystemNotifyTitle = systemNotifyView.findViewById(R.id.tv_name);
        tvPlayNotifyLastMsg = playNotifyView.findViewById(R.id.tv_last_message);
        tvSystemNotifyLastMsg = systemNotifyView.findViewById(R.id.tv_last_message);
        tvPlayNotifyTime = playNotifyView.findViewById(R.id.tv_time);
        tvSystemNotifyTime = systemNotifyView.findViewById(R.id.tv_time);
        adapter.addHeaderView(playNotifyView);
        adapter.addHeaderView(systemNotifyView);
        adapter.setHeaderAndEmpty(true);
        updateHeadView();
        updatePlayNotifyView();
        V2TIMManager.getConversationManager().setConversationListener(new V2TIMConversationListener() {
            @Override
            public void onSyncServerStart() {
                super.onSyncServerStart();
                LogUtil.e("onSyncServerStart");
            }

            @Override
            public void onSyncServerFinish() {
                super.onSyncServerFinish();
                LogUtil.e("onSyncServerFinish");
            }

            @Override
            public void onSyncServerFailed() {
                super.onSyncServerFailed();
                LogUtil.e("onSyncServerFailed");
            }

            @Override
            public void onNewConversation(List<V2TIMConversation> conversationList) {
                super.onNewConversation(conversationList);
                LogUtil.e("onNewConversation");
                initData();
            }

            @Override
            public void onConversationChanged(List<V2TIMConversation> conversationList) {
                super.onConversationChanged(conversationList);
                LogUtil.e("onConversationChanged");
                initData();
            }

            @Override
            public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
                super.onTotalUnreadMessageCountChanged(totalUnreadCount);
                LogUtil.e("onTotalUnreadMessageCountChanged :" + totalUnreadCount);
                initData();
            }
        });
    }

    private void updateHeadView() {
        playNotifyIcon.setImageResource(R.drawable.play_notify_icon);
        systemNotifyIcon.setImageResource(R.drawable.system_notify_icon);
        tvSystemNotifyTitle.setText("系统通知");
        tvPlayNotifyTitle.setText("赛事通知");
        playNotifyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotifyListActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });

        systemNotifyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotifyListActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        V2TIMManager.getConversationManager().getConversationList(0, 20, new V2TIMValueCallback<V2TIMConversationResult>() {
            @Override
            public void onSuccess(V2TIMConversationResult v2TIMConversationResult) {
                adapter.setNewData(v2TIMConversationResult.getConversationList());
            }

            @Override
            public void onError(int code, String desc) {

            }
        });
        updatePlayNotifyView();
    }

    private void updatePlayNotifyView() {
        HttpClient.queryPushRecordPage(getActivity(), 1, 1, 1, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                PushRecordBean bean = new Gson().fromJson(result, PushRecordBean.class);
                if (bean == null || bean.getRows() == null || bean.getRows().size() > 0) {
                    fillPlayNotifyView(bean.getRows().get(0));
                }
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(getActivity(), msg);
            }
        });
    }

    private void fillPlayNotifyView(PushRecordBean.Rows rows) {
        if (playNotifyView == null) {
            return;
        }
        tvPlayNotifyLastMsg.setText(rows.getContent());
        tvPlayNotifyTime.setText(DateTimeUtil.getConversationTime(Utils.getDateByString(rows.getCreateTime(), "")));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(Object event) {
        if (event instanceof UpdatePlayNotifyEvent) {
            updatePlayNotifyView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
