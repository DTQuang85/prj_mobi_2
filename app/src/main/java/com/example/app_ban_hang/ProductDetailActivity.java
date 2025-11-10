package com.example.app_ban_hang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private Product product;
    private int qty = 1;
    private final NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Lỗi: Không có thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        bindEvents();
    }

    private void initViews() {
        ImageView iv = findViewById(R.id.ivImg);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvDesc = findViewById(R.id.tvDesc);
        TextView tvQty = findViewById(R.id.tvQty);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnAdd = findViewById(R.id.btnAddToCart);
        Button btnBuy = findViewById(R.id.btnBuyNow);

        // Load ảnh
        Glide.with(this)
                .load(product.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(iv);

        // Hiển thị thông tin
        tvName.setText(product.name);
        tvPrice.setText(product.priceText);
        tvDesc.setText(product.description != null ? product.description : "Không có mô tả");
        tvQty.setText(String.valueOf(qty));
    }

    private void bindEvents() {
        TextView tvQty = findViewById(R.id.tvQty);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnAdd = findViewById(R.id.btnAddToCart);
        Button btnBuy = findViewById(R.id.btnBuyNow);

        btnMinus.setOnClickListener(v -> {
            if (qty > 1) {
                qty--;
                tvQty.setText(String.valueOf(qty));
            }
        });

        btnPlus.setOnClickListener(v -> {
            qty++;
            tvQty.setText(String.valueOf(qty));
        });

        btnAdd.setOnClickListener(v -> {
            CartManager.get().add(product, qty);
            Toast.makeText(this, "Đã thêm " + qty + " sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();
        });

        btnBuy.setOnClickListener(v -> {
            CartManager.get().clear();
            CartManager.get().add(product, qty);
            startActivity(new android.content.Intent(this, CartActivity.class));
            finish();
        });
    }
}