package com.ctdj.djandroid.common;

import android.annotation.SuppressLint;
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

import java.text.DecimalFormat;
import java.text.ParseException;
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
    private final static String[] constellationArr = new String[]{"?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????"};

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
        // ?????????????????? ??????????????????????????????????????????????????????????????????
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
                LogUtil.i("????????????????????????");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtil.e("???????????????????????? i:" + i + ", s:" + s);
            }
        });
    }

    /**
     * ????????????????????????
     *
     * @param context ?????????
     * @param string  ?????????????????????
     * @param color   ?????????????????????
     * @param start   ????????????
     * @param end     ????????????
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
     * ?????????????????????????????????
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
     * ????????????Activity ?????????????????????
     *
     * @param context
     * @param className ??????????????????
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
     * @param str1      ????????????
     * @param str2
     * @param colorStr1 ??????????????????
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

    /**
     * ??????????????????????????????
     *
     * @param endTime
     * @return
     */
    public static long getLeftTime(String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        try {
            Date endDate = sdf.parse(endTime);
            long end = endDate.getTime();
            long current = new Date().getTime();
            if (end <= current) {
                return 0;
            } else {
                return end - current;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public static String getRankCountDown(long timeMills) {
        if (timeMills <= 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("d/HH/mm/ss", Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String[] strings = sdf.format(new Date(timeMills)).split("/");
        String time = "?????????????????? " + strings[0] + "D " + strings[1] + "h " + strings[2] + "m " + strings[3] + "s";
        return time;
    }

    public static String constellation(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String constellation = "";
        if (month == 1 && day >= 20 || month == 2 && day <= 18) {
            constellation = "?????????";
        }
        if (month == 2 && day >= 19 || month == 3 && day <= 20) {
            constellation = "?????????";
        }
        if (month == 3 && day >= 21 || month == 4 && day <= 19) {
            constellation = "?????????";
        }
        if (month == 4 && day >= 20 || month == 5 && day <= 20) {
            constellation = "?????????";
        }
        if (month == 5 && day >= 21 || month == 6 && day <= 21) {
            constellation = "?????????";
        }
        if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 9 && day >= 23 || month == 10 && day <= 23) {
            constellation = "?????????";
        }
        if (month == 10 && day >= 24 || month == 11 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 11 && day >= 23 || month == 12 && day <= 21) {
            constellation = "?????????";
        }
        if (month == 12 && day >= 22 || month == 1 && day <= 19) {
            constellation = "?????????";
        }
        return constellation;
    }

    /**
     * ??????
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
            constellation = "?????????";
        }
        if (month == 2 && day >= 19 || month == 3 && day <= 20) {
            constellation = "?????????";
        }
        if (month == 3 && day >= 21 || month == 4 && day <= 19) {
            constellation = "?????????";
        }
        if (month == 4 && day >= 20 || month == 5 && day <= 20) {
            constellation = "?????????";
        }
        if (month == 5 && day >= 21 || month == 6 && day <= 21) {
            constellation = "?????????";
        }
        if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 9 && day >= 23 || month == 10 && day <= 23) {
            constellation = "?????????";
        }
        if (month == 10 && day >= 24 || month == 11 && day <= 22) {
            constellation = "?????????";
        }
        if (month == 11 && day >= 23 || month == 12 && day <= 21) {
            constellation = "?????????";
        }
        if (month == 12 && day >= 22 || month == 1 && day <= 19) {
            constellation = "?????????";
        }
        return constellation;
    }

    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //?????????????????????????????????????????????
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //????????????
        int monthNow = cal.get(Calendar.MONTH);  //????????????
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //????????????
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //???????????????
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//??????????????????????????????????????????
            } else {
                age--;//??????????????????????????????????????????
            }
        }
        return age;
    }

    public static int getAge(String birthDay) {
        Date date = getDateByString(birthDay, "");
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //?????????????????????????????????????????????
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //????????????
        int monthNow = cal.get(Calendar.MONTH);  //????????????
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //????????????
        cal.setTime(date);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //???????????????
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//??????????????????????????????????????????
            } else {
                age--;//??????????????????????????????????????????
            }
        }
        return age;
    }

    public static String getFilePathByUri_BELOWAPI11(Uri uri, Context context) {
        // ??? content:// ??????????????????  content://media/external/file/960
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
     * ??????????????????
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
     * ????????????????????????
     *
     * @param content
     */
    public static void copy(Context context, String content) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        Utils.showToast(context, "????????????");
    }

    /**
     * ??????
     */
    @SuppressLint("MissingPermission")
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
     * ???????????????app
     *
     * @param context ?????????????????? com.tencent.tmgp.sgame
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
            showToast(context, "?????????????????????");
        }
    }

    /**
     * ????????????
     *
     * @param textView
     * @param typefaceFile
     */
    public static void setTextTypeface(TextView textView, String typefaceFile) {
        Typeface typeface = Typeface.createFromAsset(MyApplication.getInstance().getAssets(), typefaceFile);
        textView.setTypeface(typeface);
    }

    /**
     * ????????????
     *
     * @param view
     * @param duration
     * @param repeatCount
     */
    public static void playRotateAnim(View view, int duration, int repeatCount) {
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(duration);//????????????????????????
        rotate.setRepeatCount(repeatCount);//??????????????????
        rotate.setFillAfter(true);//???????????????????????????????????????????????????
        rotate.setStartOffset(10);//????????????????????????
        view.setAnimation(rotate);
    }

    /**
     * ????????????
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
     * ?????????????????????????????????2???
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

    public static boolean checkNotifyPermission(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        // areNotificationsEnabled??????????????????????????????????????????API 19?????????19??????????????????????????????????????????true?????????????????????????????????????????????
        LogUtil.e("?????????????????????" + manager.areNotificationsEnabled());
        return manager.areNotificationsEnabled();
    }

    public static void openNotifySetting(Context context) {
        try {
            // ??????isOpened?????????????????????????????????????????????AppInfo??????????????????App????????????
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //????????????????????? API 26, ???8.0??????8.0??????????????????
            intent.putExtra(EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);

            //????????????????????? API21??????25?????? 5.0??????7.1 ???????????????????????????
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);

            // ??????6 -MIUI9.6-8.0.0??????????????????????????????????????????????????????"????????????????????????"??????????????????????????????????????????????????????????????????I'm not ok!!!
            //  if ("MI 6".equals(Build.MODEL)) {
            //      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            //      Uri uri = Uri.fromParts("package", getPackageName(), null);
            //      intent.setData(uri);
            //      // intent.setAction("com.android.settings/.SubSettings");
            //  }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // ?????????????????????????????????????????????????????????3??????OC105 API25
            Intent intent = new Intent();

            //??????????????????????????????????????????????????????????????????
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        }
    }

    /**
     * double ??????????????????
     *
     * @param d
     * @return
     */
    public static String transDouble2(double d) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(d);
    }

    /**
     * ???????????????????????????
     * @param createTime
     * @return
     */
    public static String getNotifyTime(String createTime) {
        if (createTime.length() != 19) {
            return "";
        }
        Date date = getDateByString(createTime, "");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        Calendar calendar2 = Calendar.getInstance();
        int month1 = calendar1.get(Calendar.MONTH) + 1;
        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
        int month2 = calendar2.get(Calendar.MONTH) + 1;
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        String format;
        if (month1 == month2 && day1 == day2) {
            format = "HH:mm";
        } else {
            format = "MM.dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return sdf.format(date);
    }

}
