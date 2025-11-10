package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;
    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Gọi trong mỗi Activity con sau setContentView():
     *   setupBottomNavigation(R.id.nav_home)  // ở HomeActivity
     *   setupBottomNavigation(R.id.nav_products) // ở ProductListActivity
     *   ...
     */
    protected void setupBottomNavigation(@IdRes int selectedItemId) {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView == null) return;

        // set selected để highlight đúng tab hiện tại
        bottomNavigationView.setSelectedItemId(selectedItemId);

        // Listener chọn tab (API mới)
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);

        // Listener bấm lại tab hiện tại (reselect)
        bottomNavigationView.setOnItemReselectedListener(item -> {
            // Hook cho Activity con (vd: Home scroll to top)
            onBottomTabReselected(item.getItemId());
        });
    }

    private final NavigationBarView.OnItemSelectedListener onItemSelectedListener =
            item -> {
                final int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    navigateIfNeeded(HomeActivity.class, itemId);
                    return true;

                } else if (itemId == R.id.nav_products) {
                    navigateIfNeeded(ProductListActivity.class, itemId);
                    return true;

                } else if (itemId == R.id.nav_favorites) {
                    navigateIfNeeded(WishlistActivity.class, itemId);
                    return true;

                } else if (itemId == R.id.nav_cart) {
                    navigateIfNeeded(CartActivity.class, itemId);
                    return true;

                } else if (itemId == R.id.nav_profile) {
                    showProfileDialog();
                    return true;
                }

                return false;
            };

    /**
     * Điều hướng chỉ khi Activity mục tiêu khác Activity hiện tại.
     * Không tạo animation để cảm giác như switch tab.
     */
    private void navigateIfNeeded(Class<?> target, @IdRes int itemId) {
        if (!target.isInstance(this)) {
            Intent intent = new Intent(this, target);
            // Tuỳ chiến lược back stack: có thể dùng REORDER_TO_FRONT để giữ state tab cũ
            // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish(); // tránh chồng nhiều activity khi chuyển tab
        } else {
            // Nếu là cùng activity, gọi reselect hook (tuỳ bạn xử lý)
            onBottomTabReselected(itemId);
        }
    }

    /**
     * Hook khi bấm lại tab hiện tại. Activity con có thể override.
     * Ví dụ trong HomeActivity: scroll RecyclerView về đầu hoặc refresh.
     */
    protected void onBottomTabReselected(@IdRes int itemId) {
        // Mặc định không làm gì. Override ở Activity con nếu cần.
    }

    // ==== Profile Dialog giữ nguyên như bạn có ====
    protected void showProfileDialog() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Thông tin người dùng");

            String userInfo = "Email: " + currentUser.getEmail() +
                    "\nUID: " + currentUser.getUid();

            builder.setMessage(userInfo);
            builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
                mAuth.signOut();
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
            builder.setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        } else {
            Toast.makeText(this, "Không có thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // Cleanup listener để tránh leak
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(null);
            bottomNavigationView.setOnItemReselectedListener(null);
        }
        super.onDestroy();
    }
}
