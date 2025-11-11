package com.example.app_ban_hang;

import android.content.Intent; // Thêm import
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    // Biến cho Banner
    private ViewPager2 viewPagerBanner;
    private BannerAdapter bannerAdapter;
    private List<Integer> bannerImages = new ArrayList<>();
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    // Xóa: Biến cho Danh mục

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupBottomNavigation(R.id.nav_home);
        setupBanner();
        // Xóa: setupCategoryChips();
    }

    private void initViews() {
        // Toolbar
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Tìm ViewPager2 Banner
        viewPagerBanner = findViewById(R.id.viewPagerBanner);

        // Xóa: Tìm ChipGroup Danh mục
    }

    /**
     * Cài đặt cho Banner quảng cáo (ViewPager2)
     */
    private void setupBanner() {
        // CHUẨN BỊ DATA ẢNH (Dùng ảnh từ res/drawable)
        bannerImages.add(R.drawable.banner_1); // (Thay 'banner_1' bằng tên file của bạn)
        bannerImages.add(R.drawable.banner_2); // (Thay 'banner_2' bằng tên file của bạn)
        bannerImages.add(R.drawable.banner_3); // (Thay 'banner_3' bằng tên file của bạn)

        // Khởi tạo Adapter
        bannerAdapter = new BannerAdapter(bannerImages);

        // Gán Adapter cho ViewPager2
        viewPagerBanner.setAdapter(bannerAdapter);

        // Cài đặt cho ViewPager2
        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);
        viewPagerBanner.setOffscreenPageLimit(3);

        viewPagerBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        // (Bắt đầu tự động trượt)
        setupAutoSlider();
    }

    /**
     * Cài đặt tự động trượt cho Banner
     */
    private void setupAutoSlider() {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPagerBanner.getCurrentItem();
                int totalItems = bannerAdapter.getItemCount();
                if (totalItems > 0) {
                    viewPagerBanner.setCurrentItem((currentItem + 1) % totalItems, true);
                }
                sliderHandler.postDelayed(this, 3000);
            }
        };
    }

    // KHÔI PHỤC VÀ SỬA LOGIC NAVIGATION (CẦN THIẾT CHO VIỆC CHUYỂN PROFILE)
    @Override
    protected void setupBottomNavigation(int selectedItemId) {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);

            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();

                Class<?> targetActivity = null;

                if (itemId == R.id.nav_home) { // Sửa logic cho nav_home
                    targetActivity = HomeActivity.class;
                } else if (itemId == R.id.nav_products) {
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


    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}