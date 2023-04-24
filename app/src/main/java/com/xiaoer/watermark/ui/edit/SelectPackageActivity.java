package com.xiaoer.watermark.ui.edit;

import static com.xiaoer.watermark.ui.edit.EditConfigActivity.REQUEST_CONFIG_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xiaoer.watermark.R;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.hook.ConfigHelper;

import java.util.ArrayList;

public class SelectPackageActivity extends AppCompatActivity {

    private ArrayList<PackageInfo> mInstalledPackages;

    private WaterMarkConfig originConfig;
    private SwipeRefreshLayout mSrlRoot;
    private SelectPackageAdapter mSelectPackageAdapter;
    private WaterMarkConfig mWaterMarkConfig;
    private RecyclerView mRvAppList;
    private SearchView mSearchView;

    public static Intent getStartIntent(Context context){
        return new Intent(context, SelectPackageActivity.class);
    }

    public static Intent getStartIntentWithConfig(Context context, WaterMarkConfig waterMarkConfig){
        Intent startIntent = getStartIntent(context);
        startIntent.putExtra(REQUEST_CONFIG_KEY, waterMarkConfig);
        return startIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_app);

        initData();
        initView();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        mRvAppList = findViewById(R.id.rv_appList);
        mSrlRoot = findViewById(R.id.srl_root);

        mSrlRoot.setOnRefreshListener(() -> {
            if(mSearchView.hasFocus()){
                mSearchView.clearFocus();
            }else {
                if (mSelectPackageAdapter != null){
                    mSelectPackageAdapter.updateSelectList(value -> mSrlRoot.post(() -> mSelectPackageAdapter.notifyDataSetChanged()));
                }
            }
            mSrlRoot.setRefreshing(false);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectPackageAdapter = new SelectPackageAdapter(this, mInstalledPackages, mWaterMarkConfig.getAppList());
        mRvAppList.setAdapter(mSelectPackageAdapter);
        mRvAppList.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        if(getIntent() != null && getIntent().getSerializableExtra(REQUEST_CONFIG_KEY) instanceof WaterMarkConfig){
            mWaterMarkConfig = (WaterMarkConfig) getIntent().getSerializableExtra(REQUEST_CONFIG_KEY);
        }
        originConfig = mWaterMarkConfig;
        mInstalledPackages = (ArrayList<PackageInfo>) getPackageManager().getInstalledPackages(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //引用menu文件
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem save = menu.findItem(R.id.menu_save);
        save.setOnMenuItemClickListener(item -> {
            if(mSelectPackageAdapter != null){
                mWaterMarkConfig.setAppList(mSelectPackageAdapter.getSelectAppList());
                boolean success = ConfigHelper.saveWaterMarkConfig(SelectPackageActivity.this, mWaterMarkConfig);
                if(success){
                    finishEdit();
                }
            }
            return true;
        });

        // //找到SearchView并配置相关参数
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // //设置提示词
        mSearchView.setQueryHint("搜索应用");
        @SuppressLint("NotifyDataSetChanged")
        ValueCallback<Boolean> filterCallback = value -> {
            if(mSelectPackageAdapter != null){
                runOnUiThread(() -> mSelectPackageAdapter.notifyDataSetChanged());
            }
        };
        mSearchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                if(mSelectPackageAdapter != null){
                    mSelectPackageAdapter.filterPackage("", filterCallback);
                }
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mSelectPackageAdapter != null){
                    mSelectPackageAdapter.filterPackage(newText, filterCallback);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void finishEdit(){
        Intent data = new Intent();
        data.putExtra("result", mWaterMarkConfig);
        setResult(RESULT_OK, data);
        finish();
    }
}
