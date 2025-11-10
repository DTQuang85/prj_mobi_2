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
    private TextView tvName, tvPrice, tvDesc, tvQty, tvSizeLabel, tvSelectedSize;
    private Spinner spSize;
    private Button btnMinus, btnPlus, btnAddToCart, btnFavorites;
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Avatar
        ivAvatar = findViewById(R.id.ivAvatar);

        // Product views
        ivImg = findViewById(R.id.ivImg);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDesc = findViewById(R.id.tvDesc);
        tvQty = findViewById(R.id.tvQty);
        spSize = findViewById(R.id.spSize);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnFavorites = findViewById(R.id.btnFavorites);

        // Size related views - THÊM CÁC VIEW MỚI
        tvSizeLabel = findViewById(R.id.tvSizeLabel);
        tvSelectedSize = findViewById(R.id.tvSelectedSize);

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
        tvName.setText(product.name != null ? product.name : "Adidas Samba OG sneakers");

        // Hiển thị giá
        if (product.priceText != null && !product.priceText.trim().isEmpty()) {
            tvPrice.setText(product.priceText);
        } else if (product.priceVnd > 0) {
            tvPrice.setText(formatPrice(product.priceVnd));
        } else {
            tvPrice.setText("2.600.000 ₫");
        }

        tvDesc.setText(product.description != null && !product.description.isEmpty()
                ? product.description : "Giày Adidas Samba cổ điển với thiết kế tối giản, phối màu nhã nhặn, phù hợp để đi chơi và tập luyện nhẹ");
        tvQty.setText(String.valueOf(qty));

        // Sizes (Spinner) - FIX: Sử dụng method riêng
        setupSizeSpinner();
    }

    private void setupSizeSpinner() {
        // Sử dụng getSizes() để tránh null
        List<Integer> availableSizes = product.sizes != null ? product.sizes : new ArrayList<>();

        // DEBUG: Kiểm tra sizes
        Toast.makeText(this, "Số lượng sizes: " + availableSizes.size(), Toast.LENGTH_SHORT).show();

        if (availableSizes != null && !availableSizes.isEmpty()) {
            List<String> sizeItems = new ArrayList<>();
            for (Integer size : availableSizes) {
                sizeItems.add("Size " + size);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, sizeItems
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSize.setAdapter(adapter);

            selectedSize = availableSizes.get(0); // mặc định chọn size đầu tiên
            updateSelectedSizeDisplay();

            spSize.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    selectedSize = availableSizes.get(position);
                    updateSelectedSizeDisplay();
                    Toast.makeText(ProductDetailActivity.this, "Đã chọn size: " + selectedSize, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    selectedSize = null;
                    updateSelectedSizeDisplay();
                }
            });

            // Hiển thị phần chọn size
            tvSizeLabel.setVisibility(View.VISIBLE);
            spSize.setVisibility(View.VISIBLE);
            tvSelectedSize.setVisibility(View.VISIBLE);
        } else {
            // Ẩn phần chọn size nếu không có
            tvSizeLabel.setVisibility(View.GONE);
            spSize.setVisibility(View.GONE);
            tvSelectedSize.setVisibility(View.GONE);
            Toast.makeText(this, "Sản phẩm này không có size", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSelectedSizeDisplay() {
        if (tvSelectedSize != null) {
            if (selectedSize != null) {
                tvSelectedSize.setText("Đã chọn: Size " + selectedSize);
                tvSelectedSize.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvSelectedSize.setText("Vui lòng chọn size");
                tvSelectedSize.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    private String formatPrice(double price) {
        return String.format("%,d ₫", (int) price);
    }

    private void bindEvents() {
        btnMinus.setOnClickListener(v -> {
            if (qty > 1) {
                qty--;
                tvQty.setText(String.valueOf(qty));
            } else {
                Toast.makeText(this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (qty < 10) { // Giới hạn tối đa 10 sản phẩm
                qty++;
                tvQty.setText(String.valueOf(qty));
            } else {
                Toast.makeText(this, "Số lượng tối đa là 10", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if (spSize.getVisibility() == View.VISIBLE && selectedSize == null) {
                Toast.makeText(this, "Vui lòng chọn size", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm vào giỏ hàng
            CartManager.get().add(product, qty, selectedSize);
            String message = "Đã thêm " + qty + " " + product.name +
                    (selectedSize != null ? " (size " + selectedSize + ")" : "") +
                    " vào giỏ hàng";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });

        btnFavorites.setOnClickListener(v -> {
            if (spSize.getVisibility() == View.VISIBLE && selectedSize == null) {
                Toast.makeText(this, "Vui lòng chọn size", Toast.LENGTH_SHORT).show();
                return;
            }

            WishlistManager.get().add(product, selectedSize);
            Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();

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
        } else if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null && ivAvatar != null) {
            Glide.with(this)
                    .load(mAuth.getCurrentUser().getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivAvatar);
        }
    }

    @Override
    protected void showProfileDialog() {
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
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnCompleteListener(this, task -> {
                    Toast.makeText(ProductDetailActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}