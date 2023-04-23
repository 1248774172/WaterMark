package com.xiaoer.watermark.ui.main;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.xiaoer.watermark.R;

public class ConfigViewHolder extends RecyclerView.ViewHolder {

    public MaterialCardView mCardView;
    public TextView mTvContent;

    public ConfigViewHolder(@NonNull View itemView) {
        super(itemView);
        mCardView = itemView.findViewById(R.id.mcv_root);
        mTvContent = itemView.findViewById(R.id.tv_content);
    }
}
