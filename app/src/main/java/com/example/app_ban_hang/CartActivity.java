package com.example.app_ban_hang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvTotal;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        initViews();
        setupRecyclerView();
        updateTotal();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            if (CartManager.get().getItems().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new android.content.Intent(this, CheckoutActivity.class));
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
        tvTotal.setText("Tổng thanh toán: " + nf.format(total));
    }

    @Override
    public void onQuantityChanged() {
        // Khi số lượng thay đổi, cập nhật tổng tiền
        updateTotal();

        // Refresh toàn bộ adapter để đảm bảo hiển thị đúng
        if (adapter != null) {
            adapter.updateData(CartManager.get().getItems());
        }
    }

    @Override
    public void onItemRemoved(int position) {
        // Xóa item khỏi giỏ hàng
        CartManager.get().remove(position);

        // Thông báo cho adapter
        adapter.notifyItemRemoved(position);
        adapter.updateData(CartManager.get().getItems());

        // Cập nhật tổng tiền
        updateTotal();

        // Thông báo cho user
        Toast.makeText(this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();

        // Nếu giỏ hàng trống
        if (CartManager.get().getItems().isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotal();
        if (adapter != null) {
            adapter.updateData(CartManager.get().getItems());
        }
    }
}