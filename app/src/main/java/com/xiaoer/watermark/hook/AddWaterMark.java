package com.xiaoer.watermark.hook;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.github.kyuubiran.ezxhelper.EzXHelper;
import com.xiaoer.watermark.util.FileUtils;
import com.xiaoer.watermark.util.LogUtil;
import com.xiaoer.watermark.util.ProcessUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class AddWaterMark implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static Application mApplication;

    @Override
    public void initZygote(StartupParam startupParam) {
        EzXHelper.initZygote(startupParam);
        FileUtils.getInstance().initZygote();
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        if (lpparam != null){
            if (lpparam.packageName.equals("android")) {
                LogUtil.d("hook android XposedBridge.getXposedVersion(): " + XposedBridge.getXposedVersion());
                EzXHelper.initHandleLoadPackage(lpparam);
                FileUtils.getInstance().hookAMS(lpparam);
            }

            XposedHelpers.findAndHookMethod("android.app.Application", lpparam.classLoader, "onCreate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mApplication = (Application) param.thisObject;
                    if (ProcessUtil.isMainProcess()) {
                        WaterMarkManager.init(mApplication);
                        LogUtil.init(mApplication);
                        addListener(mApplication);
                    }
                }
            });
        }
    }

    public static void addListener(Application application){
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                WaterMarkManager.getInstance().addWaterMark(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                WaterMarkManager.getInstance().removeWaterMark(activity);
            }
        });
    }

    public static Application getApplication(){
        return mApplication;
    }

}
