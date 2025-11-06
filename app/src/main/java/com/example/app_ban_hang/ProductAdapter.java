package com.example.app_ban_hang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    private final List<Product> data;

    public ProductAdapter(List<Product> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Product p = data.get(position);
        h.img.setImageResource(p.imageRes);
        h.name.setText(p.name);
        h.price.setText(p.priceText);
        h.desc.setText(p.description);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, price, desc;
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.ivImg);
            name = v.findViewById(R.id.tvName);
            price = v.findViewById(R.id.tvPrice);
            desc = v.findViewById(R.id.tvDesc);
        }
    }
}
