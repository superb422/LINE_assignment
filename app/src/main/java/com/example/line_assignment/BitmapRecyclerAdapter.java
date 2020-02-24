package com.example.line_assignment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.line_assignment.databinding.RecyclerImageDataBinding;

import java.util.List;

public class BitmapRecyclerAdapter extends RecyclerView.Adapter<BitmapRecyclerAdapter.ViewHolder> {

    private List<BitmapData> bitmapDatas;
    final Context context;
    public OnBindCallback onBind;

    public BitmapRecyclerAdapter(List<BitmapData> bitmapDatas, Context context) {
        this.bitmapDatas = bitmapDatas;
        this.context = context;
    }



    @NonNull
    @Override
    public BitmapRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerImageDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.recycler_image_data, parent, false);

        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        BitmapData bitmapData = bitmapDatas.get(position);
        holder.imagebinding.memoImage.setImageBitmap(bitmapData.bitmap);
        holder.imagebinding.memoImage.setBackground(new ShapeDrawable(new OvalShape()));
        holder.imagebinding.memoImage.setClipToOutline(true);

        if (onBind != null) {
            onBind.onViewBound(holder, position);
        }
    }


    @Override
    public int getItemCount() {
        return bitmapDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerImageDataBinding imagebinding;

        public ViewHolder(RecyclerImageDataBinding binding) {
            super(binding.getRoot());
            this.imagebinding = binding;
        }
    }

}