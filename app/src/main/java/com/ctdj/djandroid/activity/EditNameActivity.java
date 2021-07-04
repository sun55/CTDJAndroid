package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.ctdj.djandroid.databinding.ActivityEditNameBinding;

public class EditNameActivity extends BaseActivity {
    ActivityEditNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNameBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}