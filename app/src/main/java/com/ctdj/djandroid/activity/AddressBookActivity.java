package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.ViewPagerAdapter;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.databinding.ActivityAddressBookBinding;
import com.ctdj.djandroid.fragment.UserListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录
 */
public class AddressBookActivity extends BaseActivity {

    ActivityAddressBookBinding binding;
    private int currentIndex = 0;
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
        binding.tvTitleFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex == 0) {
                    return;
                }
                currentIndex = 0;
                binding.viewPager.setCurrentItem(0);
            }
        });

        binding.tvTitleFans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex == 1) {
                    return;
                }
                currentIndex = 1;
                binding.viewPager.setCurrentItem(1);
            }
        });

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.tvTitleFollow.setAlpha(1f);
                    binding.tvTitleFans.setAlpha(0.5f);
                } else {
                    binding.tvTitleFollow.setAlpha(0.5f);
                    binding.tvTitleFans.setAlpha(1f);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}