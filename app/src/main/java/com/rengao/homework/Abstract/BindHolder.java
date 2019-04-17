package com.rengao.homework.Abstract;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 抽象FootHolder 和 ItemHolder
 */
public abstract class BindHolder extends RecyclerView.ViewHolder {
    public BindHolder(@NonNull View itemView) {
        super(itemView);
    }
    public abstract void bind(Object o);
}
