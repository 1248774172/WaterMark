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

public class ConfigByFile {

    public static boolean saveAppConfig(Context context, AppConfig config) {
        return FileUtils.getInstance().saveFile(context, APP_CONFIG, new Gson().toJson(config));
    }

    public static AppConfig getAppConfig(Context context){
        String json = FileUtils.getInstance().readFile(context, APP_CONFIG);
        return new Gson().fromJson(json,AppConfig.class);
    }

    public static boolean saveWaterMarkConfig(Context context, WaterMarkConfig config) {
        List<WaterMarkConfig> waterMarkConfigs = getWaterMarkConfigs(context);
        List<WaterMarkConfig> result = new ArrayList<>();
        Gson gson = new Gson();
        if (waterMarkConfigs.size() == 0) {
            result.add(config);
        } else {
            for (WaterMarkConfig waterMarkConfig : waterMarkConfigs) {
                result.add(config);
                if (!TextUtils.equals(waterMarkConfig.configId, config.configId)) {
                    result.add(waterMarkConfig);
                }
            }
        }
        return FileUtils.getInstance().saveFile(context, SPContact.WATERMARK_CONFIG, gson.toJson(result));
    }


    public static List<WaterMarkConfig> getWaterMarkConfigs(Context context) {
        String json = FileUtils.getInstance().readFile(context, SPContact.WATERMARK_CONFIG);
        ArrayList<WaterMarkConfig> result = new Gson().fromJson(json, new TypeToken<ArrayList<WaterMarkConfig>>() {});
        return result == null ? new ArrayList<>() : result;
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

    public static void setDebug(Context context, boolean isDebuggable) {
        if (isDebuggable) {
            FileUtils.getInstance().saveFile(context, DEBUGGABLE, "1");
        }else {
            FileUtils.getInstance().deleteFile(context, DEBUGGABLE);
        }
    }

    public static boolean isDebug(Context context) {
        String debuggable = FileUtils.getInstance().readFile(context, DEBUGGABLE);
        return TextUtils.equals("1", debuggable);
    }
}
