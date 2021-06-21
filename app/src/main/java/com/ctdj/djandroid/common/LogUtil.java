package com.ctdj.djandroid.common;

import android.text.TextUtils;
import android.util.Log;

/**
 * 打印Log.点击可以直接连接到打印log所在行
 */
public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static int LEVEL = VERBOSE;
    public static final String TAG = "dj_log_";

    public static void setLevel(int Level) {
        LEVEL = Level;
    }

    public static void v(String TAG, String msg) {
        if (LEVEL <= VERBOSE) {
            MyLog(VERBOSE, TAG, msg);
        }
    }

    public static void d(String msg) {
        if (LEVEL <= DEBUG) {
            MyLog(DEBUG, TAG, msg);
        }
    }
    public static void d(String TAG, String msg) {
        if (LEVEL <= DEBUG) {
            MyLog(DEBUG, TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (LEVEL <= INFO) {
            MyLog(INFO, TAG, msg);
        }
    }

    public static void i(String msg) {
        if (LEVEL <= INFO) {
            MyLog(INFO, TAG, msg);
        }
    }

    public static void w(String msg) {
        if (LEVEL <= WARN) {
            MyLog(WARN, TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (LEVEL <= WARN) {
            MyLog(WARN, TAG, msg);
        }
    }

    public static void e(String msg) {
        if (LEVEL <= ERROR) {
            MyLog(ERROR, TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (LEVEL <= ERROR) {
            MyLog(ERROR, TAG, msg);
        }
    }

    private static void MyLog(int type, String TAG, String msg) {
        if ( TextUtils.isEmpty(msg) ) {
            Log.e(TAG, "没有Log的数据");
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ .(").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");
        stringBuilder.append(msg);
        String logStr = stringBuilder.toString();
        switch (type) {
            case VERBOSE:
                Log.v(TAG, logStr);
                break;
            case DEBUG:
                Log.d(TAG, logStr);
                break;
            case INFO:
                Log.i(TAG, logStr);
                break;
            case WARN:
                Log.w(TAG, logStr);
                break;
            case ERROR:
                Log.e(TAG, logStr);
                break;
            default:
                break;
        }
    }


}
