package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.bean.UploadBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityFeedBackBinding;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity {

    ActivityFeedBackBinding binding;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBackBinding.inflate(LayoutInflater.from(this));
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

        binding.etFeedbackContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setBtnBg();
            }
        });
    }

    public void commitBtnClick(View view) {
        if (TextUtils.isEmpty(binding.etFeedbackContent.getText().toString().trim())) {
            Utils.showToast(this, "请输入意见反馈内容");
        } else if (TextUtils.isEmpty(imageUrl)){
            Utils.showToast(this, "请上传反馈图片");
        } else {
            HttpClient.opinion(this, binding.etFeedbackContent.getText().toString().trim(), imageUrl, new HttpCallback() {
                @Override
                public void onSuccess(String result) {
                    Utils.showToast(FeedBackActivity.this, "提交成功");
                    finish();
                }

                @Override
                public void onFailure(String msg) {
                    Utils.showToast(FeedBackActivity.this, msg);
                }
            });
        }
    }

    private void setBtnBg() {
        if (TextUtils.isEmpty(binding.etFeedbackContent.getText().toString().trim()) || TextUtils.isEmpty(imageUrl)) {
            binding.btnCommit.setAlpha(0.5f);
        } else {
            binding.btnCommit.setAlpha(1f);
        }
    }

    public void pickPhoto(View view) {
        if (!Utils.checkPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            LogUtil.e("请求权限");
            ActivityCompat.requestPermissions(this, new String[]{
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
                .withAspectRatio(1, 2)
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
                LogUtil.e(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getCutPath())));
                filePaths.add(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getCutPath())));
            } else {
                filePaths.add(list.get(0).getCutPath());
            }
            Glide.with(this).load(filePaths.get(0)).into(binding.ivPhoto);
            uploadImage(filePaths.get(0));
        }
    }

    private void uploadImage(String path) {
        Utils.showLoadingDialog(this);
        HttpClient.uploadImage(this, path, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.hideLoadingDialog();
                Utils.showToast(FeedBackActivity.this, "上传成功！");
                UploadBean bean = new Gson().fromJson(result, UploadBean.class);
                imageUrl = bean.url;
                setBtnBg();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(FeedBackActivity.this, msg);
                Utils.hideLoadingDialog();
            }
        });
    }
}