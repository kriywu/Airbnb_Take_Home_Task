package com.rengao.homework.Holder;

import android.view.View;
import android.widget.TextView;

import com.rengao.homework.Abstract.BindHolder;
import com.rengao.homework.R;

import androidx.annotation.NonNull;

/**
 * foot holder 用于显示"Loading"或者"Hit Bottom"
 */
public class FootHolder extends BindHolder {
    private TextView tvFoot;
    public FootHolder(@NonNull View itemView) {
        super(itemView);
        tvFoot = itemView.findViewById(R.id.tv_foot);
    }

    @Override
    public void bind(Object o) {
        boolean end = (boolean) o;
        if(end) tvFoot.setText(R.string.hint_end);
        else tvFoot.setText(R.string.hint_loading);
    }
}