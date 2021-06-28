package com.ctdj.djandroid.net;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mt.y2021.activity.LoginActivity;
import com.mt.y2021.common.Constants;
import com.mt.y2021.common.LogUtil;
import com.mt.y2021.common.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpCaller {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/x-www-form-urlencoded");
    private static final MediaType FILE = MediaType.get("multipart/form-data");

    private static OkHttpClient okHttpClient;

    public static void doPost(final Context context, String url, HashMap<String, Object> params, final HttpCallback callback) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                    .build();
        }

        try {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put("sign", createSign(params));
            StringBuilder sb = new StringBuilder();
            for (String key : params.keySet()) {
                sb.append(key + "=" + URLEncoder.encode(String.valueOf(params.get(key)), "UTF-8") + "&");
            }
            RequestBody body = RequestBody.create(sb.toString(), MEDIA_TYPE);
            LogUtil.i("+++" + url + ":Params+++" + new Gson().toJson(params));

            final Request request = new Request.Builder()
                    .url(API.BASE_URL + url)
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LogUtil.e("+++" + url + ":Fail+++" + e.toString());
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                failure();
                            }
                        });
                    } else {
                        failure();
                    }
                }

                private void failure() {
//                    Utils.showToast(context, "服务器连接失败，请稍后重试");
                    if (callback != null) {
                        callback.onFailure("服务器连接失败，请稍后重试");
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    final String result = response.body().string();
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                success(result);
                            }
                        });
                    } else {
                        success(result);
                    }
                }

                private void success(String result) {
                    try {
                        LogUtil.i("+++" + url + ":Result+++" + result);
                        if (TextUtils.isEmpty(result)) {
                            if (callback != null) {
                                callback.onFailure("服务器错误，请稍后重试");
                            }
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } else if (code == 300) {
                            if (!(context instanceof LoginActivity)) {
                                Utils.logout(context);
                            }
                            if (context instanceof Activity) {
                                Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                                ((Activity) context).finish();
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure(jsonObject.getString("msg"));
                                callback.onFailure(jsonObject.getInt("code"), jsonObject.getString("msg"), result);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        if (callback != null) {
//                            callback.onFailure("服务器错误，请稍后重试");
//                        }
//                        Utils.showToast(context, "数据解析异常");
                        LogUtil.e("+++" + url + ":Exception+++" + e.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("+++" + url + ":Exception+++" + e.toString());
        }
    }

    /**
     * 创建签名
     *
     * @param params
     */
    private static String createSign(HashMap<String, Object> params) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> keys = new ArrayList<>();
            keys.addAll(params.keySet());
            // 使根据指定比较器产生的顺序对指定对象数组进行排序。
            Collections.sort(keys, Collator.getInstance(Locale.ENGLISH));
            for (String s : keys) {
                stringBuilder.append(params.get(s));
            }
            stringBuilder.append(Constants.API_SIGN);
            String sign = MD5Encode(URLEncoder.encode(stringBuilder.toString(), "UTF-8"));
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * MD5加密算法. 用法：MD5Encrypt.MD5Encode("123456")
     *
     * @param origin 原始密码
     * @return String MD5加密后的密码
     */
    public static String MD5Encode(String origin) {
        String resultString = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToString(md.digest(origin.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));// 若使用本函数转换则可得到加密结果的16进制表示，即数字字母混合的形式
            // resultSb.append(byteToNumString(b[i]));//使用本函数则返回加密结果的10进制数字字串，即全数字形式
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    public static void uploadImageFiles(final Context context, String url, int type, List<String> filePaths, final HttpCallback callback) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                    .build();
        }

        try {
            MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
            multiBuilder.setType(MultipartBody.FORM);
            StringBuilder sbFilePath = new StringBuilder();
            for (String filePath : filePaths) {
                File file = new File(filePath);
                sbFilePath.append(file.getName());
                MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
                RequestBody filebody = MultipartBody.create(MEDIA_TYPE_JPG, file);
                //这里是 封装上传图片参数
                multiBuilder.addFormDataPart("files", file.getName(), filebody);
                //参数以添加header方式将参数封装，否则上传参数为空
                // 设置请求体
                //这里是 封装上传图片参数
//                multiBuilder.addFormDataPart("file", file.getName(), filebody);
            }
            // 封装请求参数,这里最重要
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "1");
            params.put("files", sbFilePath.toString());
            //参数以添加header方式将参数封装，否则上传参数为空
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    multiBuilder.addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, params.get(key)));
                }
            }
            RequestBody body = multiBuilder.build();


