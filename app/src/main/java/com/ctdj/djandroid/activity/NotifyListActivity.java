package com.ctdj.djandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.PushRecordBean;
import com.ctdj.djandroid.bean.ScoreListBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityNotifyListBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NotifyListActivity extends BaseActivity {

    ActivityNotifyListBinding binding;
    int type = 1;
    private BaseQuickAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotifyListBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        type = getIntent().getIntExtra("type", 1);
        if (type == 1) {
            binding.titleView.setTitle("赛事通知");
        } else {
            binding.titleView.setTitle("系统通知");
        }
        LinearLayoutCompat.LayoutParams l = (LinearLayoutCompat.LayoutParams) binding.titleView.getLayoutParams();
        l.topMargin = DisplayUtil.getStatusBarHeight(this);
        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        adapter = new BaseQuickAdapter<PushRecordBean.Rows, BaseViewHolder>(R.layout.notify_item_layout, new ArrayList<>()) {

            @Override
            protected void convert(@NonNull BaseViewHolder helper, PushRecordBean.Rows item) {
                ((TextView) helper.getView(R.id.tv_content)).setText(item.getContent());
                ((TextView) helper.getView(R.id.tv_title)).setText(item.getTitle());
                ((TextView) helper.getView(R.id.tv_time)).setText(Utils.getNotifyTime(item.getCreateTime()));
                if (type == 1) {
                    helper.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent(NotifyListActivity.this, PlayDetailActivity.class);
//                            intent.putExtra("orderno", item.get().getOrderno());
//                            intent.putExtra("from", 1);
//                            startActivity(intent);
                        }
                    });
                }
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        HttpClient.queryPushRecordPage(this, type, 1, 20, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                PushRecordBean bean = new Gson().fromJson(result, PushRecordBean.class);
                adapter.setNewData(bean.getRows());
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(NotifyListActivity.this, msg);
            }
        });
    }
}