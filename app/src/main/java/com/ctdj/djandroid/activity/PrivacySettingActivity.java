package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.UpdatePersonalBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityPrivacySettingBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

/**
 * 隐私权限设置
 */
public class PrivacySettingActivity extends BaseActivity {

    ActivityPrivacySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacySettingBinding.inflate(LayoutInflater.from(this));
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

        binding.ivToggle.setBackgroundResource(MyApplication.getInstance().getUserInfo().hideRecord == 1 ? R.drawable.toggle_close : R.drawable.toggle_open);
        binding.ivToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpClient.updatePersonal(PrivacySettingActivity.this,
                        7,
                        MyApplication.getInstance().getUserInfo().hideRecord == 1 ? 0 : 1,
                        "",
                        new HttpCallback() {
                            @Override
                            public void onSuccess(String result) {
                                UpdatePersonalBean bean = new Gson().fromJson(result, UpdatePersonalBean.class);
                                MyApplication.getInstance().saveUserInfo(bean.data);
                                Utils.showToast(PrivacySettingActivity.this, "修改成功");
                                binding.ivToggle.setBackgroundResource(MyApplication.getInstance().getUserInfo().hideRecord == 1 ? R.drawable.toggle_close : R.drawable.toggle_open);                            }

                            @Override
                            public void onFailure(String msg) {
                                Utils.showToast(PrivacySettingActivity.this, msg);
                            }
                        });
            }
        });
    }
}