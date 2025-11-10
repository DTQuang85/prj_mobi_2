package com.example.app_ban_hang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity implements CartAdapter.CartListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private TextView tvTotal;
    private Button btnNext;
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
        loadUserAvatar();
    }

    private void initViews() {
        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Giỏ hàng");
        }

        // Avatar
        ivAvatar = findViewById(R.id.ivAvatar);

        rvCart = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            if (CartManager.get().getItems().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });

        // Xử lý click avatar
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> showProfileDialog());
        }
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
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnCompleteListener(this, task -> {
                    Toast.makeText(CartActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    @Override
    public void onQuantityChanged() {
        updateTotal();
        if (adapter != null) {
            adapter.updateData(CartManager.get().getItems());
        }
    }

    @Override
    public void onItemRemoved(int position) {
        CartManager.get().remove(position);
        adapter.notifyItemRemoved(position);
        adapter.updateData(CartManager.get().getItems());
        updateTotal();
        Toast.makeText(this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();

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