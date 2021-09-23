package com.ctdj.djandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ctdj.djandroid.R;

public abstract class BaseDialog extends Dialog {

    protected Context context;
    private int layoutId;

    public BaseDialog(Context context, int layoutId) {
        super(context, R.style.dialog_style);
        this.context = context;
        this.layoutId = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        View view = View.inflate(context, layoutId, null);
        setContentView(view);
        initView(view);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        layoutParams.width = (int) (310 * displayMetrics.density);
        window.setAttributes(layoutParams);
        this.setCanceledOnTouchOutside(true);
    }

    public abstract void initView(View view);

}
