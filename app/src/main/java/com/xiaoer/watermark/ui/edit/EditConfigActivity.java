package com.xiaoer.watermark.ui.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.xiaoer.watermark.R;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.hook.ConfigHelper;
import com.xiaoer.watermark.ui.WaterMarkView;
import com.xiaoer.watermark.ui.WatermarkDrawable;

public class EditConfigActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String REQUEST_CONFIG_KEY = "request_waterMarkConfig";

    private WaterMarkConfig mWaterMarkConfig;
    private SwitchMaterial mSmState;
    private FrameLayout mFlShow;
    private TextInputEditText mEtContent;
    private MaterialButton mMbColor;
    private MaterialButton mMbAppList;

    public static Intent getStartIntent(Context context){
        return new Intent(context, EditConfigActivity.class);
    }

    public static Intent getStartIntentWithConfig(Context context, WaterMarkConfig waterMarkConfig){
        Intent intent = new Intent(context, EditConfigActivity.class);
        intent.putExtra(REQUEST_CONFIG_KEY, waterMarkConfig);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_config);

        initView();

        Intent intent = getIntent();
        if (intent == null || intent.getSerializableExtra(REQUEST_CONFIG_KEY) == null){
            mWaterMarkConfig = new WaterMarkConfig();
        }else {
            mWaterMarkConfig = (WaterMarkConfig) intent.getSerializableExtra(REQUEST_CONFIG_KEY);
        }

        updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem save = menu.add("");
        save.setIcon(R.mipmap.ic_save);
        save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        save.setOnMenuItemClickListener(item -> {
            boolean success = ConfigHelper.saveWaterMarkConfig(EditConfigActivity.this, mWaterMarkConfig);
            if(success){
                finishEdit(true);
            }
            return true;
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        mSmState = findViewById(R.id.sm_state);
        mFlShow = findViewById(R.id.fl_show);
        mEtContent = findViewById(R.id.et_content);
        mMbColor = findViewById(R.id.mb_color);
        mMbAppList = findViewById(R.id.mb_appList);

        mMbColor.setOnClickListener(this);
        mMbAppList.setOnClickListener(this);
        mSmState.setOnCheckedChangeListener((buttonView, isChecked) -> mWaterMarkConfig.setOpen(isChecked));
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && !TextUtils.equals(s.toString(), mWaterMarkConfig.getContent())){
                    mWaterMarkConfig.setContent(TextUtils.isEmpty(s.toString()) ? "waterMark" : s.toString());
                    updateWaterView();
                }
            }
        });
    }

    private void updateWaterView() {
        WatermarkDrawable drawable = new WatermarkDrawable(EditConfigActivity.this, mWaterMarkConfig);
        WaterMarkView waterMarkView = new WaterMarkView(EditConfigActivity.this, drawable);
        mFlShow.removeAllViews();
        mFlShow.addView(waterMarkView);
    }

    private void updateView() {
        if(mWaterMarkConfig == null){
            return;
        }
        mSmState.setChecked(mWaterMarkConfig.isOpen());
        mEtContent.setText(mWaterMarkConfig.getContent());
        updateWaterView();

    }


    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(EditConfigActivity.this);
        materialAlertDialogBuilder.setTitle("退出而不保存");
        materialAlertDialogBuilder.setMessage("所有变更将会丢失");
        materialAlertDialogBuilder.setPositiveButton("确认", (dialog, which) -> finishEdit(false));
        materialAlertDialogBuilder.setNegativeButton("取消", null);
        materialAlertDialogBuilder.show();
    }

    public void finishEdit(boolean hasChange){
        if (hasChange){
            setResult(RESULT_OK);
        }
        finish();
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
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mb_color){

        }else {

        }
    }
}