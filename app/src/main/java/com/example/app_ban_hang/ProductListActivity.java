package com.example.app_ban_hang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductListActivity extends BaseActivity {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private final List<Product> productList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
        setupBottomNavigation(R.id.nav_products);
        loadProductsFromFirestore();
        showUserInfo();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách sản phẩm");
        }

        ivAvatar = findViewById(R.id.ivAvatar);
        rvProducts = findViewById(R.id.rvProducts);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productList);
        rvProducts.setAdapter(adapter);

        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> showProfileDialog());
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

    private void showUserInfo() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            String name = account.getDisplayName();
            Uri photoUrl = account.getPhotoUrl();

            if (name != null) {
                Toast.makeText(this, "Xin chào, " + name, Toast.LENGTH_SHORT).show();
            }

            if (photoUrl != null && ivAvatar != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivAvatar);
            }
        } else if (mAuth.getCurrentUser() != null && ivAvatar != null) {
            Uri photoUrl = mAuth.getCurrentUser().getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivAvatar);
            }
        }
    }

    private void loadProductsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    productList.clear();
                    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                    for (DocumentSnapshot doc : querySnapshot) {
                        // ... (giữ nguyên code load sản phẩm)
                        String name = doc.getString("name");
                        String brand = doc.getString("brand");
                        String category = doc.getString("category");
                        String description = doc.getString("description");
                        String imageUrl = doc.getString("imageUrl");

                        double priceVnd = 0d;
                        Object rawPrice = doc.get("priceVnd");
                        if (rawPrice instanceof Number) {
                            priceVnd = ((Number) rawPrice).doubleValue();
                        } else if (rawPrice instanceof String) {
                            String digits = ((String) rawPrice).replaceAll("[^0-9]", "");
                            if (!digits.isEmpty()) {
                                try {
                                    priceVnd = Double.parseDouble(digits);
                                } catch (NumberFormatException ignore) {}
                            }
                        }

                        String priceText = doc.getString("priceText");
                        if (priceText == null || priceText.trim().isEmpty()) {
                            priceText = priceVnd > 0 ? nf.format(priceVnd) : "Liên hệ";
                        }

                        Product product = new Product(
                                name != null ? name : "Không có tên",
                                priceText,
                                priceVnd,
                                description != null ? description : "Không có mô tả",
                                imageUrl,
                                category != null ? category : "Không phân loại",
                                brand != null ? brand : "Không thương hiệu"
                        );

                        productList.add(product);
                    }

                    adapter.notifyDataSetChanged();

                    if (productList.isEmpty()) {
                        Toast.makeText(this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    protected void signOut() {
        mAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(ProductListActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProductListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null && GoogleSignIn.getLastSignedInAccount(this) == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}