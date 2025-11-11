package com.example.app_ban_hang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Dùng Glide để tải ảnh (bạn đã có)

import java.util.List;

// (Adapter này dùng List<Integer> để lấy ảnh từ drawable)
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Integer> imageList;

    public BannerAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        int imageRes = imageList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(imageRes)
                .into(holder.ivBannerImage);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBannerImage;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
        }
    }
}