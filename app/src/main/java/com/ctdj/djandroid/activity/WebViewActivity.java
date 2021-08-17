package com.ctdj.djandroid.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ctdj.djandroid.common.LogUtil;
import com.ctdj.djandroid.common.Utils;
import com.ctdj.djandroid.databinding.ActivityWebViewBinding;
import com.ctdj.djandroid.view.TitleView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class WebViewActivity extends BaseActivity {

    ActivityWebViewBinding binding;
    private JSInterface jsInterface;
    private ProgressDialog dialog;
    private String url;
    private String title;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.checkPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA})) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }
        binding = ActivityWebViewBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        LogUtil.e("web url:" + url);
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        binding.titleView.setTitle(title);
        binding.titleView.setOnBtnListener(new TitleView.OnBtnListener() {
            @Override
            public void onLeftClick() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack();
                } else {
                    finish();
                }
            }

            @Override
            public void onRightClick() {

            }
        });

        initWebView();
    }

    private void initWebView() {
        dialog = new ProgressDialog(this);
        WebSettings webSettings = binding.webView.getSettings();
        jsInterface = new JSInterface(this);
        binding.webView.addJavascriptInterface(jsInterface, "jsInterface");
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setTextSize(WebSettings.TextSize.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(getApplicationContext().getCacheDir().getAbsolutePath());

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //调用
                if (url.startsWith("alipays:") || url.startsWith("alipay")){
                    Uri uri = Uri.parse( "alipays://platformapi/startApp" );
                    Intent intent = new Intent( Intent.ACTION_VIEW, uri );
                    ComponentName componentName = intent.resolveActivity(getPackageManager());
                    //判断是否安装支付宝
                    if (componentName != null) {
                        startActivity(new Intent( Intent.ACTION_VIEW, Uri.parse(url) ));
                    } else {
                        Utils.showToast(WebViewActivity.this, "请安装支付宝客户端");
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading( view, url );
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        });

        binding.webView.setWebChromeClient(new ZpWebChromeClient());

        loadUrl(url);
    }

    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    private boolean multiple_files = false;

    class ZpWebChromeClient extends WebChromeClient {

        /*
         * openFileChooser is not a public Android API and has never been part of the SDK.
         */
        //handling input[type="file"] requests for android API 16+
        @SuppressLint("ObsoleteSdkInt")
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            if (multiple_files && Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            startActivityForResult(Intent.createChooser(i, "选择文件"), FCR);
        }

        //handling input[type="file"] requests for android API 21+
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            String[] perms = {WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

            //checking for storage permission to write images for upload
            if (ContextCompat.checkSelfPermission(WebViewActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WebViewActivity.this, perms, FCR);

                //checking for WRITE_EXTERNAL_STORAGE permission
            } else if (ContextCompat.checkSelfPermission(WebViewActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, FCR);

                //checking for CAMERA permissions
            } else if (ContextCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{Manifest.permission.CAMERA}, FCR);
            }
            if (mUMA != null) {
                mUMA.onReceiveValue(null);
            }
            mUMA = filePathCallback;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(WebViewActivity.this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCM);
                } catch (IOException ex) {
                    LogUtil.e("Image file creation failed:" + ex);
                }
                if (photoFile != null) {
                    mCM = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");
            if (multiple_files) {
                contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "选择文件");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, FCR);
            return true;
        }
    }

    //creating new image file here
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //checking if response is positive
            if (resultCode == RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null || intent.getData() == null) {
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        } else {
                            if (multiple_files) {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();
                                    results = new Uri[numSelectedFiles];
                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        results[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.webView.canGoBack()) {
            binding.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadUrl(String url) {
        if (TextUtils.isEmpty(content)) {
            binding.webView.loadUrl(url);
        } else {
            binding.webView.postUrl(url, content.getBytes());
        }
    }

    public class JSInterface {
        Context mContext;

        private JSInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void logout() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Utils.logout(HLWebView.this);
//                    Utils.showToast(HLWebView.this, "登录过期，请重新登录");
//                    HLWebView.this.finish();
//                }
//            });
        }
    }
}