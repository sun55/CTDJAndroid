package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.MessageActivity;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.net.HttpCallback;
import com.ctdj.djandroid.net.HttpClient;
import com.ctdj.djandroid.view.CircleImageView;

import org.jetbrains.annotations.NotNull;

/**
 * 接受约战
 */
public class ReceiveInviteDialog extends Dialog {
    private Context context;
    private int challengeType = 1;
    private String fmid;
    private String targetName;
    private String headimg;
    private ImageView ivChallengeType;
    private ImageView ivClose;
    private CircleImageView avatar;
    private TextView tvCountDown;
    private TextView tvEnterRoom;

    public ReceiveInviteDialog(@NonNull @NotNull Context context, int challengeType, String fmid, String targetName, String headimg) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.challengeType = challengeType;
        this.fmid = fmid;
        this.targetName = targetName;
        this.headimg = headimg;
        getWindow().setGravity(Gravity.TOP);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.receive_invite_layout, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        ivChallengeType = view.findViewById(R.id.iv_challenge_type);
        ivClose = view.findViewById(R.id.iv_close);
        tvEnterRoom = view.findViewById(R.id.tv_enter_room);
        tvCountDown = view.findViewById(R.id.tv_count_down);
        avatar = view.findViewById(R.id.avatar);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Glide.with(context).load(headimg).into(avatar);
        ivChallengeType.setBackgroundResource(challengeType == 1 ? R.drawable.receive_invite_icon_1 : R.drawable.receive_invite_icon_2);
        tvEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterRoom();
            }
        });
        countDown();
    }

    private void countDown() {
        CountDownTimer timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                dismiss();
            }
        };
        timer.start();
    }

    private void enterRoom() {
        HttpClient.reciveMatchInvite(context, fmid, new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("target_name", targetName);
                intent.putExtra("user_id", fmid);
                context.startActivity(intent);
                dismiss();
            }

            @Override
            public void onFailure(String msg) {
                Utils.showToast(context, msg);
            }
        });
    }
}
