package com.xiaoer.watermark.util;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import de.robv.android.xposed.XposedBridge;

public class LogUtil {
    private static Application mApplication;
    private static boolean canShowLog = true;
    private LogUtil(){}

    public static void init(Application application){
        mApplication = application;
        canShowLog = canShowLog();
    }
    public static void d(String log) {
        print("yys " + log);
    }

    public static void e(String errorMsg) {
        print("yys error: " + errorMsg);
    }

    private static void print(String msg){
        if(canShowLog()){
            if(mApplication != null && TextUtils.equals(mApplication.getPackageName(), "com.xiaoer.watermark")){
                XposedBridge.log(msg);
            }else {
                Log.d("Xposed", msg);
            }
        }
    }

    private static boolean canShowLog(){
        return canShowLog || (mApplication != null && isDebuggable());
    }

    private static boolean isDebuggable() {
        RemoteSpUtils remoteSpUtils = new RemoteSpUtils(mApplication);
        return remoteSpUtils.isCanShowLog();
    }
}
