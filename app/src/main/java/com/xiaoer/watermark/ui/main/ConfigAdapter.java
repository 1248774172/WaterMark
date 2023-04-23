package com.xiaoer.watermark.ui.main;

import static com.xiaoer.watermark.hook.ConfigHelper.isModuleActivated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoer.watermark.R;
import com.xiaoer.watermark.bean.WaterMarkConfig;
import com.xiaoer.watermark.hook.ConfigHelper;
import com.xiaoer.watermark.ui.edit.EditConfigActivity;
import com.xiaoer.watermark.util.DensityUtils;

import java.util.List;

public class ConfigAdapter extends RecyclerView.Adapter<ConfigViewHolder>{
    private List<WaterMarkConfig> mWaterMarkConfigs;
    private final ActivityResultLauncher<Intent> mResultLauncher;
    private final Context mContext;

    private Point lastTouchPosition = new Point(0, 0);

    public ConfigAdapter(Context context, List<WaterMarkConfig> waterMarkConfigs, ActivityResultLauncher<Intent> resultLauncher){
        this.mContext = context;
        this.mWaterMarkConfigs = waterMarkConfigs;
        this.mResultLauncher = resultLauncher;
    }

    public void setWaterMarkConfigs(List<WaterMarkConfig> waterMarkConfigs){
        this.mWaterMarkConfigs = waterMarkConfigs;
    }

    @NonNull
    @Override
    public ConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_config, parent, false);
        return new ConfigViewHolder(inflate);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ConfigViewHolder holder, int position) {
        WaterMarkConfig waterMarkConfig = mWaterMarkConfigs.get(position);
        if (waterMarkConfig.isOpen() && isModuleActivated()){
            holder.itemView.setAlpha(1f);
        }else {
            holder.itemView.setAlpha(0.5f);
        }
        holder.mTvContent.setText(waterMarkConfig.getContent());
        holder.mCardView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastTouchPosition.set((int) event.getX(), (int) event.getY());
            }
            return false;
        });
        holder.mCardView.setOnClickListener(v -> {
            Intent startIntentWithConfig = EditConfigActivity.getStartIntentWithConfig(mContext, waterMarkConfig);
            mResultLauncher.launch(startIntentWithConfig);
        });
        holder.mCardView.setOnLongClickListener(v -> {
            showPopupWindow(v, waterMarkConfig);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mWaterMarkConfigs.size();
    }

    private void showPopupWindow(View view, WaterMarkConfig waterMarkConfig){
        int dip2px = DensityUtils.dip2px(mContext, 100);
        PopupWindow popupWindow = new PopupWindow(dip2px, dip2px);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        View rootView = View.inflate(mContext, R.layout.popup_action, null);
        Button mbState = rootView.findViewById(R.id.mb_state);
        Button mbDelete = rootView.findViewById(R.id.mb_delete);
        if(waterMarkConfig.isOpen){
            mbState.setText("禁用");
        }else {
            mbState.setText("启用");
        }
        mbState.setOnClickListener(v -> {
            waterMarkConfig.setOpen(!waterMarkConfig.isOpen);
            ConfigHelper.saveWaterMarkConfig(mContext, waterMarkConfig);
            notifyDataSetChanged();
            popupWindow.dismiss();
        });
        mbDelete.setOnClickListener(v -> {
            ConfigHelper.deleteWaterMarkConfig(mContext, waterMarkConfig);
            notifyDataSetChanged();
            popupWindow.dismiss();
        });
        popupWindow.setContentView(rootView);
        Log.d("yys ", "showPopupWindow: " + lastTouchPosition);
        popupWindow.showAsDropDown(view, lastTouchPosition.x, lastTouchPosition.y - view.getMeasuredHeight(), Gravity.NO_GRAVITY);

    }
}

