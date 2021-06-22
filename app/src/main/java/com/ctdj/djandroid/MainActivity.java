package com.ctdj.djandroid;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.ctdj.djandroid.activity.BaseActivity;
import com.ctdj.djandroid.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}