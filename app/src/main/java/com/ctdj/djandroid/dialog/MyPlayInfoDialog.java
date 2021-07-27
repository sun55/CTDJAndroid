package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;

/**
 * @Author : Sun
 * @Time : 2021/7/21 14:58
 * @Description :我的比赛信息
 */
public class MyPlayInfoDialog extends Dialog {
    private Context context;
    private int playType;
    private int playArea;
    TextView tvType1;
    TextView tvType2;
    TextView tvArea1;
    TextView tvArea2;
    ImageView ivType1;
    ImageView ivType2;
    ImageView ivArea1;
    ImageView ivArea2;
    RelativeLayout rlType1;
    RelativeLayout rlType2;
    RelativeLayout rlArea1;
    RelativeLayout rlArea2;
    Button btnConfirm;

    public MyPlayInfoDialog(@NonNull Context context) {
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
        View view = View.inflate(context, R.layout.my_play_info_layout, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        tvType1 = view.findViewById(R.id.tv_play_type_1);
        tvType2 = view.findViewById(R.id.tv_play_type_2);
        tvArea1 = view.findViewById(R.id.tv_play_area_1);
        tvArea2 = view.findViewById(R.id.tv_play_area_2);
        ivType1 = view.findViewById(R.id.iv_play_item_type_1);
        ivType2 = view.findViewById(R.id.iv_play_item_type_2);
        ivArea1 = view.findViewById(R.id.iv_play_item_area_1);
        ivArea2 = view.findViewById(R.id.iv_play_item_area_2);
        rlType1 = view.findViewById(R.id.rl_play_item_type_1);
        rlType2 = view.findViewById(R.id.rl_play_item_type_2);
        rlArea1 = view.findViewById(R.id.rl_play_item_area_1);
        rlArea2 = view.findViewById(R.id.rl_play_item_area_2);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        selectType(1);
        selectArea(1);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmClick(playType, playArea);
                }
            }
        });

        rlType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType(1);
            }
        });
        rlType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType(2);
            }
        });

        rlArea1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectArea(1);
            }
        });

        rlArea2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectArea(2);
            }
        });
    }

    public void selectType(int i) {
        playType = i;
        if (i == 1) {
            rlType1.setBackgroundResource(R.drawable.play_item_select_bg);
            ivType1.setVisibility(View.VISIBLE);
            rlType2.setBackgroundResource(R.drawable.radius_5_22252f);
            ivType2.setVisibility(View.GONE);
        } else {
            rlType1.setBackgroundResource(R.drawable.radius_5_22252f);
            ivType1.setVisibility(View.GONE);
            rlType2.setBackgroundResource(R.drawable.play_item_select_bg);
            ivType2.setVisibility(View.VISIBLE);
        }
    }

    public void selectArea(int i) {
        playArea = i;
        if (i == 1) {
            rlArea1.setBackgroundResource(R.drawable.play_item_select_bg);
            ivArea1.setVisibility(View.VISIBLE);
            rlArea2.setBackgroundResource(R.drawable.radius_5_22252f);
            ivArea2.setVisibility(View.GONE);
        } else {
            rlArea1.setBackgroundResource(R.drawable.radius_5_22252f);
            ivArea1.setVisibility(View.GONE);
            rlArea2.setBackgroundResource(R.drawable.play_item_select_bg);
            ivArea2.setVisibility(View.VISIBLE);
        }
    }

    private OnBtnClickListener listener;

    public OnBtnClickListener getListener() {
        return listener;
    }

    public void setListener(OnBtnClickListener listener) {
        this.listener = listener;
    }

    public interface OnBtnClickListener {
        void onConfirmClick(int playType, int playArea);
    }
}
