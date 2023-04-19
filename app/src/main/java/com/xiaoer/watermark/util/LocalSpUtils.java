package com.xiaoer.watermark.util;


import static com.xiaoer.watermark.bean.SPContact.IS_CAN_SHOW_LOG;
import static com.xiaoer.watermark.bean.SPContact.SP_NAME;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.SPContact;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalSpUtils {
    private Context mContext;

    public LocalSpUtils(Context context) {
        this.mContext = context;
    }

    public List<WaterMarkConfig> getWaterMarkConfigs() {
        SharedPreferences sharedPreferences = getSp();
        Set<String> stringSet = sharedPreferences.getStringSet(WATER_MARK_CONFIG, null);
        ArrayList<WaterMarkConfig> result = new ArrayList<>();
        if (stringSet != null && stringSet.size() > 0) {
            for (String item : stringSet) {
                LogUtil.d("getWaterMarkConfigs: " + item);
                result.add(new Gson().fromJson(item, WaterMarkConfig.class));
            }
        }
        LogUtil.d("size: " + result.size());
        return result;
    }

    public WaterMarkConfig getCurrentAppConfig(String packageName) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs();
        for (WaterMarkConfig item : waterMarkConfigs) {
            if (item.packageList != null && item.packageList.contains(packageName)) {
                return item;
            }
        }
        return null;
    }

    public void saveWaterMarkConfig(WaterMarkConfig config){
        if(config == null){
            return;
        }
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs();
        HashSet<String> result = new HashSet<>();
        Gson gson = new Gson();
        if(waterMarkConfigs == null || waterMarkConfigs.size() == 0){
            result.add(gson.toJson(config));
        }else {
            for (WaterMarkConfig waterMarkConfig : waterMarkConfigs) {
                result.add(gson.toJson(TextUtils.equals(waterMarkConfig.configId, config.configId) ? config : waterMarkConfig));
            }
        }
        getSp().edit().putStringSet(WATER_MARK_CONFIG, result).apply();
    }

    public void setDebug(){
        getSp().edit().putBoolean(SPContact.IS_CAN_SHOW_LOG, BuildConfig.DEBUG).apply();
    }

    private Context getContext() {
        if (mContext == null) {
            mContext = AndroidAppHelper.currentApplication();
        }
        return mContext;
    }

    public SharedPreferences getSp() {
        MultiProcessSharedPreferences.setAuthority("com.xiaoer.watermark.provider");
        return MultiProcessSharedPreferences.getSharedPreferences(getContext(), SP_NAME, Context.MODE_PRIVATE);
    }
}
