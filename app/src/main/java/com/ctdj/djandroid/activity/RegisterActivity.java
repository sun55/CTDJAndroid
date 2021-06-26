package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityRegisterBinding;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity {
    ActivityRegisterBinding binding;
    int page = 1;
    int gender = 1; // 1 男 2 女

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        page = 1;
        binding.ll1.setVisibility(View.VISIBLE);
        binding.ll2.setVisibility(View.GONE);
        binding.tvPreStep.setVisibility(View.GONE);
        binding.button.setText("还差一步");
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
            Utils.showToast(this, "正在开发中。。。");
        }
    }

    public void selectMale(View view) {
        if (gender == 1) {
            return;
        }
        gender = 1;
        binding.ivMale.setImageResource(R.drawable.male_selected);
        binding.ivFemale.setBackgroundResource(R.drawable.female_unselect);
    }

    public void selectFemale(View view) {
        if (gender == 2) {
            return;
        }
        gender = 2;
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
                .withAspectRatio(3, 4)
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
                LogUtil.e(Utils.getFilePathByUri(RegisterActivity.this, Uri.parse(list.get(0).getPath())));
                filePaths.add(Utils.getFilePathByUri(RegisterActivity.this, Uri.parse(list.get(0).getPath())));
            } else {
                filePaths.add(list.get(0).getPath());
            }
            Glide.with(this).load(list.get(0).getPath()).into(binding.ivSelectPhoto);
        }
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
}