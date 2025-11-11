package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // THÊM
import android.widget.Button; // THÊM
import android.widget.LinearLayout; // THÊM
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WishlistActivity extends BaseActivity implements WishlistAdapter.WishlistListener {

    private RecyclerView rvWishlist;
    private WishlistAdapter adapter;

    // THÊM CÁC BIẾN NÀY
    private LinearLayout emptyStateLayout;
    private Button btnShopNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        initToolbar();
        initViews();
        setupBottomNavigation(R.id.nav_favorites);

        checkEmptyState(); // THÊM HÀM GỌI NÀY
    }

    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách yêu thích");

        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        rvWishlist = findViewById(R.id.rvWishlist);

        // THÊM 2 DÒNG NÀY
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnShopNow = findViewById(R.id.btnShopNow);

        List<WishlistItem> data = WishlistManager.get().getItems();

        Log.d("WISHLIST_DEBUG", "Wishlist items count: " + data.size());

        adapter = new WishlistAdapter(data, this);
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));
        rvWishlist.setAdapter(adapter);

        // XÓA 2 DÒNG TOAST CŨ

        // THÊM CLICK LISTENER CHO NÚT "Shop Now"
        btnShopNow.setOnClickListener(v -> {
            startActivity(new Intent(WishlistActivity.this, ProductListActivity.class));
            finish();
        });
    }

    // THÊM HÀM MỚI NÀY
    private void checkEmptyState() {
        if (adapter.getItemCount() == 0) {
            rvWishlist.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            rvWishlist.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(Product product) {
        // ... code cũ của bạn
        android.util.Log.e("WISHLIST_ACTIVITY", "onItemClicked called for: " + product.name);
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);

    }

    @Override
    public void onItemRemoved(int position) {
        Log.d("WISHLIST_DEBUG", "onItemRemoved called for position: " + position);
        WishlistManager.get().remove(position);
        adapter.updateData(WishlistManager.get().getItems());
        Toast.makeText(this, "Đã xóa sản phẩm khỏi yêu thích", Toast.LENGTH_SHORT).show();

        // ---- SỬA CHỖ NÀY ----
        // XÓA DÒNG if (WishlistManager.get()...

        // THAY BẰNG:
        checkEmptyState();
    }

    @Override
    public void onAddToCart(Product product, Integer size) {
        // ... code cũ của bạn
        Log.d("WISHLIST_DEBUG", "onAddToCart called for: " + product.name);
        CartManager.get().add(product, 1, size);
        Toast.makeText(this, "Đã thêm " + product.name + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // Ghi đè method setupBottomNavigation từ BaseActivity để fix lỗi
    // Trong WishlistActivity.java

    // Ghi đè method setupBottomNavigation từ BaseActivity để fix lỗi
    @Override
    protected void setupBottomNavigation(int selectedItemId) {
        // ... code cũ của bạn
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
                    // ĐÃ SỬA: Chuyển sang mở ProfileActivity thay vì gọi dialog
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