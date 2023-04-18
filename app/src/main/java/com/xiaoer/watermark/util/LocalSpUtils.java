package com.xiaoer.watermark.util;

import static com.xiaoer.watermark.bean.SPContact.SP_NAME;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.SPContact;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalSpUtils {
    private static volatile LocalSpUtils mInstance;
    private static volatile SharedPreferences mSharedPreferences;

    private LocalSpUtils(Context context){
        if(mSharedPreferences == null){
            mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
    }

    public static LocalSpUtils getInstance(Context context){
        if(mInstance == null){
            synchronized (LocalSpUtils.class){
                if (mInstance == null){
                    mInstance = new LocalSpUtils(context);
                }
            }
        }
        return mInstance;
    }

    public void saveWaterMarkConfig(WaterMarkConfig config){
        if(config == null){
            return;
        }
        HashSet<String> result = new HashSet<>();
        result.add(new Gson().toJson(config));
        mSharedPreferences.edit().putStringSet(WATER_MARK_CONFIG, result).apply();
    }

    public List<WaterMarkConfig> getWaterMarkConfigs(){
        Set<String> stringSet = mSharedPreferences.getStringSet(WATER_MARK_CONFIG, null);
        ArrayList<WaterMarkConfig> result = new ArrayList<>();
        if(stringSet != null && stringSet.size() > 0){
            for (String item : stringSet) {
                result.add(new Gson().fromJson(item, WaterMarkConfig.class));
            }
        }
        return result;
    }

    public WaterMarkConfig getCurrentAppConfig(String packageName){
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs();
        for (WaterMarkConfig item : waterMarkConfigs) {
            if(item.packageList != null && item.packageList.contains(packageName)){
                return item;
            }
        }
        return null;
    }

    public void setDebug(){
        mSharedPreferences.edit().putBoolean(SPContact.IS_CAN_SHOW_LOG, BuildConfig.DEBUG).apply();
    }
}
