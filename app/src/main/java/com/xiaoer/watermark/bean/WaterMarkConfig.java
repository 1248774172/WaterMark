package com.xiaoer.watermark.bean;

import android.graphics.Color;
import android.text.TextUtils;

import java.util.ArrayList;

public class WaterMarkConfig {
    public int textColor;
    public String content;
    public int textSize;
    public float rotation;
    public String configId;

    public ArrayList<String> packageList = new ArrayList<>();
    public int getTextColor() {
        return textColor == 0 ? Color.DKGRAY : textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "watermark" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTextSize() {
        return textSize == 0 ? 12 : textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public float getRotation() {
        return rotation == 0 ? -25 : rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public void addApp(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        packageList.add(packageName);
    }

    @Override
    public String toString() {
        return "WaterMarkConfig{" + "content='" + content + '\'' + ", configId='" + configId + '\'' + ", packageList=" + packageList + '}';
    }
}
