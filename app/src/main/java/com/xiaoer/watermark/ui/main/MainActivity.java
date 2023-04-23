package com.xiaoer.watermark.ui.main;


import static com.xiaoer.watermark.hook.ConfigHelper.isModuleActivated;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xiaoer.watermark.R;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.hook.ConfigHelper;
import com.xiaoer.watermark.ui.edit.EditConfigActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public SwipeRefreshLayout mSrflRoot;
    public RecyclerView mRvConfigs;
    public MaterialCardView mModuleStatusCard;
    public ImageView mIvStatusIcon;
    public TextView mTvModuleStatus;
    public TextView mTvServiceStatus;
    public FloatingActionButton mFabAdd;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private List<WaterMarkConfig> mWaterMarkConfigs = new ArrayList<>();
    private MainActivityHandler mMainActivityHandler;
    private ConfigAdapter mConfigAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainActivityHandler = new MainActivityHandler(MainActivity.this);
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        updateConfigData();
                    }
                });

        initView();
    }

    private void initView() {
        mSrflRoot = findViewById(R.id.srfl_root);
        mRvConfigs = findViewById(R.id.rv_config);
        mModuleStatusCard = findViewById(R.id.module_status_card);
        mIvStatusIcon = findViewById(R.id.module_status_icon);
        mTvModuleStatus = findViewById(R.id.module_status_text);
        mTvServiceStatus = findViewById(R.id.service_status_text);
        mFabAdd = findViewById(R.id.fab_add);

        setModuleState();
        initReceiverView();

        mSrflRoot.setOnRefreshListener(this::updateConfigData);
        mFabAdd.setOnClickListener(v -> {
            Intent startIntent = EditConfigActivity.getStartIntent(MainActivity.this);
            mResultLauncher.launch(startIntent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem aboutMe = menu.add("关于");
        aboutMe.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void updateConfigData() {
        if(mSrflRoot != null){
            mSrflRoot.setRefreshing(true);
        }
        new Thread(() -> {
            mWaterMarkConfigs = ConfigHelper.getWaterMarkConfigs(getApplicationContext());
            mMainActivityHandler.sendEmptyMessage(MainActivityHandler.OK);
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRecyclerView(){
        if(mConfigAdapter != null){
            mConfigAdapter.setWaterMarkConfigs(mWaterMarkConfigs);
            mConfigAdapter.notifyDataSetChanged();
            mSrflRoot.setRefreshing(false);
        }
    }

    private void initReceiverView() {
        mConfigAdapter = new ConfigAdapter(MainActivity.this, mWaterMarkConfigs, mResultLauncher);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvConfigs.setLayoutManager(linearLayoutManager);
        mRvConfigs.setAdapter(mConfigAdapter);
        updateConfigData();
    }

    public void setModuleState(){
        if (isModuleActivated()) {
            mModuleStatusCard.setCardBackgroundColor(getColor(R.color.purple_500));
            mIvStatusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_check_circle_24));
            mTvModuleStatus.setText(getString(R.string.card_title_activated));
            mTvServiceStatus.setText(R.string.card_detail_activated);

        } else {
            mModuleStatusCard.setCardBackgroundColor(getColor(R.color.red_500));
            mIvStatusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.baseline_error_24));
            mTvModuleStatus.setText(getString(R.string.card_title_not_activated));
            mTvServiceStatus.setText(R.string.card_detail_not_activated);
        }
    }
}