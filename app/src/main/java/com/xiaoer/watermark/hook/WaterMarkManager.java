package com.xiaoer.watermark.hook;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xiaoer.watermark.bean.AppConfig;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.ui.Watermark;
import com.xiaoer.watermark.util.LogUtil;
import com.xiaoer.watermark.util.NetWorkUtil;
import com.xiaoer.watermark.util.RemoteSpUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class WaterMarkManager {

    private static WaterMarkManager mManager;
    private final AtomicBoolean canShow = new AtomicBoolean();
    private static WaterMarkConfig  mWaterMarkConfig;
    private WaterMarkManager(){
    }

    public static void init(Application application){
        if (application == null){
            return;
        }
        String pkgName;
        String versionName;
        PackageManager packageManager = application.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(application.getPackageName(), 0);
            pkgName = packageInfo.packageName;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.d("获取宿主包名或版本名失败，无法显示水印");
            return;
        }

        LogUtil.d("pkgName: " + pkgName + " versionName: " + versionName);
        NetWorkUtil.getInstance(application).requestByGet("https://1248774172.github.io", new NetWorkUtil.NetWorkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    boolean matchPkg = false;
                    boolean matchVer = false;
                    AppConfig configBean = new Gson().fromJson(result, AppConfig.class);
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
                                Toast.makeText(application,"未适配当前应用的版本",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            WaterMarkManager.getInstance().canShow.set(true);
                        }
                    }
                }catch (Exception e){
                    WaterMarkManager.getInstance().canShow.set(true);
                    Toast.makeText(application,"读取网络配置失败",Toast.LENGTH_SHORT).show();
                    LogUtil.e(e.getMessage());
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

    public static WaterMarkManager getInstance(){
        if(mManager == null){
            synchronized (WaterMarkManager.class){
                mManager = new WaterMarkManager();
            }
        }
        return mManager;
    }

    public void showWaterMark(Activity activity){
        if (canShow.get()) {
            Watermark instance = new Watermark(activity);
            if (mWaterMarkConfig == null) {
                mWaterMarkConfig = new WaterMarkConfig();
            }
            instance.setText(mWaterMarkConfig.getContent());
            instance.setTextColor(mWaterMarkConfig.getTextColor());
            instance.setTextSize(mWaterMarkConfig.getTextSize());
            instance.setRotation(mWaterMarkConfig.getRotation());
            instance.show();
        }
    }

    private static void initWaterMarkConfig(Context context){
        LogUtil.d("initWaterMarkConfig: " + context.getPackageName());
        RemoteSpUtils remoteSpUtils = new RemoteSpUtils(context);
        mWaterMarkConfig = remoteSpUtils.getCurrentAppConfig(context.getPackageName());
        LogUtil.d(mWaterMarkConfig == null ? "null" : mWaterMarkConfig.toString());
    }

}
