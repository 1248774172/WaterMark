package com.xiaoer.watermark.bean;

import android.graphics.Color;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class WaterMarkConfig implements Serializable {
    public int textColor;
    public String content;
    public int textSize;
    public float rotation;
    public String configId;

    public boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

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

    public void setAppList(ArrayList<String> newList) {
        if(newList == null){
            newList = new ArrayList<>();
        }
        packageList = newList;
    }

    public ArrayList<String> getAppList(){
        return packageList;
    }

    @Override
    public String toString() {
        return "WaterMarkConfig{" + "content='" + content + '\'' + ", configId='" + configId + '\'' + ", packageList=" + packageList + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WaterMarkConfig that = (WaterMarkConfig) o;
        return textColor == that.textColor && textSize == that.textSize && Float.compare(
                that.rotation, rotation) == 0 && isOpen == that.isOpen
                && Objects.equals(content, that.content)
                && Objects.equals(configId, that.configId)
                && Objects.equals(packageList, that.packageList);
    }
}
