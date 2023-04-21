package com.xiaoer.watermark.util;


import static com.xiaoer.watermark.bean.SPContact.APP_CONFIG;
import static com.xiaoer.watermark.bean.SPContact.WATERMARK_CONFIG;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.AppConfig;
import com.xiaoer.watermark.bean.SPContact;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

public class ConfigBySp {

    public static SharedPreferences mSharedPreferences;

    public static void saveAppConfig(Context context, AppConfig config) {
        getSp(context).edit().putString(APP_CONFIG, new Gson().toJson(config)).apply();
    }

    public static AppConfig getAppConfig(Context context){
        return new Gson().fromJson(getSp(context).getString(APP_CONFIG, ""), AppConfig.class);
    }

    public static List<WaterMarkConfig> getWaterMarkConfigs(Context context) {
        SharedPreferences sharedPreferences = getSp(context);
        Set<String> stringSet = sharedPreferences.getStringSet(WATER_MARK_CONFIG, null);
        ArrayList<WaterMarkConfig> result = new ArrayList<>();
        if (stringSet != null && stringSet.size() > 0) {
            for (String item : stringSet) {
                result.add(new Gson().fromJson(item, WaterMarkConfig.class));
            }
        }
        LogUtil.d("ConfigBySp getWaterMarkConfigs: " + result);
        return result;
    }

    public static WaterMarkConfig getCurrentAppConfig(Context context) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs(context);
        for (WaterMarkConfig item : waterMarkConfigs) {
            if (item.packageList != null && item.packageList.contains(context.getPackageName())) {
                return item;
            }
        }
        return new WaterMarkConfig();
    }

    public static void saveWaterMarkConfig(Context context, WaterMarkConfig config){
        if(config == null){
            return;
        }
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs(context);
        HashSet<String> result = new HashSet<>();
        Gson gson = new Gson();
        if(waterMarkConfigs.size() == 0){
            result.add(gson.toJson(config));
        }else {
            for (WaterMarkConfig waterMarkConfig : waterMarkConfigs) {
                result.add(gson.toJson(config));
                if (!TextUtils.equals(waterMarkConfig.configId, config.configId)) {
                    result.add(gson.toJson(waterMarkConfig));
                }
            }
        }
        LogUtil.d("saveWaterMarkConfig:" + result);
        getSp(context).edit().putStringSet(WATER_MARK_CONFIG, result).apply();
    }

    public static void setDebug(Context context,boolean isDebuggable){
        getSp(context).edit().putBoolean(SPContact.IS_CAN_SHOW_LOG, isDebuggable).apply();
    }

    public static boolean isDebug(Context context){
        return getSp(context).getBoolean(SPContact.IS_CAN_SHOW_LOG, false);
    }

    public static SharedPreferences getSp(Context context) {
        LogUtil.d("use MSP");
        if(mSharedPreferences != null){
            return mSharedPreferences;
        }
        if(isXposedModule(context)){
            MultiProcessSharedPreferences.setAuthority("com.xiaoer.watermark.provider");
            mSharedPreferences = MultiProcessSharedPreferences.getSharedPreferences(context, WATERMARK_CONFIG, Context.MODE_PRIVATE);
        }else {
            mSharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, WATERMARK_CONFIG);
        }
        return mSharedPreferences;
    }

    private static boolean isXposedModule(Context context){
        return BuildConfig.APPLICATION_ID.equals(context.getPackageName());
    }
}
