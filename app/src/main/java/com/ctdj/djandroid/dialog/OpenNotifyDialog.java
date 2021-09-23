package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;

public class OpenNotifyDialog extends Dialog {
    private Context context;

    public OpenNotifyDialog(@NonNull Context context) {
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
        View view = View.inflate(context, R.layout.open_notify_dialog, null);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        setCanceledOnTouchOutside(false);
        Button btnOpenNotify = view.findViewById(R.id.btn_open);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);

        btnOpenNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Utils.openNotifySetting(context);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
