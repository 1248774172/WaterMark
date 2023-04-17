package com.xiaoer.watermark.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.Iterator;

public class ConfigUtils {
    private static ArrayList<WaterMarkConfig> configArrayList;
    public static boolean shouldReload;

    private static void checkAndInit(){
        if (configArrayList == null || shouldReload) {
            shouldReload = false;
            configArrayList = getAllConfigFromFile();
        }
    }
    public static WaterMarkConfig getConfigByPackageName(String packageName) {
        checkAndInit();
        if(configArrayList != null && configArrayList.size() > 0){
            for (WaterMarkConfig waterMarkConfig : configArrayList) {
                if(waterMarkConfig.packageList != null && waterMarkConfig.packageList.contains(packageName)){
                    return waterMarkConfig;
                }
            }
        }
        return new WaterMarkConfig();
    }

    private static ArrayList<WaterMarkConfig> getAllConfigFromFile(){
        String confJson = null;
        if (FileUtils.getConfigFile().exists()){
            confJson = FileUtils.readFromFile(FileUtils.getConfigFile());
        }
        return new Gson().fromJson(confJson, new TypeToken<ArrayList<WaterMarkConfig>>(){}.getType());
    }

    public static boolean addWaterMarkConfig(WaterMarkConfig config) {
        if (config == null) {
            return false;
        }
        checkAndInit();
        if (configArrayList != null && configArrayList.size() > 0) {
            Iterator<WaterMarkConfig> iterator = configArrayList.iterator();
            while (iterator.hasNext()){
                WaterMarkConfig next = iterator.next();
                if(next.configId.equals(config.configId)){
                    iterator.remove();
                }
            }
            configArrayList.add(config);
            return FileUtils.write2File(new Gson().toJson(configArrayList), FileUtils.getConfigFile());
        }
        return false;
    }
}
