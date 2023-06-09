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
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.xiaoer.watermark.R;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.hook.ConfigHelper;
import com.xiaoer.watermark.ui.WaterMarkView;
import com.xiaoer.watermark.ui.WatermarkDrawable;

public class EditConfigActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String REQUEST_CONFIG_KEY = "request_waterMarkConfig";

    private WaterMarkConfig mWaterMarkConfig;
    private SwitchMaterial mSmState;
    private FrameLayout mFlShow;
    private TextInputEditText mEtContent;
    private AppCompatSeekBar mAsbRotation;
    private AppCompatSeekBar mAsbTextSize;

    private ActivityResultLauncher<Intent> mResultLauncher;

    private boolean shouldReload = false;

    public static Intent getStartIntent(Context context){
        return new Intent(context, EditConfigActivity.class);
    }

    public static Intent getStartIntentWithConfig(Context context, WaterMarkConfig waterMarkConfig){
        Intent startIntent = getStartIntent(context);
        startIntent.putExtra(REQUEST_CONFIG_KEY, waterMarkConfig);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_config);

        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                mWaterMarkConfig = (WaterMarkConfig) result.getData().getSerializableExtra("result");
                shouldReload = true;
            }
        });
        initData();
        initView();
        updateView();
    }

    private void initData() {
        Intent intent = getIntent();
        if(intent != null && intent.getSerializableExtra(REQUEST_CONFIG_KEY) != null){
            mWaterMarkConfig = (WaterMarkConfig) intent.getSerializableExtra(REQUEST_CONFIG_KEY);
        }else {
            mWaterMarkConfig = new WaterMarkConfig();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem save = menu.add("");
        save.setIcon(R.drawable.baseline_save_24);
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
        MaterialCardView mcvStatus = findViewById(R.id.module_status_card);
        TextView tvStatus = findViewById(R.id.module_status_text);
        mFlShow = findViewById(R.id.fl_show);
        mEtContent = findViewById(R.id.et_content);
        MaterialButton mbColor = findViewById(R.id.mb_color);
        MaterialButton mbAppList = findViewById(R.id.mb_appList);
        mAsbRotation = findViewById(R.id.asb_rotation);
        mAsbTextSize = findViewById(R.id.asb_text_size);

        mAsbRotation.setMax(360);
        mAsbTextSize.setMax(100);
        mAsbTextSize.setMin(1);
        mbColor.setOnClickListener(this);
        mbAppList.setOnClickListener(this);
        if(ConfigHelper.isModuleActivated()){
            mcvStatus.setCardBackgroundColor(getColor(R.color.purple_500));
            tvStatus.setText("启用模块");
            mSmState.setEnabled(true);
        }else {
            mcvStatus.setCardBackgroundColor(getColor(R.color.red_500));
            tvStatus.setText("模块未成功激活");
            mSmState.setEnabled(false);
        }
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
        mAsbRotation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaterMarkConfig.setRotation(-progress);
                updateWaterView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mAsbTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mWaterMarkConfig.setTextSize(progress);
                updateWaterView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        mAsbRotation.setProgress((int) Math.abs(mWaterMarkConfig.getRotation()));
        mAsbTextSize.setProgress(Math.abs(mWaterMarkConfig.getTextSize()));

    }


    @Override
    public void onBackPressed() {
        if(!shouldReload){
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(EditConfigActivity.this);
            materialAlertDialogBuilder.setTitle("退出而不保存");
            materialAlertDialogBuilder.setMessage("所有变更将会丢失");
            materialAlertDialogBuilder.setPositiveButton("确认", (dialog, which) -> {
                dialog.dismiss();
                finishEdit(false);
            });
            materialAlertDialogBuilder.setNegativeButton("取消", null);
            materialAlertDialogBuilder.show();
        }else {
            finishEdit(true);
        }
    }

    public void finishEdit(boolean hasChange){
        if (shouldReload || hasChange){
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
            ColorPickerDialogBuilder
                    .with(EditConfigActivity.this)
                    .setTitle("选择水印颜色")
                    .initialColor(mWaterMarkConfig.getTextColor())
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("确认", (dialog, selectedColor, allColors) -> {
                        mWaterMarkConfig.setTextColor(selectedColor);
                        updateWaterView();
                        dialog.dismiss();
                    })
                    .setNegativeButton("取消", null)
                    .build()
                    .show();
        }else if(id == R.id.mb_appList) {
            mResultLauncher.launch(SelectPackageActivity.getStartIntentWithConfig(EditConfigActivity.this, mWaterMarkConfig));
        }
    }
}