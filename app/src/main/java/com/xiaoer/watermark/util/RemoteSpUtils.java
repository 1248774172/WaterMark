package com.xiaoer.watermark.util;


import static com.xiaoer.watermark.bean.SPContact.IS_CAN_SHOW_LOG;
import static com.xiaoer.watermark.bean.SPContact.SP_NAME;
import static com.xiaoer.watermark.bean.SPContact.WATER_MARK_CONFIG;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xiaoer.watermark.bean.WaterMarkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RemoteSpUtils {
    private Context mContext;

    public RemoteSpUtils(Context context) {
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

    private Context getContext() {
        if (mContext == null) {
            mContext = AndroidAppHelper.currentApplication();
        }
        return mContext;
    }

    private SharedPreferences getSp() {
        MultiprocessSharedPreferences.setAuthority("com.xiaoer.watermark.provider");
        return MultiprocessSharedPreferences.getSharedPreferences(getContext(), SP_NAME, Context.MODE_PRIVATE);
    }

    public boolean isCanShowLog(){
        return getSp().getBoolean(IS_CAN_SHOW_LOG, false);
    }
}
