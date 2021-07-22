package com.ctdj.djandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.FollowUserAdapter;
import com.ctdj.djandroid.bean.UserListBean;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.FragmentUserListBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;

public class UserListFragment extends Fragment {
    public static final int FOLLOW = 2;
    public static final int FANS = 1;
    private int type = 1;
     private int page = 1;
    FragmentUserListBinding binding;

    FollowUserAdapter adapter;

    public UserListFragment(int type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        binding.rcvUserList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FollowUserAdapter(new ArrayList<>(), type);
        binding.rcvUserList.setAdapter(adapter);
        adapter.setEmptyView(getLayoutInflater().inflate(R.layout.empty_layout, null));

        binding.refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getData(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(true);
            }
        });
        getData(true);
    }

    private void getData(boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            page++;
        }
        HttpClient.followList(getActivity(), page, 10, type == FOLLOW ? 1 : 0, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                UserListBean bean = new Gson().fromJson(result, UserListBean.class);
//                if (isRefresh) {
//                    binding.refresh.finishRefresh();
//                    adapter.setNewData(bean.getData().getList());
//                } else {
//                    if (bean.getData().getList().size() > 0) {
//                        binding.refresh.finishLoadMore();
//                        adapter.addData(bean.getData().getList());
//                    } else {
//                        binding.refresh.finishLoadMoreWithNoMoreData();
//                    }
//                }
            }

            @Override
            public void onFailure(String msg) {
                if (isRefresh) {
                    binding.refresh.finishRefresh();
                } else {
                    binding.refresh.finishLoadMore(false);
                }
            }
        });
    }

    private void follow(String fmno) {
        HttpClient.addFollow(getActivity(), fmno, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                getData(true);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(getActivity(), msg);
            }
        });
    }

    private void deleteFollow(String fmid, int position) {
        HttpClient.deleteFollow(getActivity(), fmid, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                if (type == FOLLOW) {
//                    adapter.remove(position);
                } else {
                    getData(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(getActivity(), msg);
            }
        });
    }
}
