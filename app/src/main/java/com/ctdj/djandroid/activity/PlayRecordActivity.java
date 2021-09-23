package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.PlayRecordBean;
import com.ctdj.djandroid.bean.UploadBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityPlayRecordBinding;
import com.ctdj.djandroid.dialog.InvitePlayDialog;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.CircleImageView;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class PlayRecordActivity extends BaseActivity {

    ActivityPlayRecordBinding binding;
    private int page = 1;
    BaseQuickAdapter adapter;
    private static final int UPLOAD_IMAGE_FROM_PLAY_RECORD = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayRecordBinding.inflate(LayoutInflater.from(this));
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<PlayRecordBean.Data, BaseViewHolder>(R.layout.play_record_item_layout, new ArrayList<>()) {

            @Override
            protected void convert(@NonNull BaseViewHolder helper, PlayRecordBean.Data item) {
                ((TextView) helper.getView(R.id.tv_create_time)).setText(item.getCreateTime());
                Glide.with(PlayRecordActivity.this).load(item.getFheadimg()).into(((CircleImageView) helper.getView(R.id.avatar)));
                ((TextView) helper.getView(R.id.tv_nickname)).setText(item.getFmname());
                ((TextView) helper.getView(R.id.tv_challenge_type)).setText(item.getChallengeType() == 1 ? "金币挑战赛" : "赏金挑战赛");
                ((TextView) helper.getView(R.id.tv_award)).setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        item.getChallengeType() == 1 ? R.drawable.gold_icon : R.drawable.diamond_icon,
                        0);
                ((TextView) helper.getView(R.id.tv_award)).setText(item.getAward() + "");
                TextView tvStatus = helper.getView(R.id.tv_status);
                ImageView ivWinStatus = helper.getView(R.id.iv_win_status);
                Button btnLeft = helper.getView(R.id.btn_left);
                Button btnRight = helper.getView(R.id.btn_right);
                switch (item.getSta()) {
                    case 0:
                        btnLeft.setVisibility(View.VISIBLE);
                        btnRight.setVisibility(View.VISIBLE);
                        ivWinStatus.setVisibility(View.GONE);
                        if (item.getIsdefier() == 1) { // 挑战者
                            tvStatus.setText("等待对方应战");
                            btnLeft.setText("取消比赛");
                            btnRight.setText("等待应战");
                            btnRight.setAlpha(0.5f);
                            btnRight.setEnabled(false);
                        } else { // 接受者
                            tvStatus.setText("请速速应战");
                            btnLeft.setText("拒绝比赛");
                            btnRight.setText("立即应战");
                            btnRight.setEnabled(true);
                        }
                        break;
                    case 1:
                        tvStatus.setText("进行中");
                        btnLeft.setVisibility(View.GONE);
                        btnRight.setVisibility(View.VISIBLE);
                        ivWinStatus.setVisibility(View.GONE);
                        btnRight.setText("上传截图");
                        btnRight.setEnabled(true);
                        break;
                    case 2:
                        tvStatus.setText("审核中");
                        btnRight.setEnabled(true);
                        btnRight.setVisibility(View.VISIBLE);
                        btnRight.setText("查看截图");
                        ivWinStatus.setVisibility(View.GONE);
                        if (item.getUpdateBy().equals(MyApplication.getInstance().getMid())) {
                            btnLeft.setVisibility(View.GONE);
                        } else {
                            btnLeft.setVisibility(View.VISIBLE);
                            btnLeft.setText("赛果申诉");
                        }
                        break;
                    case 99: // 已取消
                        tvStatus.setText("已取消");
                        btnLeft.setVisibility(View.GONE);
                        btnRight.setVisibility(View.GONE);
                        ivWinStatus.setVisibility(View.GONE);
                        break;
                    case 4:
                        tvStatus.setText("申诉审核中");
                        btnLeft.setVisibility(View.VISIBLE);
                        btnRight.setVisibility(View.VISIBLE);
                        ivWinStatus.setVisibility(View.GONE);
                        btnRight.setEnabled(true);
                        btnLeft.setText("查看申诉");
                        btnRight.setText("查看截图");
                        break;
                    case 5:
                        btnLeft.setVisibility(View.GONE);
                        btnRight.setVisibility(View.GONE);
                        ivWinStatus.setVisibility(View.VISIBLE);
                        if (item.getWinType() == 1) {
                            tvStatus.setText("胜利");
                            ivWinStatus.setBackgroundResource(R.drawable.win_icon);
                        } else {
                            tvStatus.setText("败北");
                            ivWinStatus.setBackgroundResource(R.drawable.failure_icon);
                        }
                        break;
                }
                btnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getSta() == 0) {
                            if (item.getIsdefier() == 1) {
                                cancelPlay(item.getChallengeId());
                            } else {
                                refusePlay(item.getChallengeId());
                            }
                        } else if (item.getSta() == 2) { // 赛果申诉
                            Intent intent = new Intent(PlayRecordActivity.this, FeedBackActivity.class);
                            intent.putExtra("from", 3);
                            intent.putExtra("challengeId", item.getChallengeId());
                            startActivity(intent);
                        } else if (item.getSta() == 4) { // 查看申诉
                            Intent intent = new Intent(PlayRecordActivity.this, FeedBackActivity.class);
                            intent.putExtra("from", 2);
                            intent.putExtra("imageUrl", item.getAppealimg());
                            intent.putExtra("remarks", item.getRemarks());
                            startActivity(intent);
                        }
                    }
                });

                btnRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getSta() == 0) {
                            if (item.getIsdefier() != 1) {
                                receivePlay(
                                        item.getFmid(),
                                        item.getOrderno(),
                                        item.getAward(),
                                        item.getChallengeType(),
                                        item.getArea(),
                                        item.getChallengeId());
                            }
                        } else if (item.getSta() == 1) { // 上传截图
                            uploadByData = item;
                            if (!Utils.checkPermissions(PlayRecordActivity.this, new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                                LogUtil.e("请求权限");
                                ActivityCompat.requestPermissions(PlayRecordActivity.this, new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
                                return;
                            }
                            PictureSelector.create(PlayRecordActivity.this)
                                    .openGallery(PictureMimeType.ofImage())
                                    .isCamera(false)
                                    .imageEngine(GlideEngine.createGlideEngine())
                                    .imageSpanCount(4)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .freeStyleCropEnabled(true)
                                    .isDragFrame(false)
                                    .maxSelectNum(1)
                                    .selectionMode(PictureConfig.MULTIPLE)
                                    .synOrAsy(true)
                                    .forResult(UPLOAD_IMAGE_FROM_PLAY_RECORD);
                        } else if (item.getSta() == 2 || item.getSta() == 4) { // 查看游戏截图
                            if (TextUtils.isEmpty(item.getGameimg())) {
                                Utils.showToast(PlayRecordActivity.this, "赛果截图数据为空");
                                return;
                            }
                            Utils.previewSingleImage(PlayRecordActivity.this, item.getGameimg());
                        }
                    }
                });

                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PlayRecordActivity.this, PlayDetailActivity.class);
                        intent.putExtra("orderno", item.getOrderno());
                        intent.putExtra("from", 3);
                        startActivity(intent);
                    }
                });
            }
        };
        binding.recyclerView.setAdapter(adapter);
        binding.refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getData(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData(true);
            }
        });
        getData(true);
    }

    private void getData(boolean isRefresh) {
        if (isRefresh) {
            page = 1;
        } else {
            page++;
        }
        HttpClient.queryChallengeOrder(this, page, 10, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                PlayRecordBean bean = new Gson().fromJson(result, PlayRecordBean.class);
                if (isRefresh) {
                    binding.refresh.finishRefresh();
                    adapter.setNewData(bean.getData());
                    binding.recyclerView.scrollToPosition(0);
                } else {
                    if (bean.getData().size() > 0) {
                        binding.refresh.finishLoadMore();
                        adapter.addData(bean.getData());
                    } else {
                        binding.refresh.finishLoadMoreWithNoMoreData();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                if (isRefresh) {
                    binding.refresh.finishRefresh();
                } else {
                    binding.refresh.finishLoadMore(false);
                }
                Utils.showToast(PlayRecordActivity.this, msg);
            }
        });
    }

    /**
     * 取消比赛
     */
    private void cancelPlay(int challengeId) {
        HttpClient.closeMatch(this, challengeId, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.showToast(PlayRecordActivity.this, "取消比赛成功");
                getData(true);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PlayRecordActivity.this, msg);
            }
        });
    }

    /**
     * 拒绝比赛
     */
    private void refusePlay(int challengeId) {
        HttpClient.receiveOrRefuseChallenge(this, challengeId, 2, "", new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.showToast(PlayRecordActivity.this, "拒绝比赛成功");
                getData(true);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PlayRecordActivity.this, msg);
            }
        });
    }

    /**
     * 接受比赛
     */
    private void receivePlay(String fmid, String orderno, int award, int challengeType, int area, int challengeId) {
        InvitePlayDialog dialog = new InvitePlayDialog(this, fmid, orderno, award, challengeType, area, challengeId);
        dialog.show();
        dialog.setListener(new InvitePlayDialog.OnBtnClickListener() {
            @Override
            public void onBtnClick() {
                Utils.showToast(PlayRecordActivity.this, "应战成功");
                getData(true);
            }
        });
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
        String imagePath;
        LogUtil.e("path:" + list.get(0).getPath());
        if (list.get(0).getPath().contains("content://")) {
            LogUtil.e(Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath())));
            imagePath = Utils.getFilePathByUri(this, Uri.parse(list.get(0).getPath()));
        } else {
            imagePath = list.get(0).getPath();
        }
        if (requestCode == UPLOAD_IMAGE_FROM_PLAY_RECORD) {
            uploadImageForGame(imagePath);
        }
    }

    private PlayRecordBean.Data uploadByData;

    private void uploadImageForGame(String filePath) {
        Utils.showLoadingDialog(this);
        if (uploadByData == null) {
            return;
        }
        HttpClient.uploadImage(this, filePath, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                UploadBean bean = new Gson().fromJson(result, UploadBean.class);
                HttpClient.submitAudit(PlayRecordActivity.this, uploadByData.getChallengeId(), 2, bean.url, "", new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Utils.hideLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.hideLoadingDialog();
                        Utils.showToast(PlayRecordActivity.this, msg);
                    }
                });
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PlayRecordActivity.this, msg);
                Utils.hideLoadingDialog();
            }
        });
    }
}