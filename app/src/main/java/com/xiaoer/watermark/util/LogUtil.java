package com.xiaoer.watermark.util;

import android.app.Application;

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
        if(canShowLog()){
            XposedBridge.log("yys " + log);
        }
    }

    public static void e(String errorMsg) {
        if(canShowLog()){
            XposedBridge.log("yys error: " + errorMsg);
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
