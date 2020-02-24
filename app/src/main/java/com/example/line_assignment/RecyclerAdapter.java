package com.example.line_assignment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.line_assignment.databinding.RecyclerMemoDataBinding;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<MemoData> memoDatas;
    final Context context;

    public RecyclerAdapter(List<MemoData> memoDatas, Context context) {
        this.memoDatas = memoDatas;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerMemoDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.recycler_memo_data, parent, false);

        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
            MemoData memoData = memoDatas.get(position);
            holder.memobinding.setMemoType(memoData);
            holder.memobinding.listMemoImage.setImageBitmap(memoData.bitmap);
            holder.memobinding.listMemoImage.setBackground(new ShapeDrawable(new OvalShape()));
            holder.memobinding.listMemoImage.setClipToOutline(true);

            holder.memobinding.memoList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), EditActivity.class);
                    intent.putExtra("title",memoDatas.get(position).title);
                    intent.putExtra("content",memoDatas.get(position).content);
                    intent.putExtra("fileName",memoDatas.get(position).filename);
                    intent.putExtra("Option","Detail");
                    view.getContext().startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return memoDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RecyclerMemoDataBinding memobinding;

        public ViewHolder(RecyclerMemoDataBinding binding) {
            super(binding.getRoot());
            this.memobinding = binding;
        }
    }
}
