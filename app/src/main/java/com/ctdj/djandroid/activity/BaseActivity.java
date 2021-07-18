package com.ctdj.djandroid.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.common.Constants;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
public class BaseActivity extends AppCompatActivity {

    protected int statusBarHeight;
    protected DisplayMetrics displayMetrics = new DisplayMetrics();

    public static final String[] ALL_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private LogoutReceiver logoutReceiver;
    private boolean canJump = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            View decorView = window.getDecorView();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            decorView.setSystemUiVisibility(0 | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.parseColor("#161824"));
        }
        getDisplayMetrics();
        initStatusBarHeight();
        registerLogoutBroadcast();
        hideWindowStatusBar(true);
    }

    private void getDisplayMetrics() {
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    private void initStatusBarHeight() {
        statusBarHeight = getSystemStatusBarHeight(this);
    }

    protected int getSystemStatusBarHeight(Context context) {
        int id = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        return id > 0 ? context.getResources().getDimensionPixelSize(id) : id;
    }

    protected void hideWindowStatusBar() {
        hideWindowStatusBar(false);
    }

    protected void hideWindowStatusBar(boolean isWhiteFont) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (isWhiteFont) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected void setWindowFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

//    protected void setTitle(String title) {
//        TextView titleTv = findViewById(R.id.tv_title);
//        if (titleTv != null) {
//            titleTv.setText(title);
//        }
//        ImageView backIv = findViewById(R.id.iv_back);
//        if (backIv != null) {
//            backIv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBackPressed();
//                }
//            });
//        }
//    }
//
//    protected void setRightClick(View.OnClickListener clickListener) {
//        TextView tvRight = findViewById(R.id.tv_right);
//        if (tvRight != null) {
//            tvRight.setVisibility(View.VISIBLE);
//            tvRight.setOnClickListener(clickListener);
//        }
//    }

    public MyApplication application() {
        return (MyApplication) getApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (logoutReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean permissionArrayGranted(String[] permissions) {
        boolean granted = true;
        for (String per : permissions) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        return granted;
    }

    protected boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void reqPermissions(String[] permissions, int request) {
        ActivityCompat.requestPermissions(this, permissions, request);
    }

    private void registerLogoutBroadcast() {
        logoutReceiver = new LogoutReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.LOGOUT_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, intentFilter);
    }

    protected class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.LOGOUT_BROADCAST.equals(intent.getAction())) {
                onLogout();
            }
        }
    }

    protected void onLogout() {
        finish();
    }

    protected void setClassicsHeader(ClassicsHeader header) {
        if (header == null) {
            return;
        }
        header.setTextSizeTitle(12);
        header.setTextSizeTime(10);
        header.setDrawableMarginRight(5);
        header.setDrawableSize(15);
    }

    protected void setClassicsFooter(ClassicsFooter footer) {
        if (footer == null) {
            return;
        }
        footer.setTextSizeTitle(12);
        footer.setDrawableMarginRight(5);
        footer.setDrawableSize(15);
    }

    @Override
    protected void onResume() {
        super.onResume();
        canJump = true;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (canJump) {
            canJump = false;
            super.startActivityForResult(intent, requestCode, options);
        }
    }
}
