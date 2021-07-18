package com.ctdj.djandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctdj.djandroid.adapter.ConversationAdapter;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.databinding.FragmentMessageBinding;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMConversationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMValueCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageFragment extends Fragment {
    FragmentMessageBinding binding;
    ConversationAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        binding.ivContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                V2TIMManager.getInstance().sendC2CTextMessage("ce shi text" + new Random().nextInt(), "100014", new V2TIMValueCallback<V2TIMMessage>() {
                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        LogUtil.e("发送成功：" + v2TIMMessage.getMessage().getMessageKey());

                    }

                    @Override
                    public void onError(int code, String desc) {
                        LogUtil.e("发送失败：" + code + "， msg：" + desc);
                    }
                });
            }
        });

        binding.rcvConversation.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new ConversationAdapter(new ArrayList<>());
        binding.rcvConversation.setAdapter(adapter);
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
            }
        });
    }

    private void initData() {
        V2TIMManager.getConversationManager().getConversationList(0, 20, new V2TIMValueCallback<V2TIMConversationResult>() {
            @Override
            public void onSuccess(V2TIMConversationResult v2TIMConversationResult) {
                LogUtil.e("conversation size:" + v2TIMConversationResult.getConversationList().size());
                adapter.setNewData(v2TIMConversationResult.getConversationList());
            }

            @Override
            public void onError(int code, String desc) {

            }
        });
    }

}
