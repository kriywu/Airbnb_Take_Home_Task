package com.rengao.homework.Holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.rengao.homework.Abstract.BindHolder;
import com.rengao.homework.Module.Project;
import com.rengao.homework.R;

import androidx.annotation.NonNull;

/**
 * 用于显示项目信息
 */
public class ItemHolder extends BindHolder {
    private Context context;
    private ImageView ivAvatar; // 头像
    private TextView tvName; // 项目名称
    private TextView tvStar; // 星星数量

    public ItemHolder(@NonNull Context context, View itemView) {
        super(itemView);
        this.context = context;
        ivAvatar = itemView.findViewById(R.id.iv_avatar);
        tvName = itemView.findViewById(R.id.tv_name);
        tvStar = itemView.findViewById(R.id.tv_star);
    }

    public void bind(Object o) {
        Project project = (Project) o;
        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(16));
        Glide.with(context)
                .load(project.avatar)
                .apply(options)
                .into(ivAvatar);
        tvName.setText(project.name);
        tvStar.setText(String.valueOf(project.star));
    }
}
