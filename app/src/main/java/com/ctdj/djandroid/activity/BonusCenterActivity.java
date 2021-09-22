package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityBonusCenterBinding;
import com.ctdj.djandroid.view.TitleView;

/**
 * 领奖中心
 */
public class BonusCenterActivity extends BaseActivity {

    ActivityBonusCenterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBonusCenterBinding.inflate(LayoutInflater.from(this));
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
        Utils.setTextTypeface(binding.tvBalance1, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvCurrentBonus, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvItem1, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvItem2, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvItem3, "fonts/avantibold.ttf");
        Utils.setTextTypeface(binding.tvItem4, "fonts/avantibold.ttf");

    }

    public void selectTopClick(View view) {
        if (view.getId() == R.id.rl_1) {
            binding.rl1.setBackgroundResource(R.drawable.radius_10_5252fe);
            binding.rl2.setBackgroundResource(R.drawable.radius_10_22252f);
            binding.iv1.setVisibility(View.VISIBLE);
            binding.iv2.setVisibility(View.GONE);
        } else {
            binding.rl1.setBackgroundResource(R.drawable.radius_10_22252f);
            binding.rl2.setBackgroundResource(R.drawable.radius_10_5252fe);
            binding.iv1.setVisibility(View.GONE);
            binding.iv2.setVisibility(View.VISIBLE);
        }
    }
    public void selectBonusClick(View view) {
        if (view.getId() == R.id.rl_item_1) {
            binding.rlItem1.setBackgroundResource(R.drawable.play_item_select_bg);
            binding.rlItem2.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem3.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem4.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.ivItem1.setVisibility(View.VISIBLE);
            binding.ivItem2.setVisibility(View.GONE);
            binding.ivItem3.setVisibility(View.GONE);
            binding.ivItem4.setVisibility(View.GONE);
        } else if (view.getId() == R.id.rl_item_2) {
            binding.rlItem1.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem2.setBackgroundResource(R.drawable.play_item_select_bg);
            binding.rlItem3.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem4.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.ivItem1.setVisibility(View.GONE);
            binding.ivItem2.setVisibility(View.VISIBLE);
            binding.ivItem3.setVisibility(View.GONE);
            binding.ivItem4.setVisibility(View.GONE);
        } else if (view.getId() == R.id.rl_item_3) {
            binding.rlItem1.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem2.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem3.setBackgroundResource(R.drawable.play_item_select_bg);
            binding.rlItem4.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.ivItem1.setVisibility(View.GONE);
            binding.ivItem2.setVisibility(View.GONE);
            binding.ivItem3.setVisibility(View.VISIBLE);
            binding.ivItem4.setVisibility(View.GONE);
        } else if (view.getId() == R.id.rl_item_4) {
            binding.rlItem1.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem2.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.rlItem4.setBackgroundResource(R.drawable.play_item_select_bg);
            binding.rlItem3.setBackgroundResource(R.drawable.radius_5_22252f);
            binding.ivItem1.setVisibility(View.GONE);
            binding.ivItem2.setVisibility(View.GONE);
            binding.ivItem3.setVisibility(View.GONE);
            binding.ivItem4.setVisibility(View.VISIBLE);
        }
    }

}