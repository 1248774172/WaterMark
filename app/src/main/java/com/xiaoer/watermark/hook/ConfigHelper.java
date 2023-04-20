package com.xiaoer.watermark.hook;

import android.content.Context;

import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.AppConfig;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.util.ConfigByFile;
import com.xiaoer.watermark.util.ConfigBySp;
import com.xiaoer.watermark.util.ConfigByXSp;

import java.util.List;

public class ConfigHelper {

    public static boolean canUseSp = false;

    public static boolean canUseSp(){
        return canUseSp;
    }

    public static List<WaterMarkConfig> getWaterMarkConfigs(Context context) {
        if(canUseSp()){
            if (isXposedModule(context)){
                return ConfigBySp.getWaterMarkConfigs(context);
            }else {
                return ConfigByXSp.getWaterMarkConfigs();
            }
        }else {
            return ConfigByFile.getWaterMarkConfigs(context);
        }
    }

    public static WaterMarkConfig getCurrentAppConfig(Context context) {
        if(canUseSp()){
            if (isXposedModule(context)){
                return ConfigBySp.getCurrentAppConfig(context);
            }else {
                return ConfigByXSp.getCurrentAppConfig(context.getPackageName());
            }
        }else {
            return ConfigByFile.getCurrentAppConfig(context);
        }
    }

    public static boolean saveWaterMarkConfig(Context context, WaterMarkConfig config){
        if(canUseSp()){
            ConfigBySp.saveWaterMarkConfig(context, config);
            return true;
        }else {
            return ConfigByFile.saveWaterMarkConfig(context, config);
        }
    }

    public static void setDebug(Context context){
        if(canUseSp()){
            ConfigBySp.setDebug(context, BuildConfig.DEBUG);
        }else {
            ConfigByFile.setDebug(context, BuildConfig.DEBUG);
        }
    }

    public static boolean isCanShowLog(Context context) {
        if(canUseSp()){
            if(isXposedModule(context)){
                return ConfigBySp.isDebug(context);
            }else {
                return ConfigByXSp.isDebug();
            }
        }else {
            return ConfigByFile.isDebug(context);
        }
    }

    public static boolean saveAppConfig(Context context, AppConfig config) {
        if(canUseSp()){
            ConfigBySp.saveAppConfig(context, config);
            return true;
        }else {
            return ConfigByFile.saveAppConfig(context, config);
        }
    }

    public static AppConfig getAppConfig(Context context){
        if(canUseSp()){
            if(isXposedModule(context)){
                return ConfigBySp.getAppConfig(context);
            }else {
                return ConfigByXSp.getAppConfig();
            }
        }else {
            return ConfigByFile.getAppConfig(context);
        }
    }

    private static boolean isXposedModule(Context context){
        return BuildConfig.APPLICATION_ID.equals(context.getPackageName());
    }
}
