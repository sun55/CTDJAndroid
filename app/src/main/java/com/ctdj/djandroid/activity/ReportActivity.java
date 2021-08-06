package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.adapter.ReportAdapter;
import com.ctdj.djandroid.adapter.ReportImageAdapter;
import com.ctdj.djandroid.bean.ReportBean;
import com.ctdj.djandroid.bean.UploadFilesBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityReportBinding;
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
 * 举报页面
 */
public class ReportActivity extends BaseActivity {

    ActivityReportBinding binding;
    ReportAdapter itemAdapter;
    ReportImageAdapter imageAdapter;
    ArrayList<String> imageList = new ArrayList<>();
    boolean isSelectBlack = true;
    String targetUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(LayoutInflater.from(this));
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
        binding.rcvReport.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ReportAdapter(new ArrayList<>());
        binding.rcvReport.setAdapter(itemAdapter);
        imageAdapter = new ReportImageAdapter(this, imageList);
        binding.imgGridView.setAdapter(imageAdapter);
        imageAdapter.setOnDeleteItemListener(new ReportImageAdapter.OnDeleteItemListener() {
            @Override
            public void onDeleteItem(int position) {
//                imgBuilder = new StringBuilder();
//                for (int i = 0; i < imageAdapter.getList().size(); i++) {
//                    if (i == imageAdapter.getList().size() - 1) {
//                        imgBuilder.append(imageAdapter.getList().get(i));
//                    } else {
//                        imgBuilder.append(imageAdapter.getList().get(i) + ",");
//                    }
//                }
            }
        });
        binding.imgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imageAdapter.getListSize()) {
                    pickPhoto();
                } else {
                    Utils.previewSingleImage(ReportActivity.this, imageAdapter.getList().get(position));
                }
            }
        });
        initData();
    }

    private void pickPhoto() {
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
                .isCamera(true)
                .imageEngine(GlideEngine.createGlideEngine())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
                .freeStyleCropEnabled(true)
                .showCropFrame(true)
                .isDragFrame(false)
                .maxSelectNum(3 - imageList.size())
                .selectionMode(PictureConfig.MULTIPLE)
                .synOrAsy(true)
                .forResult(1004);
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
        if (requestCode == 1004) {
            List<String> filePaths = new ArrayList<>();
            for (LocalMedia l : list) {
                if (l.getPath().contains("content://")) {
                    LogUtil.e(Utils.getFilePathByUri(this, Uri.parse(l.getPath())));
                    filePaths.add(Utils.getFilePathByUri(this, Uri.parse(l.getPath())));
                } else {
                    filePaths.add(l.getPath());
                }
            }
            Utils.showLoadingDialog(this);
            HttpClient.uploadFiles(this, filePaths, new HttpCallback() {
                @Override
                public void onSuccess(String result) {
                    Utils.hideLoadingDialog();
                    UploadFilesBean bean = new Gson().fromJson(result, UploadFilesBean.class);
                    imageList.addAll(bean.getData().getImglist());
                    imageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String msg) {
                    Utils.hideLoadingDialog();
                }
            });
        }
    }

    private void initData() {
        targetUserId = getIntent().getStringExtra("user_id");
        HttpClient.reportIntoData(this, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                ReportBean bean = new Gson().fromJson(result, ReportBean.class);
                itemAdapter.setNewData(bean.getData());
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(ReportActivity.this, msg);
            }
        });
    }

    public void reportClick(View view) {
        if (itemAdapter.getSelectIndex() == -1) {
            Utils.showToast(this, "请选择举报原因");
            return;
        }
        if (isSelectBlack) {
            HttpClient.addBlack(ReportActivity.this, MyApplication.getInstance().getMid(), targetUserId, new HttpCallback() {
                @Override
                public void onSuccess(String result) {
                    addReport();
                }

                @Override
                public void onFailure(String msg) {
                    Utils.showToast(ReportActivity.this, msg);
                }
            });
        } else {
            addReport();
        }
    }

    private void addReport() {
        String remark = binding.etDesc.getText().toString().trim();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < imageAdapter.getList().size(); i++) {
            if (i == imageAdapter.getList().size() - 1) {
                builder.append(imageAdapter.getList().get(i));
            } else {
                builder.append(imageAdapter.getList().get(i) + ",");
            }
        }
        HttpClient.addReport(ReportActivity.this,
                targetUserId,
                itemAdapter.getSelectItem().getTypeNo(),
                builder.toString(),
                remark,
                new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Utils.showToast(ReportActivity.this, "举报成功");
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.showToast(ReportActivity.this, msg);
                    }
                });
    }

    public void selectBlackClick(View view) {
        isSelectBlack = !isSelectBlack;
        binding.ivBlackCheck.setBackgroundResource(isSelectBlack ? R.drawable.report_item_checked : R.drawable.report_item_uncheck);
    }
}