//            RequestBody body = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("type", String.valueOf(type))
//                    .addFormDataPart("file", filePath)
//                    .build();

            final Request request = new Request.Builder()
                    .url(API.BASE_URL + url)
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LogUtil.e("+++" + url + ":Fail+++" + e.toString());
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                failure();
                            }
                        });
                    } else {
                        failure();
                    }
                }

                private void failure() {
                    if (callback != null) {
                        callback.onFailure("服务器连接失败，请稍后重试");
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    final String result = response.body().string();
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                success(result);
                            }
                        });
                    } else {
                        success(result);
                    }
                }

                private void success(String result) {
                    try {
                        LogUtil.i("+++" + url + ":Result+++" + result);
                        if (TextUtils.isEmpty(result)) {
                            if (callback != null) {
                                callback.onFailure("服务器错误，请稍后重试");
                            }
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } else if (code == 300) {
                            if (!(context instanceof LoginActivity)) {
                                Utils.logout(context);
                            }
                            if (context instanceof Activity) {
                                Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                                ((Activity) context).finish();
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure(jsonObject.getString("msg"));
                                callback.onFailure(jsonObject.getInt("code"), jsonObject.getString("msg"), result);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onFailure("服务器错误，请稍后重试");
                        }
                        LogUtil.e("+++" + url + ":Exception+++" + e.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("+++" + url + ":Exception+++" + e.toString());
        }
    }

    public static void uploadVideoFile(final Context context, String url, String filePath, final HttpCallback callback) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                    .build();
        }

        try {
            MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
            multiBuilder.setType(MultipartBody.FORM);
            File file = new File(filePath);
            MediaType MEDIA_TYPE_JPG = MediaType.parse("video/mp4");
            RequestBody filebody = MultipartBody.create(MEDIA_TYPE_JPG, file);
            //这里是 封装上传图片参数
            multiBuilder.addFormDataPart("file", file.getName(), filebody);
            //参数以添加header方式将参数封装，否则上传参数为空
            // 设置请求体
            //这里是 封装上传图片参数
//                multiBuilder.addFormDataPart("file", file.getName(), filebody);
            // 封装请求参数,这里最重要
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "3");
            params.put("file", file.getName());
            //参数以添加header方式将参数封装，否则上传参数为空
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    multiBuilder.addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, params.get(key)));
                }
            }
            RequestBody body = multiBuilder.build();


//            RequestBody body = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("type", String.valueOf(type))
//                    .addFormDataPart("file", filePath)
//                    .build();

            final Request request = new Request.Builder()
                    .url(API.BASE_URL + url)
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LogUtil.e("+++" + url + ":Fail+++" + e.toString());
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                failure();
                            }
                        });
                    } else {
                        failure();
                    }
                }

                private void failure() {
                    if (callback != null) {
                        callback.onFailure("服务器连接失败，请稍后重试");
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    final String result = response.body().string();
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                success(result);
                            }
                        });
                    } else {
                        success(result);
                    }
                }

                private void success(String result) {
                    try {
                        LogUtil.i("+++" + url + ":Result+++" + result);
                        if (TextUtils.isEmpty(result)) {
                            if (callback != null) {
                                callback.onFailure("服务器错误，请稍后重试");
                            }
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } else if (code == 300) {
                            if (!(context instanceof LoginActivity)) {
                                Utils.logout(context);
                            }
                            if (context instanceof Activity) {
                                Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                                ((Activity) context).finish();
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure(jsonObject.getString("msg"));
                                callback.onFailure(jsonObject.getInt("code"), jsonObject.getString("msg"), result);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onFailure("服务器错误，请稍后重试");
                        }
                        LogUtil.e("+++" + url + ":Exception+++" + e.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("+++" + url + ":Exception+++" + e.toString());
        }
    }

    public static void uploadAudioFile(final Context context, String url, String filePath, final HttpCallback callback) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                    .build();
        }

        try {
            MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
            multiBuilder.setType(MultipartBody.FORM);
            File file = new File(filePath);
            MediaType MEDIA_TYPE = MediaType.parse("application/octet-stream");
            RequestBody filebody = MultipartBody.create(MEDIA_TYPE, file);
            //这里是 封装上传图片参数
            multiBuilder.addFormDataPart("file", file.getName(), filebody);
            //参数以添加header方式将参数封装，否则上传参数为空
            // 设置请求体
            //这里是 封装上传图片参数
//                multiBuilder.addFormDataPart("file", file.getName(), filebody);
            // 封装请求参数,这里最重要
            HashMap<String, String> params = new HashMap<>();
            params.put("type", "2");
            params.put("file", file.getName());
            //参数以添加header方式将参数封装，否则上传参数为空
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    multiBuilder.addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, params.get(key)));
                }
            }
            RequestBody body = multiBuilder.build();


//            RequestBody body = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("type", String.valueOf(type))
//                    .addFormDataPart("file", filePath)
//                    .build();

            final Request request = new Request.Builder()
                    .url(API.BASE_URL + url)
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LogUtil.e("+++" + url + ":Fail+++" + e.toString());
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                failure();
                            }
                        });
                    } else {
                        failure();
                    }
                }

                private void failure() {
                    if (callback != null) {
                        callback.onFailure("服务器连接失败，请稍后重试");
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    final String result = response.body().string();
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                success(result);
                            }
                        });
                    } else {
                        success(result);
                    }
                }

                private void success(String result) {
                    try {
                        LogUtil.i("+++" + url + ":Result+++" + result);
                        if (TextUtils.isEmpty(result)) {
                            if (callback != null) {
                                callback.onFailure("服务器错误，请稍后重试");
                            }
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } else if (code == 300) {
                            if (!(context instanceof LoginActivity)) {
                                Utils.logout(context);
                            }
                            if (context instanceof Activity) {
                                Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                                ((Activity) context).finish();
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure(jsonObject.getString("msg"));
                                callback.onFailure(jsonObject.getInt("code"), jsonObject.getString("msg"), result);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onFailure("服务器错误，请稍后重试");
                        }
                        LogUtil.e("+++" + url + ":Exception+++" + e.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("+++" + url + ":Exception+++" + e.toString());
        }
    }
}
