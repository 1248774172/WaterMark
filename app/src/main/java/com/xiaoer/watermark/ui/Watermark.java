package com.xiaoer.watermark.ui;

import android.app.Activity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.ref.SoftReference;

public class Watermark {
    /**
     * 水印文本
     */
    private String mText;
    /**
     * 字体颜色，十六进制形式，例如：0xAEAEAEAE
     */
    private int mTextColor;
    /**
     * 字体大小，单位为sp
     */
    private float mTextSize;
    /**
     * 旋转角度
     */
    private float mRotation;

    private final SoftReference<Activity> mActivitySoftReference;
    private WaterMarkView mWaterMarkView;

    public Watermark(Activity activity) {
        this.mActivitySoftReference = new SoftReference<>(activity);
        ViewGroup rootView = getActivityRootView();
        if(rootView == null){
            return;
        }
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
            if(!(newFocus instanceof WaterMarkView)){
                show(mText);
            }
        });
    }


    /**
     * 设置水印文本
     *
     * @param text 文本
     * @return Watermark实例
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色，十六进制形式，例如：0xAEAEAEAE
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * 设置字体大小
     *
     * @param size 大小，单位为sp
     */
    public void setTextSize(float size) {
        mTextSize = size;
    }

    /**
     * 设置旋转角度
     *
     * @param degrees 度数
     */
    public void setRotation(float degrees) {
        mRotation = degrees;
    }

    /**
     * 显示水印，铺满整个页面
     *
     */
    public void show() {
        show(mText);
    }

    /**
     * 显示水印，铺满整个页面
     *
     * @param text     水印
     */
    public void show(String text) {
        removeIfExit();
        addIntoRootView(text);
    }

    private void addIntoRootView(String text){
        ViewGroup rootView = getActivityRootView();
        if(rootView == null){
            return;
        }
        WatermarkDrawable drawable = new WatermarkDrawable(rootView.getContext());
        drawable.mText = text;
        drawable.mTextColor = mTextColor;
        drawable.mTextSize = mTextSize;
        drawable.mRotation = mRotation;
        mWaterMarkView = new WaterMarkView(rootView.getContext(), drawable);
        rootView.post(() -> {
            if (mWaterMarkView.getParent() == null) {
                rootView.addView(mWaterMarkView, -1);
            }
        });
    }

    private ViewGroup getActivityRootView(){
        if(mActivitySoftReference == null || mActivitySoftReference.get() == null){
            return null;
        }
        return (ViewGroup) mActivitySoftReference.get().getWindow().getDecorView();
    }

    private void removeIfExit(){
        if(mWaterMarkView == null){
            return;
        }
        ViewGroup activityRootView = getActivityRootView();
        if(activityRootView != null){
            activityRootView.removeView(mWaterMarkView);
        }
    }
}
