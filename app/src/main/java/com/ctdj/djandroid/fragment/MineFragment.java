package com.ctdj.djandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.AboutActivity;
import com.ctdj.djandroid.activity.BindGameAccountActivity;
import com.ctdj.djandroid.activity.EditActivity;
import com.ctdj.djandroid.activity.FeedBackActivity;
import com.ctdj.djandroid.activity.KingRecordActivity;
import com.ctdj.djandroid.activity.SettingActivity;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.FragmentMessageBinding;
import com.ctdj.djandroid.databinding.FragmentMineBinding;
import com.ctdj.djandroid.net.UserInfoBean;

public class MineFragment extends Fragment {
    FragmentMineBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getUserInfo() == null) {
                    Utils.showToast(getActivity(), "用户信息未加载到");
                } else {
                    startActivity(new Intent(getActivity(), EditActivity.class));
                }
            }
        });

        binding.tvKingRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), KingRecordActivity.class));
            }
        });

        binding.tvBindGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BindGameAccountActivity.class));
            }
        });

        binding.tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });

        binding.tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FeedBackActivity.class));
            }
        });

        binding.tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fillView();
    }

    private void fillView() {
        UserInfoBean bean = MyApplication.getInstance().getUserInfo();
        Glide.with(this).load(bean.headimg).into(binding.ivAvatar);
        binding.tvNickname.setText(bean.mname);
        binding.tvNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, bean.sex == 1 ? R.drawable.male_small_icon : R.drawable.female_small_icon, 0);
        binding.tvId.setText(bean.mid);
        if (TextUtils.isEmpty(bean.city)) {
            binding.tvCity.setVisibility(View.GONE);
        } else {
            binding.tvCity.setVisibility(View.VISIBLE);
            binding.tvCity.setText(bean.city);
        }
        if (TextUtils.isEmpty(bean.birthday)) {
            binding.tvConstellation.setVisibility(View.GONE);
        } else {
            binding.tvConstellation.setVisibility(View.VISIBLE);
            String constellation = Utils.constellation(bean.birthday);
            binding.tvConstellation.setText(constellation);
        }
    }
}
