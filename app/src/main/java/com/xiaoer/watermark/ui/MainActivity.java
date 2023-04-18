package com.xiaoer.watermark.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaoer.watermark.BuildConfig;
import com.xiaoer.watermark.R;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.util.LocalSpUtils;

import java.util.List;

public class MainActivity extends Activity {

    private EditText mEtContent;
    private EditText mEtColor;
    private EditText mEtSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if(BuildConfig.DEBUG){
            LocalSpUtils.getInstance(getApplicationContext()).setDebug();
        }
    }

    private void initView() {
        Button btSave = findViewById(R.id.bt_save);
        mEtContent = findViewById(R.id.et_content);
        mEtColor = findViewById(R.id.et_color);
        mEtSize = findViewById(R.id.et_size);

        LocalSpUtils instance = LocalSpUtils.getInstance(getApplicationContext());

        WaterMarkConfig waterMarkConfig = new WaterMarkConfig();
        waterMarkConfig.content = "jxjTest";
        waterMarkConfig.configId = System.currentTimeMillis() + "";
        waterMarkConfig.addApp("com.jd.jxj");
        waterMarkConfig.addApp("com.xiaoer.watermark");

        btSave.setOnClickListener(v -> {
            instance.saveWaterMarkConfig(waterMarkConfig);
            List<WaterMarkConfig> waterMarkConfigs = instance.getWaterMarkConfigs();
            Toast.makeText(getApplicationContext(), waterMarkConfigs.toString() , Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            View currentFocus = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideInputMethod(this, v);
                if (currentFocus != null) {
                    // 点击EditText外的其他区域。  关闭键盘  取消光标显示
                    if (currentFocus instanceof EditText) {
                        currentFocus.clearFocus();
                    }
                }
            } else {
                // 点击EditText的事件,不需要隐藏键盘，需要显示光标。
                if (currentFocus != null) {
                    if (currentFocus instanceof EditText) {
                        currentFocus.requestFocus();
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);

    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public void hideInputMethod(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}