package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.FeedBackActivity;
import com.ctdj.djandroid.activity.MessageActivity;
import com.ctdj.djandroid.activity.PlayDetailActivity;
import com.ctdj.djandroid.bean.MatchOrderBean;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;

public class PlayMoreBottomDialog extends Dialog {
    private Context context;
    private TextView tvTitle;
    private TextView tvDesc;
    private Button btnCancel;
    private MatchOrderBean data;


    public PlayMoreBottomDialog(@NonNull Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
        getWindow().setGravity(Gravity.BOTTOM);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.play_more_bottom_layout, null);
        tvTitle = view.findViewById(R.id.tv_title);
        tvDesc = view.findViewById(R.id.tv_desc);
        btnCancel = view.findViewById(R.id.btn_cancel);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void fillData(MatchOrderBean data) {
        this.data = data;
        if (data.getData().getSta() == 0) { // ?????????
            tvTitle.setText("????????????");
            tvDesc.setText("???????????????????????????????????????????????????");
        } else if (data.getData().getSta() == 2) { // ???????????????
            tvTitle.setText("????????????");
            tvDesc.setText("???????????????????????????");
        } else if (data.getData().getSta() == 4) { // ???????????????
            tvTitle.setText("??????????????????");
            tvDesc.setText("???????????????????????????????????????");
        }
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getData().getSta() == 0) {
                    cancelPlay();
                } else if (data.getData().getSta() == 2) {
                    Intent intent = new Intent(context, FeedBackActivity.class);
                    intent.putExtra("from", 3);
                    intent.putExtra("challengeId", data.getData().getChallengeId());
                    context.startActivity(intent);
                    dismiss();
                } else if (data.getData().getSta() == 4) {
                    Intent intent = new Intent(context, PlayDetailActivity.class);
                    intent.putExtra("orderno", data.getData().getOrderno());
                    intent.putExtra("from", 1);
                    context.startActivity(intent);
                    dismiss();
                }
            }
        });
    }

    /**
     * ????????????
     */
    private void cancelPlay() {
        HttpClient.closeMatch(context, data.getData().getChallengeId(), new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Utils.showToast(context, "??????????????????");
                dismiss();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(context, msg);
            }
        });
    }
}
