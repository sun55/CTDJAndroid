package com.ctdj.djandroid.common;

import android.content.Context;
import android.content.Intent;

import com.ctdj.djandroid.activity.WebViewActivity;

/**
 * @ProjectName : 咪兔
 * @Author : Sun
 * @Time : 2021/4/26 11:24
 * @Description :
 */
public class UIHelper {
    public static void showWebViewActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }
}
