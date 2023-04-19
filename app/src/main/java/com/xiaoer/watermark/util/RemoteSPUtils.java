package com.xiaoer.watermark.util;


import static com.xiaoer.watermark.bean.SPContact.IS_CAN_SHOW_LOG;
import static com.xiaoer.watermark.bean.SPContact.SP_NAME;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG;

import com.google.gson.Gson;
import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

public class RemoteSPUtils {
    private static volatile RemoteSPUtils remoteSPUtils;

    private final XSharedPreferences xSp;

    private RemoteSPUtils() {
        xSp = new XSharedPreferences(BuildConfig.APPLICATION_ID, SP_NAME);
        xSp.makeWorldReadable();
    }

    public static RemoteSPUtils getInstance(){
        if(remoteSPUtils == null){
            synchronized (RemoteSPUtils.class){
                if (remoteSPUtils == null) {
                    remoteSPUtils = new RemoteSPUtils();
                }
            }
        }
        return remoteSPUtils;
    }

    public List<WaterMarkConfig> getWaterMarkConfigs() {
        Set<String> stringSet = xSp.getStringSet(WATER_MARK_CONFIG, null);
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

    public boolean isCanShowLog(){
        return xSp.getBoolean(IS_CAN_SHOW_LOG, false);
    }
}
