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
    int from = 1; // 1 意见反馈 2 查看申诉 3 提交申诉
    int challengeId;

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

        from = getIntent().getIntExtra("from", 1);
        if (from == 1) {
            binding.titleView.setTitle("意见反馈");
            binding.tv1.setText("意见说明");
            binding.etFeedbackContent.setHint("请输入反馈内容");
            binding.tv2.setText("反馈图片");
        } else if (from == 2) {
            binding.titleView.setTitle("查看赛果申诉");
            binding.btnCommit.setVisibility(View.GONE);
            binding.tv1.setText("申诉说明");
            String remarks = getIntent().getStringExtra("remarks");
            binding.etFeedbackContent.setHint("");
            if (!TextUtils.isEmpty(remarks)) {
                binding.etFeedbackContent.setText(remarks);
            }
            binding.etFeedbackContent.setEnabled(false);
            binding.tv2.setText("上传凭证");
            imageUrl = getIntent().getStringExtra("imageUrl");
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this).load(imageUrl).into(binding.ivPhoto);
            }
        } else if (from == 3) {
            binding.titleView.setTitle("赛果申诉");
            binding.tv1.setText("申诉说明");
            binding.etFeedbackContent.setHint("请输入申诉内容");
            binding.tv2.setText("上传凭证");
            challengeId = getIntent().getIntExtra("challengeId", 0);
        }
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
        if (from == 1) {
            if (TextUtils.isEmpty(binding.etFeedbackContent.getText().toString().trim())) {
                Utils.showToast(this, "请输入意见反馈内容");
            } else if (TextUtils.isEmpty(imageUrl)) {
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
        } else if (from == 3) {
            if (TextUtils.isEmpty(binding.etFeedbackContent.getText().toString().trim())) {
                Utils.showToast(this, "请输入申诉内容");
            } else if (TextUtils.isEmpty(imageUrl)) {
                Utils.showToast(this, "请上传凭证图片");
            } else {

                HttpClient.submitAudit(this, challengeId, 4, imageUrl, binding.etFeedbackContent.getText().toString().trim(), new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Utils.showToast(FeedBackActivity.this, "提交申诉成功");
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.showToast(FeedBackActivity.this, msg);
                    }
                });
            }
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
        if (from == 2) {
            if (TextUtils.isEmpty(imageUrl)) {
                Utils.showToast(FeedBackActivity.this, "图片数据为空");
            } else {
                Utils.previewSingleImage(FeedBackActivity.this, imageUrl);
            }
            return;
        }
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
                .freeStyleCropEnabled(true)
                .showCropFrame(true)
                .isDragFrame(false)
//                .isCompress(true)
                .maxSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .synOrAsy(true)
//                .compressQuality(80)
                .forResult(1003);
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
        if (requestCode == 1003) {
            List<String> filePaths = new ArrayList<>();
            if (list.get(0).getPath().contains("content://")) {
                LogUtil.e(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath())));
                filePaths.add(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath())));
            } else {
                filePaths.add(list.get(0).getPath());
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