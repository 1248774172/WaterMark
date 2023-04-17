package com.xiaoer.watermark.util;

import android.content.Context;

public class DensityUtils {

    public static int dip2px(Context context, float dipValue) {
        return Math.round(dipValue * (context.getResources().getDisplayMetrics().density));
    }

    public static int px2dip(Context context, float pxValue) {
        return Math.round(pxValue / (context.getResources().getDisplayMetrics().density));
    }
}
