package com.xiaoer.watermark.hook;

import android.content.Context;

import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.AppConfig;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.util.OpeConfigFromFile;
import com.xiaoer.watermark.util.OpeConfigFromSp;

import java.util.List;

public class ConfigHelper {

    public static boolean canUseSp = false;

    public static boolean canUseSp(){
        return false;
    }

    public static List<WaterMarkConfig> getWaterMarkConfigs(Context context) {
        if(canUseSp()){
            return OpeConfigFromSp.getInstance().getWaterMarkConfigs(context);
        }else {
            return OpeConfigFromFile.getWaterMarkConfigs(context);
        }
    }

    public static WaterMarkConfig getCurrentAppConfig(Context context) {
        if(canUseSp()){
            return OpeConfigFromSp.getInstance().getCurrentAppConfig(context);
        }else {
            return OpeConfigFromFile.getCurrentAppConfig(context);
        }
    }

    public static boolean saveWaterMarkConfig(Context context, WaterMarkConfig config){
        if(canUseSp()){
            OpeConfigFromSp.getInstance().saveWaterMarkConfig(context, config);
            return true;
        }else {
            return OpeConfigFromFile.saveWaterMarkConfig(context, config);
        }
    }

    public static void setDebug(Context context){
        if(canUseSp()){
            OpeConfigFromSp.getInstance().setDebug(context, BuildConfig.DEBUG);
        }else {
            OpeConfigFromFile.setDebug(context, BuildConfig.DEBUG);
        }
    }

    public static boolean isCanShowLog(Context context) {
        if(canUseSp()){
            return OpeConfigFromSp.getInstance().isDebug(context);
        }else {
            return OpeConfigFromFile.isDebug(context);
        }
    }

    public static boolean saveAppConfig(Context context, AppConfig config) {
        if(canUseSp()){
            OpeConfigFromSp.getInstance().saveAppConfig(context, config);
            return true;
        }else {
            return OpeConfigFromFile.saveAppConfig(context, config);
        }
    }

    public static AppConfig getAppConfig(Context context){
        if(canUseSp()){
            return OpeConfigFromSp.getInstance().getAppConfig(context);
        }else {
            return OpeConfigFromFile.getAppConfig(context);
        }
    }

}
