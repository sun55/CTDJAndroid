package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.bean.WeekCompetitionBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityWeekCompetitionBinding;
import com.ctdj.djandroid.dialog.PlayRewardDialog;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

public class WeekCompetitionActivity extends BaseActivity {

    ActivityWeekCompetitionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeekCompetitionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initView();
        initData();
    }

    private void initView() {
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

        Utils.setTextTypeface(binding.tvCurrentScore, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvCurrentRank, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvReward1, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvReward2, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvReward3, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvReward4, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvReward5, "fonts/avantibold.ttf");
    }

    private void initData() {
        HttpClient.intoWeekCompetition(this, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                WeekCompetitionBean bean = new Gson().fromJson(result, WeekCompetitionBean.class);
                fillView(bean);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(WeekCompetitionActivity.this, msg);
            }
        });
    }

    private void fillView(WeekCompetitionBean bean) {
        if (bean.getData().getLogEntryCompetition() == null) {
            binding.ivPlaying.setVisibility(View.GONE);
            binding.tvFreeJoin.setVisibility(View.VISIBLE);
            binding.viewFreeJoin.setVisibility(View.VISIBLE);
            binding.llScore.setVisibility(View.GONE);
            binding.tvJoinTips.setVisibility(View.VISIBLE);
            binding.btnJoin.setVisibility(View.VISIBLE);
        } else {
            binding.ivPlaying.setVisibility(View.VISIBLE);
            binding.tvFreeJoin.setVisibility(View.GONE);
            binding.viewFreeJoin.setVisibility(View.GONE);
            binding.llScore.setVisibility(View.VISIBLE);
            binding.tvJoinTips.setVisibility(View.GONE);
            binding.btnJoin.setVisibility(View.GONE);
            binding.tvCurrentRank.setText(bean.getData().getLogEntryCompetition().getRank() + "");
            binding.tvCurrentScore.setText(bean.getData().getLogEntryCompetition().getScore() + "");
        }

        binding.tvStartTime.setText(bean.getData().getWeekCompetition().getBeginTime());
        binding.tvEndTime.setText(bean.getData().getWeekCompetition().getEndTime());
        binding.tvReward1.setText(bean.getData().getWeekCompetition().getNo1() + "");
        binding.tvReward2.setText(bean.getData().getWeekCompetition().getNo2() + "");
        binding.tvReward3.setText(bean.getData().getWeekCompetition().getNo3() + "");
        binding.tvReward4.setText(bean.getData().getWeekCompetition().getNo4() + "");
        binding.tvReward5.setText(bean.getData().getWeekCompetition().getNo5() + "");
        binding.tvAllReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayRewardDialog dialog = new PlayRewardDialog(WeekCompetitionActivity.this);
                dialog.show();
                dialog.fillData(
                        bean.getData().getWeekCompetition().getNo1(),
                        bean.getData().getWeekCompetition().getNo2(),
                        bean.getData().getWeekCompetition().getNo3(),
                        bean.getData().getWeekCompetition().getNo4(),
                        bean.getData().getWeekCompetition().getNo5(),
                        bean.getData().getWeekCompetition().getNo6to10(),
                        bean.getData().getWeekCompetition().getNo11to20(),
                        bean.getData().getWeekCompetition().getNo21to50(),
                        bean.getData().getWeekCompetition().getNo51to80(),
                        bean.getData().getWeekCompetition().getNo81to100());
            }
        });
    }
}