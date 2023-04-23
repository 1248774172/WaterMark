package com.xiaoer.watermark.hook;

import android.content.Context;
import android.widget.Toast;

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
            boolean success = OpeConfigFromFile.saveWaterMarkConfig(context, config);
            if(success){
                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context,"保存失败",Toast.LENGTH_SHORT).show();
            }
            return success;
        }
    }

    public static boolean deleteWaterMarkConfig(Context context, WaterMarkConfig config){
        if(canUseSp()){
            OpeConfigFromSp.getInstance().deleteWaterMarkConfig(context, config);
            return true;
        }else {
            boolean success = OpeConfigFromFile.deleteWaterMarkConfig(context, config);
            if(success){
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
            }
            return success;
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

    public static boolean isModuleActivated() {
        return false;
    }
}
