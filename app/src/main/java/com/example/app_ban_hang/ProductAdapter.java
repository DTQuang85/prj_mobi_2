package com.example.app_ban_hang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> data;

    public ProductAdapter(List<Product> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = data.get(position);

        // Set data
        holder.tvName.setText(product.name != null ? product.name : "Không có tên");

        // Hiển thị giá
        if (product.priceText != null && !product.priceText.trim().isEmpty()) {
            holder.tvPrice.setText(product.priceText);
        } else if (product.priceVnd > 0) {
            java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(
                    new java.util.Locale("vi", "VN")
            );
            holder.tvPrice.setText(nf.format(product.priceVnd));
        } else {
            holder.tvPrice.setText("Liên hệ");
        }

        // Description
        if (product.description != null && !product.description.trim().isEmpty()) {
            holder.tvDesc.setText(product.description);
            holder.tvDesc.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        // Load image
        if (product.imageUrl != null && !product.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(holder.ivImg);
        } else {
            holder.ivImg.setImageResource(R.drawable.logo);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(
                    v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product", product);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName, tvPrice, tvDesc;

        ProductViewHolder(@NonNull View v) {
            super(v);
            ivImg = v.findViewById(R.id.ivImg);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvDesc = v.findViewById(R.id.tvDesc);
        }
    }
}