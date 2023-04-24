package com.xiaoer.watermark.ui.edit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoer.watermark.R;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class SelectPackageAdapter extends RecyclerView.Adapter<SelectPackageViewHolder> {

    private final CopyOnWriteArrayList<PackageInfo> mAllPackages;

    private CopyOnWriteArrayList<PackageInfo> mShowPackages;
    private final CopyOnWriteArrayList<String> mSelectList;
    private final Context mContext;

    public SelectPackageAdapter(Context context, ArrayList<PackageInfo> packageInfoList, ArrayList<String> selectList) {
        mContext = context;
        mSelectList = new CopyOnWriteArrayList<>(selectList);
        mAllPackages = new CopyOnWriteArrayList<>(packageInfoList);
        mShowPackages = new CopyOnWriteArrayList<>(packageInfoList);
        updateSelectList(null);
    }

    public void updateSelectList(ValueCallback<Boolean> callback) {
        new Thread(() -> {
            ArrayList<PackageInfo> result = new ArrayList<>();
            int preCheckIndex = 0;
            for (PackageInfo item: mAllPackages){
                if(mSelectList.contains(item.packageName)){
                    result.add(preCheckIndex++ , item);
                }else {
                    result.add(item);
                }
            }
            mShowPackages = new CopyOnWriteArrayList<>(result);
            if (callback != null){
                callback.onReceiveValue(true);
            }
        }).start();
    }

    public void filterPackage(String filter, ValueCallback<Boolean> callback){
        new Thread(() -> {
            if (TextUtils.isEmpty(filter)) {
                updateSelectList(null);
            } else {
                mShowPackages = new CopyOnWriteArrayList<>();
                for (PackageInfo packageInfo : mAllPackages) {
                    if (packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString().contains(filter)) {
                        mShowPackages.add(packageInfo);
                    }
                }
            }
            if (callback != null) {
                callback.onReceiveValue(true);
            }
        }).start();
    }

    public ArrayList<String> getSelectAppList(){
        return new ArrayList<>(mSelectList);
    }

    @NonNull
    @Override
    public SelectPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_app_select, parent, false);
        return new SelectPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectPackageViewHolder holder, int position) {
        PackageInfo packageInfo = mShowPackages.get(position);
        holder.mMcvRoot.setOnClickListener(v -> holder.mMcbCheck.setChecked(!holder.mMcbCheck.isChecked()));
        holder.mMcbCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                if (!mSelectList.contains(packageInfo.packageName)) {
                    mSelectList.add(packageInfo.packageName);
                }
            }else {
                mSelectList.remove(packageInfo.packageName);
            }
        });
        holder.mMcbCheck.setChecked(mSelectList.contains(packageInfo.packageName));
        holder.mTvAppName.setText(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()));
        holder.mIvAppIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(mContext.getPackageManager()));
        holder.mTvPackageName.setText(packageInfo.packageName);

    }

    @Override
    public int getItemCount() {
        return mShowPackages.size();
    }
}
