package com.ctdj.djandroid.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.ResourceUtils;

public class LoadingDialog extends ProgressDialog {

    private Context context;

    public LoadingDialog(Context context) {
        super(context, ResourceUtils.getStyleId(context, "loading_style"));
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
//        setContentView(R.layout.loading_dialog);
//        ImageView loadingIv = findViewById(R.id.load_iv);
//        Glide.with(context).load(R.drawable.load_anim).into(loadingIv);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        window.setBackgroundDrawableResource(R.drawable.half_translate_white_bg);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = 0.0f;
        layoutParams.width = DisplayUtil.getScreenWidthPx(context);
        layoutParams.height = DisplayUtil.getScreenHightPx(context);
        window.setAttributes(layoutParams);
    }

}
