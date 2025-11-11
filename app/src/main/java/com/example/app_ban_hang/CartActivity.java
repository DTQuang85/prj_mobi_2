package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity implements CartAdapter.CartListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvTotal;
    private TextView btnNext;
    private ImageView ivAvatar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupBottomNavigation(R.id.nav_cart);
        setupRecyclerView();
        updateTotal();
    }

    private void initViews() {
        // Toolbar
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Giỏ hàng");
        }

        rvCart = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvTotal);

        // SỬA: Tìm TextView thay vì Button
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            if (CartManager.get().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });
    }

    private void setupRecyclerView() {
        List<CartItem> cartItems = CartManager.get().getItems();
        adapter = new CartAdapter(cartItems, this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);
    }

    private void updateTotal() {
        double total = CartManager.get().getTotal();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotal.setText(nf.format(total));
    }

    @Override
    public void onQuantityChanged() {
        if (adapter != null) {
            adapter.updateData(CartManager.get().getItems());
        }
        updateTotal();
    }

    @Override
    public void onItemRemoved(int position) {
        CartManager.get().remove(position);
        adapter.notifyItemRemoved(position);
        updateTotal();
        Toast.makeText(this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();

        if (CartManager.get().isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateData(CartManager.get().getItems());
            updateTotal();
        }
    }

    // KHÔI PHỤC VÀ SỬA LOGIC NAVIGATION (Đây là phần code bị thiếu/cần sửa)
    @Override
    protected void setupBottomNavigation(int selectedItemId) {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);

            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();

                Class<?> targetActivity = null;

                if (itemId == R.id.nav_home || itemId == R.id.nav_products) {
                    targetActivity = ProductListActivity.class;
                } else if (itemId == R.id.nav_favorites) {
                    targetActivity = WishlistActivity.class;
                } else if (itemId == R.id.nav_cart) {
                    targetActivity = CartActivity.class;
                } else if (itemId == R.id.nav_profile) {
                    // ĐÃ SỬA: Mở ProfileActivity thay vì gọi dialog
                    startActivity(new Intent(this, ProfileActivity.class));
                    return true;
                }

                // Chuyển activity nếu cần và nếu không phải activity hiện tại
                if (targetActivity != null && !this.getClass().equals(targetActivity)) {
                    startActivity(new Intent(this, targetActivity));
                    finish();
                    return true;
                }

                return false;
            });
        }
    }
}