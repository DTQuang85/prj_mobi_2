package com.example.app_ban_hang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> data;
    private final CartListener listener;
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface CartListener {
        void onQuantityChanged();
        void onItemRemoved(int position);
    }

    public CartAdapter(List<CartItem> data, CartListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = data.get(position);
        Product product = item.product;

        // Set data (Giữ nguyên)
        holder.tvName.setText(product.name != null ? product.name : "Không có tên");
        holder.tvPrice.setText(nf.format(product.priceVnd));

        if (item.size != null) {
            holder.tvSize.setText("Size: " + item.size);
            holder.tvSize.setVisibility(View.VISIBLE);
        } else {
            holder.tvSize.setVisibility(View.GONE);
        }

        holder.tvQty.setText(String.valueOf(item.quantity));
        holder.tvLineTotal.setText("Tổng: " + nf.format(item.lineTotal()));

        // ============ LOGIC LOAD ẢNH ĐÃ KHÔI PHỤC ============
        if (product.imageUrl != null && !product.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.logo) // Dùng logo làm placeholder
                    .error(R.drawable.logo) // Dùng logo khi lỗi
                    .into(holder.ivImg);
        } else {
            // Nếu không có URL, hiển thị logo
            holder.ivImg.setImageResource(R.drawable.logo);
        }
        // ========================================================

        // Button listeners (Giữ nguyên)
        holder.btnPlus.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            CartItem currentItem = data.get(currentPosition);
            int newQty = currentItem.quantity + 1;

            CartManager.get().setQuantity(currentPosition, newQty);

            if (listener != null) listener.onQuantityChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            CartItem currentItem = data.get(currentPosition);
            if (currentItem.quantity > 1) {
                int newQty = currentItem.quantity - 1;
                CartManager.get().setQuantity(currentPosition, newQty);
                if (listener != null) listener.onQuantityChanged();
            } else {
                if (listener != null) listener.onItemRemoved(currentPosition);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;
            if (listener != null) listener.onItemRemoved(currentPosition);
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void updateData(List<CartItem> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName, tvPrice, tvQty, tvLineTotal, tvSize;
        TextView btnMinus, btnPlus, btnRemove;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.ivImg);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvLineTotal = itemView.findViewById(R.id.tvLineTotal);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            tvSize = itemView.findViewById(R.id.tvSize);
        }
    }
}