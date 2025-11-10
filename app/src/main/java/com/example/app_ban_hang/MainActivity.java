package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.android.gms.common.SignInButton;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvForgot, tvSignup;
    private SignInButton btnGoogle;

    // Firebase
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;

    // Activity Result API cho Google Sign-In
    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() == null) {
                    Toast.makeText(this, "Đăng nhập Google bị huỷ", Toast.LENGTH_SHORT).show();
                    return;
                }
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        firebaseAuthWithGoogle(account.getIdToken());
                    } else {
                        Toast.makeText(this, "Không lấy được tài khoản Google", Toast.LENGTH_SHORT).show();
                    }
                } catch (ApiException e) {
                    Toast.makeText(this, "Google Sign-In lỗi: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initAuth();    // <— thêm dòng này
        bindEvents();
        autoSkipIfLoggedIn(); // <— tự vào app nếu đã đăng nhập trước đó
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvForgot   = findViewById(R.id.tvForgotPassword);
        tvSignup   = findViewById(R.id.tvSignup);
        btnGoogle  = findViewById(R.id.btnGoogle);
    }

    private void initAuth() {
        mAuth = FirebaseAuth.getInstance();

        // default_web_client_id có trong strings.xml do google-services.json sinh ra
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(this, gso);
    }

    private void bindEvents() {
        // Nhấn Done trên bàn phím ở ô mật khẩu cũng trigger đăng nhập (demo cũ)
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN;
            if (actionId == EditorInfo.IME_ACTION_DONE || isEnter) {
                attemptLogin(); // (tuỳ bạn giữ hay bỏ luồng user/pass)
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng quên mật khẩu (demo)", Toast.LENGTH_SHORT).show()
        );

        tvSignup.setOnClickListener(v ->
                Toast.makeText(this, "Đi tới màn hình đăng ký (demo)", Toast.LENGTH_SHORT).show()
        );

        // ==== Google Sign-In ====
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleClient.getSignInIntent();
            googleLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(@Nullable String idToken) {
        if (idToken == null) {
            Toast.makeText(this, "Thiếu ID Token", Toast.LENGTH_SHORT).show();
            return;
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        goToProductList();
                    } else {
                        Toast.makeText(this, "Firebase Auth lỗi: " +
                                (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void autoSkipIfLoggedIn() {
        if (mAuth.getCurrentUser() != null) {
            goToProductList();
        }
    }

    private void goToProductList() {
        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ProductListActivity.class));
        finish();
    }

    // === Code demo cũ: user/pass (tuỳ chọn giữ hoặc bỏ) ===
    private void attemptLogin() {
        etUsername.setError(null);
        etPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            cancel = true;
        } else if (password.length() < 6) {
            etPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            cancel = true;
        }

        if (cancel) return;

        // Demo cũ: bạn có thể bỏ phần này khi dùng Google/Firebase Auth chuẩn
        if (username.equals("admin") && password.equals("123456")) {
            goToProductList();
        } else {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}
