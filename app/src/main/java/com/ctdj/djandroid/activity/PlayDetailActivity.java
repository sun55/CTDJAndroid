package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.bean.OrderDetailBean;
import com.ctdj.djandroid.bean.UploadBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.GlideEngine;
import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityPlayDetailBinding;
import com.ctdj.djandroid.dialog.InvitePlayDialog;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.TitleView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

public class PlayDetailActivity extends BaseActivity {

    ActivityPlayDetailBinding binding;
    private static final int UPLOAD_IMAGE_FOR_GAME = 1002;
    OrderDetailBean.Data data;
    private int from; // 从哪里跳转进来的  1 聊天页面 2 订单列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayDetailBinding.inflate(LayoutInflater.from(this));
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
        binding.tvGameNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.copy(PlayDetailActivity.this, binding.tvGameNickname.getText().toString());
            }
        });
        binding.tvOrderNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.copy(PlayDetailActivity.this, binding.tvOrderNo.getText().toString());
            }
        });
        String orderno = getIntent().getStringExtra("orderno");
        from = getIntent().getIntExtra("from", 1);
        HttpClient.queryChallengeOrderDtl(this, orderno, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                OrderDetailBean bean = new Gson().fromJson(result, OrderDetailBean.class);
                data = bean.getData();
                fillView();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PlayDetailActivity.this, msg);
            }
        });
    }

    private void fillView() {
        if (data == null) {
            return;
        }
        if (data.getChallengeType() == 1) {
            binding.tvChallengeType.setText("金币挑战赛");
            binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gold_icon_2, 0);
        } else {
            binding.tvChallengeType.setText("赏金挑战赛");
            binding.tvValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.diamond_icon, 0);
        }
        binding.tvValue.setText(data.getSta() == 0 ? data.getAward() + "" : (data.getAward() / 2) + "");
        binding.tvGameType.setText(data.getGameType());
        binding.tvGameArea.setText(data.getArea() == 1 ? "微信区服" : "企鹅区服");
        binding.tvOrderNo.setText(data.getOrderno());
        binding.tvCreateTime.setText(data.getCreateTime());
        binding.tvGameNickname.setText(data.getGameMname());
        binding.tvNickname.setText(data.getFmname());
        Glide.with(this).load(data.getFheadimg()).into(binding.avatar);
        switch (data.getSta()) {
            case 0:
                if (data.getIsdefier() == 1) { // 挑战者
                    binding.tvPlayStatus.setText("等待对方应战...");
                    binding.tvStatusDesc.setText("你已发起约战，等待对方应战");
                    binding.btn1.setText("取消比赛");
                    binding.btn2.setVisibility(View.GONE);
                } else { // 接受者
                    binding.tvPlayStatus.setText("请速速应战...");
                    binding.tvStatusDesc.setText("你已发起约战对方已发起约战，请速速应战");
                    binding.btn1.setText("拒绝");
                    binding.btn2.setText("同意");
                    binding.btn2.setVisibility(View.VISIBLE);
                }

                binding.tv1.setText("比赛后，请及时上传比赛赛果截图");
                binding.tv2.setText("请在比赛有效时间内，上传截图，否则将解散比赛");
                binding.tv3.setText("请上传正确的比赛截图信息，恶意随意上传其他图片将会自动判定为失败");
                binding.tv4.setText("可举报玩家违规行为，审核通过将获取平台奖励");
                break;
            case 1:
                binding.tvPlayStatus.setText("比赛进行中...");
                binding.tvStatusDesc.setText("请于6小时内上传比赛结果，否则将自动解散比赛");
                binding.btn1.setVisibility(View.GONE);
                binding.btn2.setText("上传截图");
                binding.btn2.setVisibility(View.VISIBLE);
                binding.tv1.setText("请及时上传比赛赛果截图");
                binding.tv2.setText("请在比赛有效时间内，上传截图，否则将解散比赛");
                binding.tv3.setText("请上传正确的比赛截图信息，恶意随意上传其他图片将会自动判定为失败");
                binding.tv4.setText("可举报玩家违规行为，审核通过将获取平台奖励");
                break;
            case 2:
                binding.tvPlayStatus.setText("判定胜负中...");
                binding.tvStatusDesc.setText("平台将在30分钟内，判定胜负结果");
                binding.btn2.setText("查看截图");
                if (data.getUpdateBy().equals(MyApplication.getInstance().getMid())) {
                    binding.btn1.setVisibility(View.GONE);
                } else {
                    binding.btn1.setVisibility(View.VISIBLE);
                    binding.btn1.setText("赛果申诉");
                }
                binding.tv1.setText("请及时上传比赛赛果截图");
                binding.tv2.setText("请在比赛有效时间内，上传截图，否则将解散比赛");
                binding.tv3.setText("请上传正确的比赛截图信息，恶意随意上传其他图片将会自动判定为失败");
                binding.tv4.setText("可举报玩家违规行为，审核通过将获取平台奖励");
                break;
            case 99: // 已取消
                binding.rlTop.setVisibility(View.GONE);
                binding.tvPlayStatus.setText("比赛已取消");
                binding.tvStatusDesc.setText("已取消比赛，报名费已原路返还");
                break;
            case 4:
                binding.tvPlayStatus.setText("申诉审核中...");
                binding.tvStatusDesc.setText("平台正在审核你的申诉情况，请耐心等待结果");
                binding.btn1.setText("查看申诉");
                binding.btn2.setText("查看截图");
                binding.tv1.setText("请及时上传比赛赛果截图");
                binding.tv2.setText("请在比赛有效时间内，上传截图，否则将解散比赛");
                binding.tv3.setText("请上传正确的比赛截图信息，恶意随意上传其他图片将会自动判定为失败");
                binding.tv4.setText("可举报玩家违规行为，审核通过将获取平台奖励");
                break;
            case 5:
                binding.rlTop.setVisibility(View.GONE);
                if (data.getWinType() == 1) {
                    binding.tvPlayStatus.setText("比赛成绩：胜利");
                    binding.tvStatusDesc.setText("比赛对局获胜，奖金已发送至你的奖金中");
                } else {
                    binding.tvPlayStatus.setText("比赛成绩：败北");
                    binding.tvStatusDesc.setText("比赛对局败北，请再接再厉");
                }
                break;
        }
    }

    public void onBtn1Click(View view) {
        if (data == null) {
            return;
        }

        if (data.getSta() == 0) {
            if (data.getIsdefier() == 1) {
                cancelPlay();
            } else {
                refusePlay();
            }
        } else if (data.getSta() == 2) { // 赛果申诉
            Intent intent = new Intent(this, FeedBackActivity.class);
            intent.putExtra("from", 3);
            intent.putExtra("challengeId", data.getChallengeId());
            startActivity(intent);
        } else if (data.getSta() == 4) { // 查看申诉
            Intent intent = new Intent(this, FeedBackActivity.class);
            intent.putExtra("from", 2);
            intent.putExtra("imageUrl", data.getAppealimg());
            intent.putExtra("remarks", data.getRemarks());
            startActivity(intent);
        }
    }

    /**
     * 取消比赛
     */
    private void cancelPlay() {
        HttpClient.closeMatch(this, data.getChallengeId(), new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.showToast(PlayDetailActivity.this, "取消比赛成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PlayDetailActivity.this, msg);
            }
        });
    }

    /**
     * 拒绝比赛
     */
    private void refusePlay() {
        HttpClient.receiveOrRefuseChallenge(this, data.getChallengeId(), 2, "", new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.showToast(PlayDetailActivity.this, "拒绝比赛成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PlayDetailActivity.this, msg);
            }
        });
    }

    public void onBtn2Click(View view) {
        if (data == null) {
            return;
        }

        if (data.getSta() == 0) {
            if (data.getIsdefier() != 1) {
                receivePlay();
            }
        } else if (data.getSta() == 1) { // 上传截图
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
                    .isCamera(false)
                    .imageEngine(GlideEngine.createGlideEngine())
                    .imageSpanCount(4)
                    .selectionMode(PictureConfig.SINGLE)
                    .freeStyleCropEnabled(true)
                    .isDragFrame(false)
                    .maxSelectNum(1)
                    .selectionMode(PictureConfig.MULTIPLE)
                    .synOrAsy(true)
                    .forResult(UPLOAD_IMAGE_FOR_GAME);
        } else if (data.getSta() == 2   || data.getSta() == 4) { // 查看游戏截图
            if (TextUtils.isEmpty(data.getGameimg())) {
                Utils.showToast(this, "赛果截图数据为空");
                return;
            }
            Utils.previewSingleImage(PlayDetailActivity.this, data.getGameimg());
        }/* else if (data.getSta() == 4) { // 查看申诉截图
            if (TextUtils.isEmpty(data.getAppealimg())) {
                Utils.showToast(this, "申诉截图数据为空");
                return;
            }
            Utils.previewSingleImage(PlayDetailActivity.this, data.getAppealimg());
        }*/
    }

    /**
     * 接受比赛
     */
    private void receivePlay() {
        InvitePlayDialog dialog = new InvitePlayDialog(this, data.getFmid(), data.getOrderno(), data.getAward(), data.getChallengeType(), data.getArea(), data.getChallengeId());
        dialog.show();
        dialog.setListener(new InvitePlayDialog.OnBtnClickListener() {
            @Override
            public void onBtnClick() {
                Utils.showToast(PlayDetailActivity.this, "应战成功");
                finish();
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
        if (requestCode == UPLOAD_IMAGE_FOR_GAME) {
            uploadImageForGame(imagePath);
        }
    }

    private void uploadImageForGame(String filePath) {
        Utils.showLoadingDialog(this);
        HttpClient.uploadImage(this, filePath, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                UploadBean bean = new Gson().fromJson(result, UploadBean.class);
                HttpClient.submitAudit(PlayDetailActivity.this, data.getChallengeId(), 2, bean.url, "", new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Utils.hideLoadingDialog();
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.hideLoadingDialog();
                        Utils.showToast(PlayDetailActivity.this, msg);
                    }
                });
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(PlayDetailActivity.this, msg);
                Utils.hideLoadingDialog();
            }
        });
    }

    public void onCallClick(View view) {
        if (from == 1) {
            finish();
        }
    }
}