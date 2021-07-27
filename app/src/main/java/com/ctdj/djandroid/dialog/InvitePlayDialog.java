package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.BindGameAccountActivity;
import com.ctdj.djandroid.activity.BindGameNicknameActivity;
import com.ctdj.djandroid.adapter.PlayPriceAdapter;
import com.ctdj.djandroid.bean.BindGameInfoBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author : Sun
 * @Time : 2021/7/21 14:58
 * @Description :
 */
public class InvitePlayDialog extends Dialog {
    private Context context;
    private RecyclerView rcvPrice;
    private Button btnInvite;
    private TextView tvPlayType;
    private TextView tvPlayArea;
    private TextView tvPlayArea2;
    private TextView tvNickname;
    private String wxName;
    private String qqName;
    PlayPriceAdapter adapter;
    String[] prices1 = new String[]{"100", "200", "300", "500", "800", "1000"};
    String[] prices2 = new String[]{"50", "100", "200", "500", "1000", "2000"};
    private int mChallengeType = 1; // 1金币挑战赛 2赏金挑战赛
    private int mPlayArea = 1; // 1微信区服 2企鹅区服
    private String fmid;

    public InvitePlayDialog(@NonNull Context context, String fmid) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.fmid = fmid;
        getWindow().setGravity(Gravity.BOTTOM);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        HttpClient.getBindGameInfo(context, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                BindGameInfoBean bindGameInfoBean = new Gson().fromJson(result, BindGameInfoBean.class);
                wxName = bindGameInfoBean.getData().get(0).getWxName();
                qqName = bindGameInfoBean.getData().get(0).getQqName();
                tvNickname.setText(TextUtils.isEmpty(wxName) ? "去关联微信区服账号" : wxName);
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(context, msg);
            }
        });
    }

    private void initView() {
        View view = View.inflate(context, R.layout.invite_play_layout, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        rcvPrice = view.findViewById(R.id.rcv_price);
        btnInvite = view.findViewById(R.id.btn_invite);
        tvPlayType = view.findViewById(R.id.tv_play_type);
        tvPlayArea = view.findViewById(R.id.tv_play_area);
        tvPlayArea2 = view.findViewById(R.id.tv_play_area2);
        tvNickname = view.findViewById(R.id.tv_nickname);
        RelativeLayout rlTypeArea = view.findViewById(R.id.rl_type_area);
        tvPlayArea.setText("微信区服");
        tvPlayArea2.setText("微信区服");
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rcvPrice.setLayoutManager(new GridLayoutManager(context, 3));
        adapter = new PlayPriceAdapter(new ArrayList<>());
        rcvPrice.setAdapter(adapter);
        adapter.setPlayType(mChallengeType);
        adapter.setNewData(Arrays.asList(mChallengeType == 1 ? prices1 : prices2));
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BindGameNicknameActivity.class);
                if (mPlayArea == 1) {
                    intent.putExtra("area", "微信区服");
                    intent.putExtra("nickname", wxName);
                } else {
                    intent.putExtra("area", "企鹅区服");
                    intent.putExtra("nickname", qqName);
                }
                context.startActivity(intent);
                dismiss();
            }
        });

        rlTypeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPlayInfoDialog dialog = new MyPlayInfoDialog(context);
                dialog.show();
                dialog.selectType(mChallengeType);
                dialog.selectArea(mPlayArea);
                dialog.setListener(new MyPlayInfoDialog.OnBtnClickListener() {
                    @Override
                    public void onConfirmClick(int playType, int playArea) {
                        mChallengeType = playType;
                        mPlayArea = playArea;
                        tvPlayType.setText(mChallengeType == 1 ? "金币挑战赛" : "赏金挑战赛");
                        tvPlayArea.setText(mPlayArea == 1 ? "微信区服" : "企鹅区服");
                        tvPlayArea2.setText(mPlayArea == 1 ? "微信区服" : "企鹅区服");
                        adapter.setPlayType(mChallengeType);
                        if (mPlayArea == 1) {
                            tvNickname.setText(TextUtils.isEmpty(wxName) ? "去关联微信区服账号" : wxName);
                        } else {
                            tvNickname.setText(TextUtils.isEmpty(qqName) ? "去关联企鹅区服账号" : qqName);
                        }
                    }
                });
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = mPlayArea == 1 ? wxName : qqName;
                if (TextUtils.isEmpty(nickname)) {
                    Utils.showToast(context, "请设置游戏昵称");
                    return;
                }
                HttpClient.createMatchRecord(context, nickname, mPlayArea, mChallengeType, Integer.parseInt(adapter.getSelectedItem()), fmid, new HttpCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Utils.showToast(context, "已发起约战");
                        dismiss();
                    }

                    @Override
                    public void onFailure(String msg) {
                        Utils.showToast(context, msg);
                    }
                });
            }
        });
    }
}
