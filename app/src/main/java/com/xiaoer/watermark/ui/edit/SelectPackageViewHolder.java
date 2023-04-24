package com.xiaoer.watermark.ui.edit;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.xiaoer.watermark.R;

public class SelectPackageViewHolder extends RecyclerView.ViewHolder {

    public final MaterialCardView mMcvRoot;
    public final ShapeableImageView mIvAppIcon;
    public final MaterialTextView mTvPackageName;
    public final MaterialTextView mTvAppName;
    public final MaterialCheckBox mMcbCheck;

    public SelectPackageViewHolder(@NonNull View itemView) {
        super(itemView);
        mMcvRoot = itemView.findViewById(R.id.mcv_root);
        mIvAppIcon = mMcvRoot.findViewById(R.id.iv_appIcon);
        mTvAppName = mMcvRoot.findViewById(R.id.mtv_appName);
        mTvPackageName = mMcvRoot.findViewById(R.id.mtv_packageName);
        mMcbCheck = mMcvRoot.findViewById(R.id.mcb_check);
    }

}
