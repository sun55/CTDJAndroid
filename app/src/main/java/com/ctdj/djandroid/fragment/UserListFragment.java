package com.ctdj.djandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.FollowUserAdapter;
import com.ctdj.djandroid.bean.UserListBean;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.FragmentUserListBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;

public class UserListFragment extends Fragment {
    public static final int FOLLOW = 1;
    public static final int FANS = 2;
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
        View empty = getLayoutInflater().inflate(R.layout.empty_layout, null);
        TextView emptyTitle = empty.findViewById(R.id.tv_empty_title);
        if (type == FOLLOW) {
            emptyTitle.setText("???????????????????????????");
        } else {
            emptyTitle.setText("???????????????????????????");
        }
        adapter.setEmptyView(empty);

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
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UserListBean.Rows item = (UserListBean.Rows) adapter.getItem(position);
                if (type == FOLLOW) {
                    deleteFollow(item.getFmid(), position);
                } else {
                    if (item.getFtype() == 1) {
                        deleteFollow(item.getMid(), position);
                    } else {
                        follow(item.getMid());
                    }
                }
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
        HttpClient.followList(getActivity(), type, page, 10, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                UserListBean bean = new Gson().fromJson(result, UserListBean.class);
                if (isRefresh) {
                    binding.refresh.finishRefresh();
                    adapter.setNewData(bean.getRows());
                } else {
                    if (bean.getRows().size() > 0) {
                        binding.refresh.finishLoadMore();
                        adapter.addData(bean.getRows());
                    } else {
                        binding.refresh.finishLoadMoreWithNoMoreData();
                    }
                }
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
                    adapter.remove(position);
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
