package com.xiaoer.watermark.ui.main;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class MainActivityHandler extends Handler {

    public static final int OK = -1;
    private final WeakReference<MainActivity> mActivity;

    public MainActivityHandler(MainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what == OK){
            MainActivity mainActivity = mActivity.get();
            if(mainActivity != null){
                mainActivity.getWindow().getDecorView().postDelayed(mainActivity::updateRecyclerView, 400);
            }
        }
    }
}
