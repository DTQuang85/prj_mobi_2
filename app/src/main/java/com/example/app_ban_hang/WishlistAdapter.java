package com.example.app_ban_hang;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.VH> {

    private List<WishlistItem> data;
    private final WishlistListener listener;
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public interface WishlistListener {
        void onItemRemoved(int position);
        void onItemClicked(Product product);
        void onAddToCart(Product product, Integer size);
    }

    public WishlistAdapter(List<WishlistItem> data, WishlistListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        WishlistItem item = data.get(position);
        Product p = item.product;

        h.tvName.setText(p.name);
        h.tvPrice.setText(p.priceText != null ? p.priceText :
                (p.priceVnd > 0 ? nf.format(p.priceVnd) : "Liên hệ"));

        if (item.size != null) {
            h.tvSize.setText("Size: " + item.size);
            h.tvSize.setVisibility(View.VISIBLE);
        } else {
            h.tvSize.setVisibility(View.GONE);
        }

        Glide.with(h.itemView.getContext())
                .load(p.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(h.ivImg);

        h.root.setOnClickListener(v -> {
            Log.e("WISHLIST_CLICK", "CLICKED: " + p.name);
            if (listener != null) {
                listener.onItemClicked(p);
            }
        });

        // Nút thêm vào giỏ hàng
        h.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCart(p, item.size);
            }
        });

        // Nút xóa khỏi wishlist
        h.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<WishlistItem> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        View root;  // thêm dòng này
        ImageView ivImg;
        TextView tvName, tvPrice, tvSize;
        Button btnAddToCart, btnRemove;

        VH(@NonNull View v) {
            super(v);
            root = v.findViewById(R.id.rootItem); // lấy container
            ivImg = v.findViewById(R.id.ivImg);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvSize = v.findViewById(R.id.tvSize);
            btnAddToCart = v.findViewById(R.id.btnAddToCart);
            btnRemove = v.findViewById(R.id.btnRemove);
        }
    }

}