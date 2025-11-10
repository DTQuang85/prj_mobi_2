package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WishlistActivity extends BaseActivity implements WishlistAdapter.WishlistListener {

    private RecyclerView rvWishlist;
    private WishlistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        initToolbar();
        initViews();
        setupBottomNavigation(R.id.nav_favorites);
    }

    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách yêu thích");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        rvWishlist = findViewById(R.id.rvWishlist);
        List<WishlistItem> data = WishlistManager.get().getItems();

        Log.d("WISHLIST_DEBUG", "Wishlist items count: " + data.size());

        adapter = new WishlistAdapter(data, this);
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));
        rvWishlist.setAdapter(adapter);

        if (data.isEmpty()) {
            Toast.makeText(this, "Danh sách yêu thích trống", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Có " + data.size() + " sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClicked(Product product) {
        // IN LOG ĐỂ DEBUG
        android.util.Log.e("WISHLIST_ACTIVITY", "onItemClicked called for: " + product.name);

        // CHUYỂN ĐẾN PRODUCT DETAIL
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

        if (WishlistManager.get().getItems().isEmpty()) {
            onBackPressed();
        }
    }

    @Override
    public void onAddToCart(Product product, Integer size) {
        Log.d("WISHLIST_DEBUG", "onAddToCart called for: " + product.name);
        CartManager.get().add(product, 1, size);
        Toast.makeText(this, "Đã thêm " + product.name + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // Ghi đè method setupBottomNavigation từ BaseActivity để fix lỗi
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
                    // Hiển thị profile dialog thay vì chuyển activity
                    showProfileDialog();
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