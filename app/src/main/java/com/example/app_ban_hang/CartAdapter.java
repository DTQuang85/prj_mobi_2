package com.example.app_ban_hang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

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
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CartItem item = data.get(position);
        Product p = item.product;

        // Hiển thị thông tin
        h.tvName.setText(p.name);
        h.tvPrice.setText(p.priceText);
        h.tvQty.setText(String.valueOf(item.quantity));

        // Tổng tiền
        double itemTotal = item.lineTotal();
        h.tvLineTotal.setText("Tổng: " + nf.format(itemTotal));

        // Load ảnh
        Glide.with(h.itemView.getContext())
                .load(p.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(h.ivImg);

        // Sự kiện NÚT CỘNG (+)
        h.btnPlus.setOnClickListener(v -> {
            int newQty = item.quantity + 1;
            CartManager.get().setQuantity(position, newQty);

            // Cập nhật hiển thị
            h.tvQty.setText(String.valueOf(newQty));
            double newTotal = item.lineTotal();
            h.tvLineTotal.setText("Tổng: " + nf.format(newTotal));

            // Thông báo
            if (listener != null) listener.onQuantityChanged();
        });

        // Sự kiện NÚT TRỪ (-)
        h.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                int newQty = item.quantity - 1;
                CartManager.get().setQuantity(position, newQty);

                // Cập nhật hiển thị
                h.tvQty.setText(String.valueOf(newQty));
                double newTotal = item.lineTotal();
                h.tvLineTotal.setText("Tổng: " + nf.format(newTotal));

                // Thông báo
                if (listener != null) listener.onQuantityChanged();
            } else {
                // Nếu số lượng = 1, bấm trừ sẽ xóa
                if (listener != null) listener.onItemRemoved(position);
            }
        });

        // Sự kiện NÚT XÓA
        h.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onItemRemoved(position);
        });
    } // ĐÃ XÓA DẤU NGOẶC THỪA Ở ĐÂY

    @Override
    public int getItemCount() {
        return data.size();
    }

    // Cập nhật dữ liệu khi có thay đổi
    public void updateData(List<CartItem> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName, tvPrice, tvQty, tvLineTotal;
        Button btnMinus, btnPlus, btnRemove;

        VH(@NonNull View v) {
            super(v);
            ivImg = v.findViewById(R.id.ivImg);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvQty = v.findViewById(R.id.tvQty);
            tvLineTotal = v.findViewById(R.id.tvLineTotal);
            btnMinus = v.findViewById(R.id.btnMinus);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnRemove = v.findViewById(R.id.btnRemove);
        }
    }
}