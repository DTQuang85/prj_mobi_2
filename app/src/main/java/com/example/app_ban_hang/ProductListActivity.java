package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private final List<Product> productList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        // === Firebase Auth ===
        mAuth = FirebaseAuth.getInstance();

        // === Google Sign-In (dùng để logout) ===
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this, gso);

        // === Toolbar ===
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách sản phẩm");
        }

        // === RecyclerView ===
        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productList);
        rvProducts.setAdapter(adapter);

        // === Load dữ liệu Firestore ===
        loadProductsFromFirestore();

        // === Nút giỏ hàng ===
        findViewById(R.id.fabCart).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class))
        );

        // === Hiển thị tên + avatar người dùng (Google) ===
        showUserInfo();
    }

    private void showUserInfo() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String name = acct.getDisplayName();
            String photoUrl = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : null;
            Toast.makeText(this, "Xin chào, " + name, Toast.LENGTH_SHORT).show();

            // Nếu layout có ImageView avatar
            ImageView ivAvatar = findViewById(R.id.ivAvatar);
            if (ivAvatar != null && photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
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
                        String name = doc.getString("name");
                        String brand = doc.getString("brand");
                        String category = doc.getString("category");
                        String description = doc.getString("description");
                        Double priceVnd = doc.getDouble("priceVnd");
                        String imageUrl = doc.getString("imageUrl");
                        String priceText = priceVnd != null ? nf.format(priceVnd) : "—";

                        productList.add(new Product(
                                name,
                                priceText,
                                priceVnd == null ? 0 : priceVnd,
                                description,
                                imageUrl,
                                category,
                                brand
                        ));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // === Menu Logout ===
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        googleClient.signOut().addOnCompleteListener(task -> {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
