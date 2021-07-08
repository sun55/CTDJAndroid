package com.ctdj.djandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivitySettingBinding;
import com.ctdj.djandroid.view.TitleView;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.File;
import java.math.BigDecimal;

public class SettingActivity extends BaseActivity {

    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        LinearLayoutCompat.LayoutParams l = (LinearLayoutCompat.LayoutParams) binding.titleView.getLayoutParams();
        l.topMargin = DisplayUtil.getStatusBarHeight(this);
        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });

        binding.itemPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PhoneSettingActivity.class));
            }
        });

        binding.itemPrivacySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PrivacySettingActivity.class));
            }
        });

        binding.itemRealName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, RealNameAuthActivity.class));
            }
        });

        binding.itemCleanCache.setRightText(getTotalCacheSize(this));
        binding.itemCleanCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllCache(SettingActivity.this);
            }
        });
        binding.itemPrivacySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, PrivacySettingActivity.class));
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logout(SettingActivity.this);
            }
        });
    }

    /**
     * 获取缓存值
     */
    public String getTotalCacheSize(Context context) {

        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
            //TODO 有网页清理时注意排错，是否存在/data/data/应用package目录下找不到database文件夹的问题
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
            Utils.showToast(context, "清理完成");
            binding.itemCleanCache.setRightText("0MB");
        }
    }

    /**
     * 删除某个文件
     */
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        if (dir != null) {
            return dir.delete();
        } else {
            return false;
        }
    }

    /**
     * 获取文件
     */
    public long getFolderSize(File file) {
        long size = 0;
        if (file != null) {
            File[] fileList = file.listFiles();
            if (fileList != null && fileList.length > 0) {
                for (int i = 0; i < fileList.length; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            }
        }
        return size;
    }

    /**
     * 格式化单位
     */
    public String getFormatSize(double size) {
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = BigDecimal.valueOf(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}