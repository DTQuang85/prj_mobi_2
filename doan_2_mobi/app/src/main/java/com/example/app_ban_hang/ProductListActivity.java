package com.example.app_ban_hang;

import android.os.Bundle;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        // 👉 Gán Toolbar hiển thị tiêu đề và nút Back
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Danh sách sản phẩm");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 👉 Cấu hình RecyclerView
        RecyclerView rv = findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ProductAdapter(mockProducts()));
    }

    // ✅ Dữ liệu mẫu
    private List<Product> mockProducts() {
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        List<Product> list = new ArrayList<>();
        list.add(new Product("Sản phẩm 1", vn.format(129000),
                "Giày Converse đen.", R.drawable.pd1));
        list.add(new Product("Sản phẩm 2", vn.format(239000),
                "Mô tả ngắn gọn cho sản phẩm 2.", R.drawable.pd2));
        list.add(new Product("Sản phẩm 3", vn.format(99000),
                "Mô tả ngắn gọn cho sản phẩm 3.", R.drawable.pd3));
        list.add(new Product("Sản phẩm 4", vn.format(179000),
                "Mô tả ngắn gọn cho sản phẩm 4.", R.drawable.pd4));
        return list;
    }
}
