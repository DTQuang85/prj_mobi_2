package com.example.app_ban_hang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends BaseActivity {

    private Product product;
    private int qty = 1;
    private Integer selectedSize = null;

    private final NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

    private ImageView ivImg, ivAvatar;
    private TextView tvName, tvPrice, tvDesc, tvQty;
    private Spinner spSize;
    private Button btnMinus, btnPlus, btnAddToCart, btnBuyNow;
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
        setupBottomNavigation(R.id.nav_products);
        bindData();
        bindEvents();
        loadUserAvatar();
    }

    private void initViews() {
        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }

        // Avatar
        ivAvatar = findViewById(R.id.ivAvatar);

        ivImg = findViewById(R.id.ivImg);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDesc = findViewById(R.id.tvDesc);
        tvQty = findViewById(R.id.tvQty);
        spSize = findViewById(R.id.spSize);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);

        // Xử lý click avatar
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> showProfileDialog());
        }
    }

    private void bindData() {
        // Ảnh
        Glide.with(this)
                .load(product.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(ivImg);

        // Thông tin
        tvName.setText(product.name != null ? product.name : "—");

        // Hiển thị giá
        if (product.priceText != null && !product.priceText.trim().isEmpty()) {
            tvPrice.setText(product.priceText);
        } else if (product.priceVnd > 0) {
            tvPrice.setText(vn.format(product.priceVnd));
        } else {
            tvPrice.setText("Liên hệ");
        }

        tvDesc.setText(product.description != null && !product.description.isEmpty()
                ? product.description : "Không có mô tả");
        tvQty.setText(String.valueOf(qty));

        // Sizes (Spinner)
        if (product.sizes != null && !product.sizes.isEmpty()) {
            List<String> items = new ArrayList<>();
            for (Integer size : product.sizes) {
                items.add("Size " + size);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, items
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSize.setAdapter(adapter);

            selectedSize = product.sizes.get(0); // mặc định chọn size đầu tiên

            spSize.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    selectedSize = product.sizes.get(position);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    selectedSize = null;
                }
            });
            spSize.setVisibility(View.VISIBLE);
        } else {
            spSize.setVisibility(View.GONE);
            selectedSize = null;
        }
    }

    private void bindEvents() {
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

        btnAddToCart.setOnClickListener(v -> {
            if (spSize.getVisibility() == View.VISIBLE && selectedSize == null) {
                Toast.makeText(this, "Vui lòng chọn size", Toast.LENGTH_SHORT).show();
                return;
            }
            CartManager.get().add(product, qty, selectedSize);
            Toast.makeText(this,
                    "Đã thêm " + qty + " sản phẩm" +
                            (selectedSize != null ? " (size " + selectedSize + ")" : "") +
                            " vào giỏ", Toast.LENGTH_SHORT).show();
        });

        btnBuyNow.setOnClickListener(v -> {
            if (spSize.getVisibility() == View.VISIBLE && selectedSize == null) {
                Toast.makeText(this, "Vui lòng chọn size", Toast.LENGTH_SHORT).show();
                return;
            }
            CartManager.get().clear();
            CartManager.get().add(product, qty, selectedSize);
            startActivity(new Intent(this, CartActivity.class));
            finish();
        });
    }

    private void loadUserAvatar() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && account.getPhotoUrl() != null && ivAvatar != null) {
            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivAvatar);
        }
    }

    public void showProfileDialog() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông tin tài khoản");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile, null);
        builder.setView(dialogView);

        TextView tvName = dialogView.findViewById(R.id.tvProfileName);
        TextView tvEmail = dialogView.findViewById(R.id.tvProfileEmail);
        ImageView ivProfileAvatar = dialogView.findViewById(R.id.ivProfileAvatar);
        Button btnLogout = dialogView.findViewById(R.id.btnProfileLogout);

        if (account != null) {
            tvName.setText(account.getDisplayName() != null ? account.getDisplayName() : "Không có tên");
            tvEmail.setText(account.getEmail() != null ? account.getEmail() : "Không có email");

            if (account.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(account.getPhotoUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivProfileAvatar);
            }
        } else if (mAuth.getCurrentUser() != null) {
            tvName.setText(mAuth.getCurrentUser().getDisplayName() != null ?
                    mAuth.getCurrentUser().getDisplayName() : "Không có tên");
            tvEmail.setText(mAuth.getCurrentUser().getEmail() != null ?
                    mAuth.getCurrentUser().getEmail() : "Không có email");

            if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivProfileAvatar);
            }
        }

        btnLogout.setOnClickListener(v -> signOut());

        builder.setNegativeButton("Đóng", null);
        builder.show();
    }

    private void signOut() {
        mAuth.signOut();
        GoogleSignIn.getClient(this, new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnCompleteListener(this, task -> {
                    Toast.makeText(ProductDetailActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}