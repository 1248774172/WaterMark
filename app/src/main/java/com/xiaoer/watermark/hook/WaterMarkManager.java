package com.xiaoer.watermark.hook;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.AppConfig;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.ui.Watermark;
import com.xiaoer.watermark.util.LogUtil;
import com.xiaoer.watermark.util.NetWorkUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class WaterMarkManager {

    private static WaterMarkManager mManager;
    private final AtomicBoolean canShow = new AtomicBoolean();
    private static WaterMarkConfig  mWaterMarkConfig;
    private final ConcurrentHashMap<String, Watermark> mHashMap = new ConcurrentHashMap<>();

    private WaterMarkManager(){
    }

    public static WaterMarkManager getInstance(){
        if(mManager == null){
            synchronized (WaterMarkManager.class){
                mManager = new WaterMarkManager();
            }
        }
        return mManager;
    }

    public static void init(Application application){
        final boolean initByCache;
        AppConfig appConfig = ConfigHelper.getAppConfig(application);

        if(appConfig != null){
            initByCache = true;
            handleAppConfig(application, appConfig);
        }else {
            initByCache = false;
        }

        NetWorkUtil.getInstance(application).requestByGet("https://1248774172.github.io", new NetWorkUtil.NetWorkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    AppConfig appConfig = new Gson().fromJson(result, AppConfig.class);
                    if(!initByCache){
                        LogUtil.d("use net appConfig");
                        handleAppConfig(application, appConfig);
                    }
                    ConfigHelper.saveAppConfig(application, appConfig);
                }catch (Exception e){
                    WaterMarkManager.getInstance().canShow.set(true);
                    Toast.makeText(application,"读取网络配置失败",Toast.LENGTH_SHORT).show();
                    LogUtil.e("onSuccess:" + e.getMessage());
                }
            }

            @Override
            public void onFail(String msg) {
                WaterMarkManager.getInstance().canShow.set(true);
                Toast.makeText(application,"读取网络配置失败",Toast.LENGTH_SHORT).show();
                LogUtil.e("请求失败，无法显示水印");
            }

            @Override
            public void onCancel(String msg) {
                WaterMarkManager.getInstance().canShow.set(true);
                Toast.makeText(application,"读取网络配置失败",Toast.LENGTH_SHORT).show();
            }
        });
        initWaterMarkConfig(application);
    }

    private static void handleAppConfig(Context context, AppConfig configBean) {
        if (context == null){
            return;
        }
        String pkgName;
        String versionName;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            pkgName = packageInfo.packageName;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.d("获取宿主包名或版本名失败，无法显示水印");
            Toast.makeText(context,"获取宿主包名或版本名失败，无法显示水印",Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.d("pkgName: " + pkgName + " versionName: " + versionName);
        boolean matchPkg = false;
        boolean matchVer = false;
        if(configBean == null){
            WaterMarkManager.getInstance().canShow.set(true);
            return;
        }
        if(configBean.getCode() == 1){
            LogUtil.d("开关关闭，开放所有");
            WaterMarkManager.getInstance().canShow.set(true);
        }else {
            if(configBean.getAppList() != null && configBean.getAppList().size() > 0){
                for (AppConfig.AppBean appBean : configBean.getAppList()) {
                    if (appBean.getPackageName().equals(pkgName)) {
                        matchPkg = true;
                        for (String s : appBean.getVersionCode()) {
                            if (s.equals(versionName)) {
                                matchVer = true;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            LogUtil.d("matchPkg: " + matchPkg + " matchVer: " + matchVer);
            if(matchPkg){
                if(matchVer){
                    WaterMarkManager.getInstance().canShow.set(true);
                }else {
                    WaterMarkManager.getInstance().canShow.set(false);
                    Toast.makeText(context, "未适配当前应用的版本", Toast.LENGTH_SHORT).show();
                }
            }else {
                WaterMarkManager.getInstance().canShow.set(true);
            }
        }
    }

    private static void initWaterMarkConfig(Context context){
        LogUtil.d("initWaterMarkConfig: " + context.getPackageName());
        mWaterMarkConfig = ConfigHelper.getCurrentAppConfig(context);
    }

    public void addWaterMark(Activity activity){
        if (canShow.get()) {
            Watermark watermark = null;
            if (mHashMap.containsKey(activity.getLocalClassName())) {
                watermark = mHashMap.get(activity.getLocalClassName());
            }
            if (watermark == null){
                watermark = new Watermark(activity);
                watermark.setText(mWaterMarkConfig.getContent());
                watermark.setTextColor(mWaterMarkConfig.getTextColor());
                watermark.setTextSize(mWaterMarkConfig.getTextSize());
                watermark.setRotation(mWaterMarkConfig.getRotation());
            }
            watermark.show();
        }
    }

    public void removeWaterMark(Activity activity){
        mHashMap.remove(activity.getLocalClassName());
    }

}
