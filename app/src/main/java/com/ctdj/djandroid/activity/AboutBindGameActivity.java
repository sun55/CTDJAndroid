package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.databinding.ActivityAboutBindGameBinding;
import com.ctdj.djandroid.view.TitleView;

/**
 * 查看关联指引
 */
public class AboutBindGameActivity extends BaseActivity {

    ActivityAboutBindGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBindGameBinding.inflate(LayoutInflater.from(this));
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
    }
}