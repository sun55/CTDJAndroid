package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;

public class NormalDialog extends Dialog {

    private TextView titleTv;
    private TextView leftTv;
    private TextView rightTv;
    private OnBtnClickListener onBtnClickListener;
    private int count = 10;
    private CountDownHandler handler;
    Context context;

    public NormalDialog(Context context) {
        super(context, R.style.dialog_style);
        this.context = context;
        getWindow().setGravity(Gravity.CENTER);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void initView() {
        View view = View.inflate(context, R.layout.normal_dialog, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        setCanceledOnTouchOutside(false);
        titleTv = view.findViewById(R.id.title_tv);
        leftTv = view.findViewById(R.id.left_tv);
        rightTv = view.findViewById(R.id.right_tv);
        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnClickListener != null) {
                    onBtnClickListener.onLeftBtnClick();
                }
                dismiss();
            }
        });
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnClickListener != null) {
                    onBtnClickListener.onRightBtnClick();
                }
                dismiss();
            }
        });
    }

    public NormalDialog setTitle(String title) {
        titleTv.setText(title);
        return this;
    }

    public NormalDialog setLeftBtnText(String text) {
        leftTv.setText(text);
        return this;
    }

    public NormalDialog setRightBtnText(String text) {
        rightTv.setText(text);
        return this;
    }

    public NormalDialog setBtnClickListener(OnBtnClickListener onBtnClickListener) {
        this.onBtnClickListener = onBtnClickListener;
        return this;
    }

    public void setRightBtnEnable(boolean enable) {
        rightTv.setEnabled(enable);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void countDown() {
        handler = new CountDownHandler();
        handler.sendEmptyMessageDelayed(0, 100);
    }

    public interface OnBtnClickListener {
        void onLeftBtnClick();

        void onRightBtnClick();
    }

    private class CountDownHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (count > 0) {
                rightTv.setText(count + " s");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count--;
                        sendEmptyMessage(0);
                    }
                }, 1000);
            } else {
                count = 10;
                rightTv.setText("确定注销");
                rightTv.setEnabled(true);
            }
        }
    }
}
