package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private ImageView ivUserAvatar;
    private TextView tvUserName, tvUserEmail, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        initToolbar();
        initViews();
        bindUserData();

        // Kích hoạt Bottom Navigation và chọn tab Profile
        setupBottomNavigation(R.id.nav_profile);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // ĐÃ BỎ: getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // ĐÃ BỎ: toolbar.setNavigationOnClickListener(v -> finish());
            getSupportActionBar().setTitle("Tài khoản");
        }
    }

    private void initViews() {
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> signOut());
    }

    private void bindUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (currentUser != null || account != null) {
            // Hiển thị thông tin từ Google
            if (account != null) {
                tvUserName.setText(account.getDisplayName() != null ? account.getDisplayName() : "Không có tên");
                tvUserEmail.setText(account.getEmail() != null ? account.getEmail() : "Không có email");
                if (account.getPhotoUrl() != null) {
                    Glide.with(this).load(account.getPhotoUrl()).circleCrop().into(ivUserAvatar);
                }
            }
            // Hiển thị thông tin từ Firebase (Email/Password)
            else if (currentUser != null) {
                tvUserName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Không có tên");
                tvUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Không có email");
            }
        }
    }

    private void signOut() {
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        mAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(ProfileActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            // Chuyển về màn hình Đăng nhập và xóa các Activity cũ
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}