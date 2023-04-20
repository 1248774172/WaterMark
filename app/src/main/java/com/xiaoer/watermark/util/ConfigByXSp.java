package com.xiaoer.watermark.util;

import static com.xiaoer.watermark.bean.SPContact.APP_CONFIG;
import static com.xiaoer.watermark.bean.SPContact.WATERMARK_CONFIG;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.AppConfig;
import com.xiaoer.watermark.bean.SPContact;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

public class ConfigByXSp {

    public static AppConfig getAppConfig(){
        return new Gson().fromJson(getSp().getString(APP_CONFIG, ""), AppConfig.class);
    }

    public static List<WaterMarkConfig> getWaterMarkConfigs() {
        SharedPreferences sharedPreferences = getSp();
        Set<String> stringSet = sharedPreferences.getStringSet(WATER_MARK_CONFIG, null);
        ArrayList<WaterMarkConfig> result = new ArrayList<>();
        if (stringSet != null && stringSet.size() > 0) {
            for (String item : stringSet) {
                LogUtil.d("ConfigByXSp getWaterMarkConfigs: " + item);
                result.add(new Gson().fromJson(item, WaterMarkConfig.class));
            }
        }
        return result;
    }

    public static WaterMarkConfig getCurrentAppConfig(String packageName) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs();
        for (WaterMarkConfig item : waterMarkConfigs) {
            if (item.packageList != null && item.packageList.contains(packageName)) {
                return item;
            }
        }
        return new WaterMarkConfig();
    }

    public static boolean isDebug(){
        return getSp().getBoolean(SPContact.IS_CAN_SHOW_LOG, false);
    }

    public static XSharedPreferences getSp() {
        LogUtil.d("use XSP");
        XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, WATERMARK_CONFIG);
        sharedPreferences.makeWorldReadable();
        return sharedPreferences;
    }
}
