package com.ctdj.djandroid.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ctdj.djandroid.MyApplication;
import com.ctdj.djandroid.R;
import com.ctdj.djandroid.activity.LoginActivity;
import com.ctdj.djandroid.dialog.LoadingDialog;
import com.ctdj.djandroid.view.MyImageSpan;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.opensource.svgaplayer.SVGAParser;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnPreparedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Notification.EXTRA_CHANNEL_ID;
import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static com.sdk.base.framework.utils.app.AppUtils.getPackageName;

public class Utils {

    public static void showOrHidePassword(Context context, EditText editText, ImageView imageView) {
        TransformationMethod method = editText.getTransformationMethod();
        if (method == HideReturnsTransformationMethod.getInstance()) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            imageView.setImageResource(R.mipmap.eye_hide_icon);
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            imageView.setImageResource(R.mipmap.eye_show_icon);
        }
        Spannable text = editText.getText();
        if (text != null) {
            Selection.setSelection(text, text.length());
        }
    }

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

    public static String getChannelId(Context context) {
        String channelId = null;
        try {
            channelId = String.valueOf(context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getInt("CHANNEL_ID"));
        } catch (Exception e) {
            try {
                channelId = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("CHANNEL_ID");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
        }
        return channelId;
    }

    public static String getFilePathByUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static String getFileSuffix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }

    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private final static String[] constellationArr = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};

    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void showSoftKeyboard(Context context, EditText editText) {
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 0);
    }

    public static void logout(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.LOGOUT_BROADCAST));
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        MyApplication.getInstance().cleanUserInfo();
        // 清除所有缓存 例如：压缩、裁剪、视频、音频所生成的临时文件
        PictureFileUtils.deleteAllCacheDirFile(context);
        TUIKit.logout(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                LogUtil.i("im logout success");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                LogUtil.i("im logout error: module:" + module + ", errCode:" + errCode + ", errMsg:" + errMsg);
            }
        });
        XGPushManager.unregisterPush(MyApplication.getInstance(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                LogUtil.i("腾讯推送退出成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtil.e("腾讯推送退出失败 i:" + i + ", s:" + s);
            }
        });
    }

    /**
     * 动态设置字体颜色
     *
     * @param context 上下文
     * @param string  需要处理的字串
     * @param color   需要设置的颜色
     * @param start   开始位置
     * @param end     结束位置
     * @param tv      TextView
     */
    public static void setColorText(Context context, String string, int color, int start, int end, TextView tv) {
        tv.setText(getColorText(context, string, color, start, end));
    }

    public static SpannableString getColorText(Context context, String string, int color, int start, int end) {
        SpannableString ss = new SpannableString(string);
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), start, end == -1 ? string.length() : end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static String getTimeBySecond(int second) {
        if (second > 10 * 60) {
            if (second % 60 >= 10) {
                return second / 60 + ":" + second % 60;
            } else {
                return second / 60 + ":0" + second % 60;
            }
        } else if (second > 60) {
            if (second % 60 >= 10) {
                return "0" + second / 60 + ":" + second % 60;
            } else {
                return "0" + second / 60 + ":0" + second % 60;
            }
        } else if (second >= 10) {
            return "00:" + second % 60;
        } else {
            return "00:0" + second % 60;
        }
    }

    static long lastClickTime;

    /**
     * 防止在短时间内重复点击
     */
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断某个Activity 界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static SpannableString getTextWithImageSpan(Context context, SpannableString spannableString, int... drawableId) {
        if (drawableId != null && drawableId.length > 0) {
            for (int i = 0; i < drawableId.length; i++) {
                Drawable drawable = context.getResources().getDrawable(drawableId[i]);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                spannableString.setSpan(new MyImageSpan(drawable), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * @param color
     * @param str1      普通文字
     * @param str2
     * @param colorStr1 带颜色的文字
     * @param colorStr2
     * @return
     */
    public static Spanned setTwoColor(String color, String str1, String str2, String colorStr1, String colorStr2) {
        return Html.fromHtml(str1 + "<font color=#" + color + ">" + colorStr1 + "</font>"
                + str2 + "<font color=#" + color + ">" + colorStr2 + "</font>");
    }

    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (context == null || TextUtils.isEmpty(msg)) {
            return;
        }
        try {
            if (toast != null) {
                toast.cancel();
                toast = null;
            }
//            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast = new Toast(context);
            TextView textView = new TextView(context);
            textView.setBackgroundResource(R.drawable.custom_toast_bg);
            textView.setPadding(DisplayUtil.dip2px(context, 20), DisplayUtil.dip2px(context, 12),
                    DisplayUtil.dip2px(context, 20), DisplayUtil.dip2px(context, 12));
            textView.setTextColor(Color.WHITE);
            textView.setText(msg);
            textView.setGravity(Gravity.CENTER);
            toast.setView(textView);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            if (toast != null) {
                toast.cancel();
                toast = null;
            }
        }
    }

    public static String transNum(int num) {
        if (num >= 1000) {
            return num / 1000 + "." + (num % 1000) / 100 + "k";
        } else {
            return "" + num;
        }
    }

    private static ProgressDialog loadingDialog;

    public static void showLoadingDialog(Context context) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(true);
            loadingDialog.show();
        }
    }

    public static void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public static Date getDateByString(String time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String constellation(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String constellation = "";
        if (month == 1 && day >= 20 || month == 2 && day <= 18) {
            constellation = "水瓶座";
        }
        if (month == 2 && day >= 19 || month == 3 && day <= 20) {
            constellation = "双鱼座";
        }
        if (month == 3 && day >= 21 || month == 4 && day <= 19) {
            constellation = "白羊座";
        }
        if (month == 4 && day >= 20 || month == 5 && day <= 20) {
            constellation = "金牛座";
        }
        if (month == 5 && day >= 21 || month == 6 && day <= 21) {
            constellation = "双子座";
        }
        if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            constellation = "巨蟹座";
        }
        if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            constellation = "狮子座";
        }
        if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            constellation = "处女座";
        }
        if (month == 9 && day >= 23 || month == 10 && day <= 23) {
            constellation = "天秤座";
        }
        if (month == 10 && day >= 24 || month == 11 && day <= 22) {
            constellation = "天蝎座";
        }
        if (month == 11 && day >= 23 || month == 12 && day <= 21) {
            constellation = "射手座";
        }
        if (month == 12 && day >= 22 || month == 1 && day <= 19) {
            constellation = "摩羯座";
        }
        return constellation;
    }

    /**
     * 星座
     *
     * @return
     */
    public static String constellation(String birthday) {
        Date date = getDateByString(birthday, "");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String constellation = "";
        if (month == 1 && day >= 20 || month == 2 && day <= 18) {
            constellation = "水瓶座";
        }
        if (month == 2 && day >= 19 || month == 3 && day <= 20) {
            constellation = "双鱼座";
        }
        if (month == 3 && day >= 21 || month == 4 && day <= 19) {
            constellation = "白羊座";
        }
        if (month == 4 && day >= 20 || month == 5 && day <= 20) {
            constellation = "金牛座";
        }
        if (month == 5 && day >= 21 || month == 6 && day <= 21) {
            constellation = "双子座";
        }
        if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            constellation = "巨蟹座";
        }
        if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            constellation = "狮子座";
        }
        if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            constellation = "处女座";
        }
        if (month == 9 && day >= 23 || month == 10 && day <= 23) {
            constellation = "天秤座";
        }
        if (month == 10 && day >= 24 || month == 11 && day <= 22) {
            constellation = "天蝎座";
        }
        if (month == 11 && day >= 23 || month == 12 && day <= 21) {
            constellation = "射手座";
        }
        if (month == 12 && day >= 22 || month == 1 && day <= 19) {
            constellation = "摩羯座";
        }
        return constellation;
    }

    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;
    }

    public static int getAge(String birthDay) {
        Date date = getDateByString(birthDay, "");
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(date);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            } else {
                age--;//当前月份在生日之前，年龄减一
            }
        }
        return age;
    }

    public static String getFilePathByUri_BELOWAPI11(Uri uri, Context context) {
        // 以 content:// 开头的，比如  content://media/external/file/960
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            String path = null;
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        return null;
    }

    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(
                context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermissions(Context context, String[] permissions) {
        boolean b = true;
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                b = false;
            }
        }
        return b;
    }

    private static SVGAParser svgaParser;

    /**
     * 解析动画资源
     *
     * @param context
     * @param fileName
     * @param parseCompletion
     */
    public static void parserSvgaAnim(Context context, String fileName, SVGAParser.ParseCompletion parseCompletion) {
        if (svgaParser == null) {
            svgaParser = new SVGAParser(context);
        }
        svgaParser.decodeFromAssets(fileName, parseCompletion);
    }

    private static WlMusic wlMusic;

    public static void playAudio(String url) {
        LogUtil.e("url:" + url);
        if (wlMusic == null) {
            wlMusic = WlMusic.getInstance();
            wlMusic.setSource(url);
            wlMusic.setPlayCircle(false);
            wlMusic.setVolume(100);
            wlMusic.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    LogUtil.e("onPrepared");
                    wlMusic.start();
                }
            });
            wlMusic.prePared();
        } else {
            if (wlMusic.isPlaying()) {
                wlMusic.stop();
            } else {
                wlMusic.setSource(url);
                wlMusic.setPlayCircle(false);
                wlMusic.setVolume(100);
                wlMusic.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        wlMusic.start();
                    }
                });
                wlMusic.prePared();
            }
        }
    }

    public static boolean isAudioPlaying() {
        if (wlMusic == null || !wlMusic.isPlaying()) {
            return false;
        } else {
            return true;
        }
    }

    public static void stopAudio() {
        if (wlMusic != null && wlMusic.isPlaying()) {
            wlMusic.stop();
        }
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static void copy(Context context, String content) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        Utils.showToast(context, "复制成功");
    }

    /**
     * 震动
     */
    public static void vibrator() {
        Vibrator vibrator = (Vibrator) MyApplication.getInstance().getSystemService(MyApplication.getInstance().VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    public static void previewSingleImage(Activity context, String imageUrl) {
        List<LocalMedia> list = new ArrayList<>();
        LocalMedia localMedia = new LocalMedia();
        localMedia.setCutPath(imageUrl);
        localMedia.setCut(true);
        localMedia.setCompressed(false);
        list.add(localMedia);
        PictureSelector.create(context)
                .themeStyle(R.style.picture_default_style)
                .isNotPreviewDownload(true)
                .imageEngine(GlideEngine.createGlideEngine())
                .openExternalPreview(0, list);
    }

    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拉起第三方app
     *
     * @param context 王者荣耀包名 com.tencent.tmgp.sgame
     */
    public static void launchApp(Context context) {
        if (Utils.isApkInstalled(context, "com.tencent.tmgp.sgame")) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.tmgp.sgame");
            if (intent != null) {
                intent.putExtra("type", "110");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } else {
            showToast(context, "未安装王者荣耀");
        }
    }

    /**
     * 设置字体
     *
     * @param textView
     * @param typefaceFile
     */
    public static void setTextTypeface(TextView textView, String typefaceFile) {
        Typeface typeface = Typeface.createFromAsset(MyApplication.getInstance().getAssets(), typefaceFile);
        textView.setTypeface(typeface);
    }

    /**
     * 旋转动画
     *
     * @param view
     * @param duration
     * @param repeatCount
     */
    public static void playRotateAnim(View view, int duration, int repeatCount) {
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(duration);//设置动画持续周期
        rotate.setRepeatCount(repeatCount);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);//执行前的等待时间
        view.setAnimation(rotate);
    }

    /**
     * 缩放动画
     *
     * @param view
     * @param duration
     * @param repeatCount
     */
    public static void playScaleAnim(View view, int duration, int repeatCount, int startOffset, Animation.AnimationListener animationListener) {
        ScaleAnimation animation = new ScaleAnimation(1, 0.5f, 1, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(repeatCount);
        animation.setStartOffset(startOffset);
        animation.setAnimationListener(animationListener);
        view.setAnimation(animation);
    }

    /**
     * 获取字符串长度，汉子占2位
     *
     * @param content
     * @return
     */
    public static int getStrLength(String content) {
        int count = 0;
        if (!TextUtils.isEmpty(content)) {
            for (int i = 0; i < content.length(); i++) {
                char item = content.charAt(i);
                if (item < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }
        }
        return count;
    }

    public static boolean checkNotifyPermission (Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        return manager.areNotificationsEnabled();
    }

    public static void openNotifySetting(Context context) {
        try {
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);

            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);

            // 小米6 -MIUI9.6-8.0.0系统，是个特例，通知设置界面只能控制"允许使用通知圆点"——然而这个玩意并没有卵用，我想对雷布斯说：I'm not ok!!!
            //  if ("MI 6".equals(Build.MODEL)) {
            //      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            //      Uri uri = Uri.fromParts("package", getPackageName(), null);
            //      intent.setData(uri);
            //      // intent.setAction("com.android.settings/.SubSettings");
            //  }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            Intent intent = new Intent();

            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }

}
