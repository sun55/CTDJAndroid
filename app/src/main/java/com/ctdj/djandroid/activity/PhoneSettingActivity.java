package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.ctdj.djandroid.databinding.ActivityPhoneSettingBinding;

/**
 * 手机号设置
 */
public class PhoneSettingActivity extends BaseActivity {

    ActivityPhoneSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneSettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}