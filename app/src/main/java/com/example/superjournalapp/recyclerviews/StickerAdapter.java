package com.example.superjournalapp.recyclerviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superjournalapp.R;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {
    private List<Integer> stickerList; // List of sticker drawable resource IDs
    private StickerClickListener stickerClickListener;

    public interface StickerClickListener {
        void onStickerClick(int stickerResId);
    }

    public StickerAdapter(List<Integer> stickerList, StickerClickListener listener) {
        this.stickerList = stickerList;
        this.stickerClickListener = listener;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        int stickerResId = stickerList.get(position);
        holder.stickerImageView.setImageResource(stickerResId);
        holder.itemView.setOnClickListener(v -> {
            if (stickerClickListener != null) {
                stickerClickListener.onStickerClick(stickerResId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public static class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView;

        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerImageView);
        }
    }
}

