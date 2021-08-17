package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;

/**
 * 赛事奖励
 */
public class PlayRewardDialog extends Dialog {
    private Context context;
    private RelativeLayout rl1;
    private RelativeLayout rl2;
    private RelativeLayout rl3;
    private RelativeLayout rl4;
    private RelativeLayout rl5;
    private RelativeLayout rl6;
    private RelativeLayout rl7;
    private RelativeLayout rl8;
    private RelativeLayout rl9;
    private RelativeLayout rl10;
    private TextView tvLeft1;
    private TextView tvLeft2;
    private TextView tvLeft3;
    private TextView tvLeft4;
    private TextView tvLeft5;
    private TextView tvLeft6;
    private TextView tvLeft7;
    private TextView tvLeft8;
    private TextView tvLeft9;
    private TextView tvLeft10;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;
    private TextView tv7;
    private TextView tv8;
    private TextView tv9;
    private TextView tv10;

    public PlayRewardDialog(@NonNull Context context) {
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
        View view = View.inflate(context, R.layout.play_reward_layout, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        rl1 = view.findViewById(R.id.rl_1);
        rl2 = view.findViewById(R.id.rl_2);
        rl3 = view.findViewById(R.id.rl_3);
        rl4 = view.findViewById(R.id.rl_4);
        rl5 = view.findViewById(R.id.rl_5);
        rl6 = view.findViewById(R.id.rl_6);
        rl7 = view.findViewById(R.id.rl_7);
        rl8 = view.findViewById(R.id.rl_8);
        rl9 = view.findViewById(R.id.rl_9);
        rl10 = view.findViewById(R.id.rl_10);
        tvLeft1 = view.findViewById(R.id.tv_left_1);
        tvLeft2 = view.findViewById(R.id.tv_left_2);
        tvLeft3 = view.findViewById(R.id.tv_left_3);
        tvLeft4 = view.findViewById(R.id.tv_left_4);
        tvLeft5 = view.findViewById(R.id.tv_left_5);
        tvLeft6 = view.findViewById(R.id.tv_left_6);
        tvLeft7 = view.findViewById(R.id.tv_left_7);
        tvLeft8 = view.findViewById(R.id.tv_left_8);
        tvLeft9 = view.findViewById(R.id.tv_left_9);
        tvLeft10 = view.findViewById(R.id.tv_left_10);
        tv1 = view.findViewById(R.id.tv_1);
        tv2 = view.findViewById(R.id.tv_2);
        tv3 = view.findViewById(R.id.tv_3);
        tv4 = view.findViewById(R.id.tv_4);
        tv5 = view.findViewById(R.id.tv_5);
        tv6 = view.findViewById(R.id.tv_6);
        tv7 = view.findViewById(R.id.tv_7);
        tv8 = view.findViewById(R.id.tv_8);
        tv9 = view.findViewById(R.id.tv_9);
        tv10 = view.findViewById(R.id.tv_10);
        Utils.setTextTypeface(tv1, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv2, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv3, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv4, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv5, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv6, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv7, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv8, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv9, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tv10, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft1, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft2, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft3, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft4, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft5, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft6, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft7, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft8, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft9, "fonts/avantibold.ttf");
        Utils.setTextTypeface(tvLeft10, "fonts/avantibold.ttf");
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void fillData(int no1, int no2, int no3, int no4, int no5, int no6to10, int no11to20, int no21to50, int no51to80, int no81to100) {
        if (no1 == 0) {
            return;
        }
        rl1.setVisibility(View.VISIBLE);
        tv1.setText(no1 + "");

        if (no2 == 0) {
            return;
        }

        rl2.setVisibility(View.VISIBLE);
        tv2.setText(no2 + "");

        if (no3 == 0) {
            return;
        }

        rl3.setVisibility(View.VISIBLE);
        tv3.setText(no3 + "");

        if (no4 == 0) {
            return;
        }

        rl4.setVisibility(View.VISIBLE);
        tv4.setText(no4 + "");

        if (no5 == 0) {
            return;
        }

        rl5.setVisibility(View.VISIBLE);
        tv5.setText(no5 + "");

        if (no6to10 == 0) {
            return;
        }

        rl6.setVisibility(View.VISIBLE);
        tv6.setText(no6to10 + "");

        if (no11to20 == 0) {
            return;
        }

        rl7.setVisibility(View.VISIBLE);
        tv7.setText(no11to20 + "");

        if (no21to50 == 0) {
            return;
        }

        rl8.setVisibility(View.VISIBLE);
        tv8.setText(no21to50 + "");

        if (no51to80 == 0) {
            return;
        }

        rl9.setVisibility(View.VISIBLE);
        tv9.setText(no51to80 + "");

        if (no81to100 == 0) {
            return;
        }

        rl10.setVisibility(View.VISIBLE);
        tv10.setText(no81to100 + "");
    }
}
