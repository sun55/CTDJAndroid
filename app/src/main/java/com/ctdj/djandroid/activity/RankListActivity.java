package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.RankListBean;
import com.ctdj.djandroid.bean.ScoreListBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityRankListBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

public class RankListActivity extends BaseActivity {

    ActivityRankListBinding binding;
    CountDownTimer countDownTimer;
    RankListBean bean;
    BaseQuickAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRankListBinding.inflate(LayoutInflater.from(this));
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
        HttpClient.queryRankList(this, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                bean = new Gson().fromJson(result, RankListBean.class);
                fillView();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(RankListActivity.this, msg);
            }
        });
    }

    private void fillView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<RankListBean.Data, BaseViewHolder>(R.layout.rank_item_layout, bean.getData()) {

            @Override
            protected void convert(@NonNull BaseViewHolder helper, RankListBean.Data item) {
                Utils.setTextTypeface(((TextView) helper.getView(R.id.tv_value)), "fonts/avantibold.ttf");
                Utils.setTextTypeface(((TextView) helper.getView(R.id.tv_rank)), "fonts/avantibold.ttf");
                ((TextView) helper.getView(R.id.tv_value)).setText(item.getScore() + "");
                ((TextView) helper.getView(R.id.tv_nickname)).setText(item.getMname());
                ((ImageView) helper.getView(R.id.iv_gender)).setBackgroundResource(item.getSex() == 1 ? R.drawable.male_small_icon : R.drawable.female_small_icon);
                Glide.with(RankListActivity.this).load(item.getHeadimg()).into((ImageView) helper.getView(R.id.avatar));
                switch (helper.getAdapterPosition()) {
                    case 0:
                        ((ImageView) helper.getView(R.id.iv_rank)).setBackgroundResource(R.drawable.week_competition_icon_4);
                        break;
                    case 1:
                        ((ImageView) helper.getView(R.id.iv_rank)).setBackgroundResource(R.drawable.week_competition_icon_5);
                        break;
                    case 2:
                        ((ImageView) helper.getView(R.id.iv_rank)).setBackgroundResource(R.drawable.week_competition_icon_6);
                        break;
                    case 3:
                        ((ImageView) helper.getView(R.id.iv_rank)).setBackgroundResource(R.drawable.week_competition_icon_7);
                        break;
                    case 4:
                        ((ImageView) helper.getView(R.id.iv_rank)).setBackgroundResource(R.drawable.week_competition_icon_8);
                        break;
                    default:
                        ((TextView) helper.getView(R.id.tv_rank)).setText((helper.getAdapterPosition() + 1) + "");
                        break;
                }
            }
        };
        for (RankListBean.Data d : bean.getData()) {
            if (d.getMid().equals(MyApplication.getInstance().getMid())) {
                Utils.setTextTypeface(binding.tvValue, "fonts/avantibold.ttf");
                Utils.setTextTypeface(binding.tvRank, "fonts/avantibold.ttf");
                binding.tvValue.setText(d.getScore() + "");
                binding.tvNickname.setText(d.getMname());
                binding.ivGender.setBackgroundResource(d.getSex() == 1 ? R.drawable.male_small_icon : R.drawable.female_small_icon);
                Glide.with(RankListActivity.this).load(d.getHeadimg()).into(binding.avatar);
                switch (d.getRanks()) {
                    case 1:
                        binding.ivRank.setBackgroundResource(R.drawable.week_competition_icon_4);
                        break;
                    case 2:
                        binding.ivRank.setBackgroundResource(R.drawable.week_competition_icon_5);
                        break;
                    case 3:
                        binding.ivRank.setBackgroundResource(R.drawable.week_competition_icon_6);
                        break;
                    case 4:
                        binding.ivRank.setBackgroundResource(R.drawable.week_competition_icon_7);
                        break;
                    case 5:
                        binding.ivRank.setBackgroundResource(R.drawable.week_competition_icon_8);
                        break;
                    default:
                        binding.tvRank.setText("--");
                        break;
                }
            }
        }
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String endTime = getIntent().getStringExtra("end_time");
        long leftTime = Utils.getLeftTime(endTime);
        binding.tvCountDown.setText(Utils.getRankCountDown(leftTime));
        countDownTimer = new CountDownTimer(leftTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvCountDown.setText(Utils.getRankCountDown(millisUntilFinished));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer == null) {
            return;
        }
        countDownTimer.cancel();
        countDownTimer = null;
    }
}