package com.ctdj.djandroid.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.bean.UpdatePersonalBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityEditNameBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;

public class EditNameActivity extends BaseActivity {
    ActivityEditNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNameBinding.inflate(LayoutInflater.from(this));
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
                updateNickname();
            }
        });
        binding.etNickname.setFilters(new InputFilter[]{new SpaceFilter()});
    }

    private void updateNickname() {
        String nickname = binding.etNickname.getText().toString().trim();
        if (nickname.length() < 4) {
            Utils.showToast(this, "请输入4-20位昵称");
        } else {
            HttpClient.updatePersonal(this, 2, nickname, new HttpCallback() {
                @Override
                public void onSuccess(String result) {
                    UpdatePersonalBean bean = new Gson().fromJson(result, UpdatePersonalBean.class);
                    MyApplication.getInstance().saveUserInfo(bean.data);
                    Utils.showToast(EditNameActivity.this, "修改成功");
                    finish();
                }

                @Override
                public void onFailure(String msg) {
                    Utils.showToast(EditNameActivity.this, msg);
                }
            });
        }
    }

    /**
     * 禁止输入空格
     *
     * @return
     */
    public class SpaceFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
            if (source.equals(" "))
                return "";
            return null;
        }
    }
}