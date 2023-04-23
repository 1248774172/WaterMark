package com.xiaoer.watermark.util;

import static com.xiaoer.watermark.bean.SPContact.APP_CONFIG;
import static com.xiaoer.watermark.bean.SPContact.DEBUGGABLE;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoer.watermark.bean.AppConfig;
import com.xiaoer.watermark.bean.SPContact;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.List;

public class OpeConfigFromFile {

    public static boolean saveAppConfig(Context context, AppConfig config) {
        LogUtil.d("OpeConfigFromFile saveAppConfig:" + config);
        return FileUtils.getInstance().saveFile(context, APP_CONFIG, new Gson().toJson(config));
    }

    public static AppConfig getAppConfig(Context context){
        AppConfig appConfig = new Gson().fromJson(FileUtils.getInstance().readFile(context, APP_CONFIG), AppConfig.class);
        LogUtil.d("OpeConfigFromFile cache appConfig: " + (appConfig == null ? "null" : appConfig));
        return appConfig;
    }

    public static boolean saveWaterMarkConfig(Context context, WaterMarkConfig config) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs(context);
        List<WaterMarkConfig> result = new ArrayList<>();
        Gson gson = new Gson();
        for (WaterMarkConfig waterMarkConfig : waterMarkConfigs) {
            if (!TextUtils.equals(waterMarkConfig.configId, config.configId)) {
                result.add(waterMarkConfig);
            }else {
                result.add(config);
            }
        }
        return FileUtils.getInstance().saveFile(context, SPContact.WATER_MARK_CONFIG_FILE_NAME, gson.toJson(result));
    }

    public static boolean deleteWaterMarkConfig(Context context, WaterMarkConfig config) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs(context);
        List<WaterMarkConfig> result = new ArrayList<>();
        Gson gson = new Gson();
        for (WaterMarkConfig waterMarkConfig : waterMarkConfigs) {
            if (!TextUtils.equals(waterMarkConfig.configId, config.configId)) {
                result.add(waterMarkConfig);
            }
        }
        return FileUtils.getInstance().saveFile(context, SPContact.WATER_MARK_CONFIG_FILE_NAME, gson.toJson(result));
    }


    public static List<WaterMarkConfig> getWaterMarkConfigs(Context context) {
        String json = FileUtils.getInstance().readFile(context, SPContact.WATER_MARK_CONFIG_FILE_NAME);
        ArrayList<WaterMarkConfig> result = new Gson().fromJson(json, new TypeToken<ArrayList<WaterMarkConfig>>() {});
        return result == null ? new ArrayList<>() : result;
    }

    public static WaterMarkConfig getCurrentAppConfig(Context context) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs(context);
        for (WaterMarkConfig item : waterMarkConfigs) {
            if (item.isOpen() && item.packageList != null && item.packageList.contains(context.getPackageName())) {
                return item;
            }
        }
        return new WaterMarkConfig();
    }
}
