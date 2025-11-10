package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Các activity con sẽ setContentView của riêng chúng
    }

    protected void setupBottomNavigation(int selectedItemId) {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);

            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    if (!(this instanceof ProductListActivity)) {
                        startActivity(new Intent(this, ProductListActivity.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_products) {
                    if (!(this instanceof ProductListActivity)) {
                        startActivity(new Intent(this, ProductListActivity.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_favorites) {
                    Toast.makeText(this, "Tính năng yêu thích đang phát triển", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    if (!(this instanceof CartActivity)) {
                        startActivity(new Intent(this, CartActivity.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Hiển thị dialog profile
                    if (this instanceof ProductListActivity) {
                        ((ProductListActivity) this).showProfileDialog();
                    } else if (this instanceof CartActivity) {
                        ((CartActivity) this).showProfileDialog();
                    } else if (this instanceof CheckoutActivity) {
                        ((CheckoutActivity) this).showProfileDialog();
                    } else if (this instanceof ProductDetailActivity) {
                        ((ProductDetailActivity) this).showProfileDialog();
                    }
                    return true;
                }
                return false;
            });
        }
    }
}