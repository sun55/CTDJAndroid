package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.ctdj.djandroid.databinding.ActivityReportBinding;

/**
 * 举报页面
 */
public class ReportActivity extends BaseActivity {

    ActivityReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}