package com.xiaoer.watermark.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class AppConfig implements Serializable {
    private int code;
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<AppBean> getAppList() {
        return data.getAppList();
    }

    public void setAppList(ArrayList<AppBean> appList) {
        data.setAppList(appList);
    }

    @Override
    public String toString() {
        return "AppConfig{" + "code=" + code + ", mData=" + data + '}';
    }

    public static class AppBean {
        private String packageName;
        private String[] versionCode;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String[] getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String[] versionCode) {
            this.versionCode = versionCode;
        }

        @Override
        public String toString() {
            return "AppBean{" + "packageName='" + packageName + '\'' + ", versionCode=" + Arrays.toString(versionCode) + '}';
        }
    }

    public static class Data{
        private ArrayList<AppBean> appList;

        public ArrayList<AppBean> getAppList() {
            return appList;
        }

        public void setAppList(ArrayList<AppBean> appList) {
            this.appList = appList;
        }

        @Override
        public String toString() {
            return "Data{" + "appList=" + appList + '}';
        }
    }
}
