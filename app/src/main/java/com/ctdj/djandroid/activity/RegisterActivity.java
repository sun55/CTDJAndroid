package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.BaseBean;
import com.ctdj.djandroid.bean.RegisterBean;
import com.ctdj.djandroid.bean.UploadBean;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityRegisterBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends BaseActivity {
    ActivityRegisterBinding binding;
    int page = 1;
    int sex = 1; // 1 男 2 女
    String mobile;
    String avatarUrl;
    String birthday;
    String nickname;
    String iCode; // 邀请码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mobile = getIntent().getStringExtra("mobile");
        page = 1;
        binding.ll1.setVisibility(View.VISIBLE);
        binding.ll2.setVisibility(View.GONE);
        binding.tvPreStep.setVisibility(View.GONE);
        binding.button.setText("还差一步");
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        DateEntity startValue = DateEntity.target(currentYear - 60, 1, 1);
        DateEntity defaultValue = DateEntity.target(2000, 7, 7);
        DateEntity endValue = DateEntity.target(currentYear, currentMonth, currentDay);
        binding.selectDate.setRange(startValue, endValue);
        binding.selectDate.setDefaultValue(defaultValue);
        binding.selectDate.setDateMode(DateMode.YEAR_MONTH_DAY);
        binding.selectDate.setCurvedMaxAngle(3);

        binding.etNickname.setFilters(new InputFilter[]{new SpaceFilter()});
    }

    public void previousStep(View view) {
        page = 1;
        binding.ll1.setVisibility(View.VISIBLE);
        binding.ll2.setVisibility(View.GONE);
        binding.tvPreStep.setVisibility(View.GONE);
        binding.button.setText("还差一步");
    }

    public void btnClick(View view) {
        if (page == 1) {
            page = 2;
            binding.button.setText("进入超团");
            binding.ll1.setVisibility(View.GONE);
            binding.ll2.setVisibility(View.VISIBLE);
            binding.tvPreStep.setVisibility(View.VISIBLE);
        } else {
            birthday = binding.selectDate.getSelectedYear() + "-" + (binding.selectDate.getSelectedMonth() < 10 ? "0" + binding.selectDate.getSelectedMonth() : binding.selectDate.getSelectedMonth()) + "-" + (binding.selectDate.getSelectedDay() < 10 ? "0" + binding.selectDate.getSelectedDay() : binding.selectDate.getSelectedDay());
            nickname = binding.etNickname.getText().toString().trim();
            iCode = binding.etCode.getText().toString().trim();
            if (TextUtils.isEmpty(birthday)) {
                Utils.showToast(this, "请选择生日");
            } else if (TextUtils.isEmpty(avatarUrl)) {
                Utils.showToast(this, "请上传头像");
            } else if (nickname.length() < 4) {
                Utils.showToast(this, "请输入4-20位昵称");
            } else {
                register();
            }

        }
    }

    private void register() {
        HttpClient.registerLogin(RegisterActivity.this,
                mobile,
                nickname,
                sex,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),
                birthday,
                avatarUrl,
                iCode,
                new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        RegisterBean bean = new Gson().fromJson(result, RegisterBean.class);
                        MyApplication.getInstance().saveUserInfo(bean.data);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.showToast(RegisterActivity.this, msg);
                    }
                });
    }

    public void selectMale(View view) {
        if (sex == 1) {
            return;
        }
        sex = 1;
        binding.ivMale.setImageResource(R.drawable.male_selected);
        binding.ivFemale.setBackgroundResource(R.drawable.female_unselect);
    }

    public void selectFemale(View view) {
        if (sex == 2) {
            return;
        }
        sex = 2;
        binding.ivMale.setImageResource(R.drawable.male_unselect);
        binding.ivFemale.setBackgroundResource(R.drawable.female_selected);
    }

    public void selectPhoto(View view) {
        if (!Utils.checkPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            LogUtil.e("请求权限");
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        }
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
//                    .theme(R.style.picture_white_style)
                .isCamera(true)
                .imageEngine(GlideEngine.createGlideEngine())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
//                .isSingleDirectReturn(true)
                .isEnableCrop(true)
                .withAspectRatio(1, 1)
                .freeStyleCropEnabled(true)
                .showCropFrame(true)
                .isDragFrame(false)
//                .isCompress(true)
                .maxSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .synOrAsy(true)
//                .compressQuality(80)
                .forResult(1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
        if (list == null || list.size() <= 0) {
            return;
        }
        if (requestCode == 1000) {
            List<String> filePaths = new ArrayList<>();
            if (list.get(0).getPath().contains("content://")) {
                LogUtil.e(Utils.getFilePathByUri(RegisterActivity.this, Uri.parse(list.get(0).getCutPath())));
                filePaths.add(Utils.getFilePathByUri(RegisterActivity.this, Uri.parse(list.get(0).getCutPath())));
            } else {
                filePaths.add(list.get(0).getCutPath());
            }
            Glide.with(this).load(filePaths.get(0)).circleCrop().into(binding.ivSelectPhoto);
            uploadImage(filePaths.get(0));
        }
    }

    private void uploadImage(String path) {
        Utils.showLoadingDialog(this);
        HttpClient.uploadImage(this, path, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.hideLoadingDialog();
                Utils.showToast(RegisterActivity.this, "上传成功！");
                UploadBean bean = new Gson().fromJson(result, UploadBean.class);
                avatarUrl = bean.url;
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(RegisterActivity.this, msg);
                Utils.hideLoadingDialog();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 111: {

                return;
            }
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