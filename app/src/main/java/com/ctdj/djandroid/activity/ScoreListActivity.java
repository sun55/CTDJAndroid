package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.ScoreListBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityScoreListBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

public class ScoreListActivity extends BaseActivity {

    ActivityScoreListBinding binding;
    BaseQuickAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScoreListBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int competitionId = getIntent().getIntExtra("competitionId", 0);
        HttpClient.queryScoreList(this, competitionId, 1, 30, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                ScoreListBean bean = new Gson().fromJson(result, ScoreListBean.class);
                if (bean.getData().getRows().size() == 0) {
                    Utils.showToast(ScoreListActivity.this, "积分列表为空");
                } else {
                    adapter = new BaseQuickAdapter<ScoreListBean.Data.Rows, BaseViewHolder>(R.layout.score_item_layout, bean.getData().getRows()) {

                        @Override
                        protected void convert(@NonNull BaseViewHolder helper, ScoreListBean.Data.Rows item) {
                            ((TextView)helper.getView(R.id.tv_type_status)).setText(item.getRemark());
                            ((TextView)helper.getView(R.id.tv_value)).setText("+" + item.getScore());
                            ((TextView)helper.getView(R.id.tv_create_time)).setText(item.getCreateTime());
                        }
                    };
                    binding.recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(ScoreListActivity.this, msg);
            }
        });
    }
}