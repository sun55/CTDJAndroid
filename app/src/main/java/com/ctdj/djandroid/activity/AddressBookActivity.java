package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.ViewPagerAdapter;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.databinding.ActivityAddressBookBinding;
import com.ctdj.djandroid.fragment.UserListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录
 */
public class AddressBookActivity extends BaseActivity {

    ActivityAddressBookBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBookBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        LinearLayoutCompat.LayoutParams l = (LinearLayoutCompat.LayoutParams) binding.rlTop.getLayoutParams();
        l.topMargin = DisplayUtil.getStatusBarHeight(this);
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<Fragment> list = new ArrayList<>();
        list.add(new UserListFragment(UserListFragment.FOLLOW));
        list.add(new UserListFragment(UserListFragment.FANS));
        binding.viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), list));
    }
}