package com.xiaoer.watermark.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatImageView;


public class WaterMarkView extends AppCompatImageView {
    private WatermarkDrawable mWatermarkDrawable;

    public WaterMarkView(Context context) {
        super(context);
        init();
    }

    public WaterMarkView(Context context, WatermarkDrawable watermarkDrawable) {
        super(context);
        this.mWatermarkDrawable = watermarkDrawable;
        init();
    }

    public WaterMarkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
        setBackground(mWatermarkDrawable);
    }
}
