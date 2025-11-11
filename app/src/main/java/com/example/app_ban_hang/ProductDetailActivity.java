package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
// XÓA import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import com.bumptech.glide.Glide;
// XÓA import com.google.android.gms.auth.api.signin.GoogleSignIn;
// XÓA import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
// XÓA import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import java.text.NumberFormat;
import java.util.*;

public class ProductDetailActivity extends BaseActivity {

    private Product product;
    private int qty = 1;
    private Integer selectedSize = null;

    private final NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

    // Đã xóa ivAvatar khỏi danh sách biến
    private ImageView ivImg;
    private TextView tvName, tvPrice, tvDesc, tvQty, tvSizeLabel, tvSelectedSize;
    private Spinner spSize;
    private TextView btnMinus, btnPlus;
    private ImageButton btnAddToCart, btnFavorites;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        mAuth = FirebaseAuth.getInstance();
        product = (Product) getIntent().getSerializableExtra("product");

        if (product == null) {
            Toast.makeText(this, "Lỗi: Không có thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        // Cần đảm bảo setupBottomNavigation được sửa trong BaseActivity
        setupBottomNavigation(R.id.nav_products);
        bindData();
        bindEvents();
        // ĐÃ XÓA loadUserAvatar();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivImg = findViewById(R.id.ivImg);
        // ivAvatar = findViewById(R.id.ivAvatar); // Nếu bạn có ivAvatar trong layout
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDesc = findViewById(R.id.tvDesc);
        tvQty = findViewById(R.id.tvQty);
        spSize = findViewById(R.id.spSize);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnFavorites = findViewById(R.id.btnFavorites);
        tvSizeLabel = findViewById(R.id.tvSizeLabel);
        tvSelectedSize = findViewById(R.id.tvSelectedSize);
    }

    private void bindData() {
        Glide.with(this)
                .load(product.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(ivImg);

        tvName.setText(product.name != null ? product.name : "Adidas Samba OG sneakers");
        tvPrice.setText(product.priceText != null ? product.priceText : formatPrice(product.priceVnd));
        tvDesc.setText(product.description != null ? product.description : "Không có mô tả");
        tvQty.setText(String.valueOf(qty));
        setupSizeSpinner();
    }

    private void setupSizeSpinner() {
        List<Integer> availableSizes = product.sizes != null ? product.sizes : new ArrayList<>();
        if (!availableSizes.isEmpty()) {
            List<String> sizeItems = new ArrayList<>();
            for (Integer size : availableSizes) {
                sizeItems.add("Size " + size);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizeItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSize.setAdapter(adapter);
            selectedSize = availableSizes.get(0);
            updateSelectedSizeDisplay();

            spSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedSize = availableSizes.get(position);
                    updateSelectedSizeDisplay();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedSize = null;
                    updateSelectedSizeDisplay();
                }
            });
        } else {
            tvSizeLabel.setVisibility(View.GONE);
            spSize.setVisibility(View.GONE);
            tvSelectedSize.setVisibility(View.GONE);
        }
    }

    private void updateSelectedSizeDisplay() {
        if (selectedSize != null) {
            tvSelectedSize.setText("Đã chọn: Size " + selectedSize);
            // Giữ nguyên dòng dưới nếu bạn có resource colors
            tvSelectedSize.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvSelectedSize.setText("Vui lòng chọn size");
            tvSelectedSize.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private String formatPrice(double price) {
        return String.format("%,d ₫", (int) price);
    }

    private void bindEvents() {
        btnMinus.setOnClickListener(v -> {
            if (qty > 1) qty--;
            tvQty.setText(String.valueOf(qty));
        });

        btnPlus.setOnClickListener(v -> {
            if (qty < 10) qty++;
            tvQty.setText(String.valueOf(qty));
        });

        btnAddToCart.setOnClickListener(v -> {
            CartManager.get().add(product, qty, selectedSize);
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        btnFavorites.setOnClickListener(v -> {
            WishlistManager.get().add(product, selectedSize);
            Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
        });
    }

    // ĐÃ XÓA loadUserAvatar()
    // ĐÃ XÓA showProfileDialog()
}