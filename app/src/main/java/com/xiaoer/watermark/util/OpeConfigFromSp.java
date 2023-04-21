package com.xiaoer.watermark.util;

import static com.xiaoer.watermark.bean.SPContact.APP_CONFIG;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG_FILE_NAME;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG_KEY;

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

public class OpeConfigFromSp {

    public static SharedPreferences mSharedPreferences;
    public static XSharedPreferences mXsp;

    private static volatile OpeConfigFromSp mOpeConfigFromSp;

    private OpeConfigFromSp(){}

    public static OpeConfigFromSp getInstance(){
        if (mOpeConfigFromSp == null){
            synchronized (OpeConfigFromSp.class){
                if (mOpeConfigFromSp == null){
                    mOpeConfigFromSp = new OpeConfigFromSp();
                }
            }
        }
        return mOpeConfigFromSp;
    }


    public void saveAppConfig(Context context, AppConfig config) {
        LogUtil.d("saveAppConfig:" + config);
        getEdit(context).putString(APP_CONFIG, new Gson().toJson(config)).apply();
    }

    public AppConfig getAppConfig(Context context){
        return new Gson().fromJson(getSp(context).getString(APP_CONFIG, ""), AppConfig.class);
    }

    public List<WaterMarkConfig> getWaterMarkConfigs(Context context) {
        SharedPreferences sharedPreferences = getSp(context);
        Set<String> stringSet = sharedPreferences.getStringSet(WATER_MARK_CONFIG_KEY, null);
        ArrayList<WaterMarkConfig> result = new ArrayList<>();
        if (stringSet != null && stringSet.size() > 0) {
            for (String item : stringSet) {
                result.add(new Gson().fromJson(item, WaterMarkConfig.class));
            }
        }
        LogUtil.d("OpeConfigFromSp---getWaterMarkConfigs: " + result);
        return result;
    }

    public WaterMarkConfig getCurrentAppConfig(Context context) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs(context);
        for (WaterMarkConfig item : waterMarkConfigs) {
            if (item.packageList != null && item.packageList.contains(context.getPackageName())) {
                return item;
            }
        }
        return new WaterMarkConfig();
    }

    public void saveWaterMarkConfig(Context context, WaterMarkConfig config){
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
        getEdit(context).putStringSet(WATER_MARK_CONFIG_KEY, result).apply();
    }

    public void setDebug(Context context,boolean isDebuggable){
        getEdit(context).putBoolean(SPContact.IS_CAN_SHOW_LOG, isDebuggable).apply();
    }

    public boolean isDebug(Context context){
        return getSp(context).getBoolean(SPContact.IS_CAN_SHOW_LOG, false);
    }

    public SharedPreferences getSp(Context context) {
        if(isXposedModule(context)){
            LogUtil.d("use SP");
            return getCommonSp(context);
        }else {
            LogUtil.d("use XSP");
            return getXSharedPreferences();
        }
    }

    private SharedPreferences getXSharedPreferences(){
        if(mXsp == null){
            mXsp = new XSharedPreferences(BuildConfig.APPLICATION_ID, WATER_MARK_CONFIG_FILE_NAME);
        }
        return mXsp;
    }

    private SharedPreferences getCommonSp(Context context){
        if(mSharedPreferences == null){
            mSharedPreferences = context.getSharedPreferences(WATER_MARK_CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    private boolean isXposedModule(Context context){
        return BuildConfig.APPLICATION_ID.equals(context.getPackageName());
    }

    private SharedPreferences.Editor getEdit(Context context){
        return getCommonSp(context).edit();
    }
}
