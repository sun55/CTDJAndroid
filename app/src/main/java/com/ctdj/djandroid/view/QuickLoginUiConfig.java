package com.ctdj.djandroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctdj.djandroid.activity.PhoneNumActivity;
import com.ctdj.djandroid.common.DisplayUtil;
import com.ctdj.djandroid.common.Utils;
import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.utils.LoginUiHelper;

public class QuickLoginUiConfig {

    public static UnifyUiConfig getUiConfig(final Activity context, final QuickLogin quickLogin) {
        TextView otherLogin = new TextView(context);
        otherLogin.setText("其他登录方式");
        otherLogin.setTextSize(15);
        otherLogin.setTextColor(Color.parseColor("#EBEBED"));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.bottomMargin = Utils.dip2px(context, 320);
        otherLogin.setLayoutParams(layoutParams);

        int X_OFFSET = 0;
        int BOTTOM_OFFSET = 0;
        UnifyUiConfig uiConfig = new UnifyUiConfig.Builder()
                .setBackgroundImage("quick_login_bg")
                // 状态栏
                .setStatusBarColor(Color.parseColor("#151522"))
                .setStatusBarDarkColor(false)
                // 设置导航栏
                .setNavigationTitle(" ")
                .setNavigationBackgroundColor(Color.parseColor("#151522"))
                .setHideNavigation(false)
                .setNavigationIcon("white_back_icon")
                // 设置logo
                .setHideLogo(true)
                //手机掩码
                .setMaskNumberColor(Color.WHITE)
                .setMaskNumberSize(26)
                .setMaskNumberXOffset(X_OFFSET)
                .setMaskNumberTopYOffset(160)
                .setMaskNumberBottomYOffset(BOTTOM_OFFSET)
                // 认证品牌
                .setSloganSize(15)
                .setSloganColor(Color.parseColor("#EBEBED"))
                .setSloganXOffset(X_OFFSET)
                .setSloganTopYOffset(200)
                .setSloganBottomYOffset(BOTTOM_OFFSET)
                // 登录按钮
                .setLoginBtnText("本机号码一键登录")
                .setLoginBtnTextColor(Color.parseColor("#EBEBED"))
                .setLoginBtnBackgroundRes("btn_enable_bg")
                .setLoginBtnWidth(315)
                .setLoginBtnHeight(44)
                .setLoginBtnTextSize(15)
                .setLoginBtnXOffset(X_OFFSET)
                .setLoginBtnTopYOffset(250)
                .setLoginBtnBottomYOffset(BOTTOM_OFFSET)
                // 隐私栏
                .setPrivacyTextStart("已阅读并同意")
                .setPrivacyTextColor(Color.parseColor("#A2A2A7"))
                .setPrivacyProtocolColor(Color.parseColor("#5252FE"))
                .setPrivacyMarginRight(25)
                .setCheckBoxGravity(Gravity.TOP)
                .setPrivacyState(true)
                .setPrivacySize(10)
                .setPrivacyBottomYOffset(21)
                .setPrivacyTextGravityCenter(false)
                .setPrivacyMarginLeft(DisplayUtil.dip2px(context, 20))
                .setPrivacyMarginRight(DisplayUtil.dip2px(context, 20))
                .setPrivacyTextGravityCenter(true)
                .setCheckBoxGravity(Gravity.TOP)
                .setCheckedImageName("check_box_selected")
                .setUnCheckedImageName("check_box_unselect")
                .setHidePrivacyCheckBox(true)
                .setProtocolPageNavColor(Color.WHITE)
                .addCustomView(otherLogin, "other_login", UnifyUiConfig.POSITION_IN_BODY, new LoginUiHelper.CustomViewListener() {
                    @Override
                    public void onClick(Context context, View view) {
                        context.startActivity(new Intent(context, PhoneNumActivity.class));
                        quickLogin.quitActivity();
                    }
                })
                .build(context);
        return uiConfig;
    }

}
