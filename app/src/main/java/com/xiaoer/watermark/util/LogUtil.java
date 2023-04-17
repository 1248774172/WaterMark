package com.xiaoer.watermark.util;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import de.robv.android.xposed.XposedBridge;

public class LogUtil {
    private static Application mApplication;
    private LogUtil(){}

    public static void init(Application application){
        mApplication = application;
    }
    public static void d(String log) {
        if(canShowLog()){
            XposedBridge.log("yys " + log);
        }
    }

    public static void e(String errorMsg) {
        if(canShowLog()){
            XposedBridge.log("yys error: " + errorMsg);
        }
    }

    public static boolean canShowLog(){
        return mApplication != null;
    }

    public static boolean isDebuggable() {
        PackageManager packageManager = mApplication.getPackageManager();
        try{
            ApplicationInfo info = packageManager.getApplicationInfo("com.xiaoer.watermark", 0);
            return (0 != (info.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        }catch(PackageManager.NameNotFoundException e){
            return false;
        }
    }
}
