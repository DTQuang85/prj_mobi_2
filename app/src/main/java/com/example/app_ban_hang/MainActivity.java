package com.example.app_ban_hang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextView btnLogin;
    private SignInButton btnGoogle;
    private TextView tvForgotPassword, tvSignup;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupGoogleSignIn();
        checkCurrentUser();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignup = findViewById(R.id.tvSignup);

        // Đăng nhập bằng email/password
        btnLogin.setOnClickListener(v -> loginWithEmail());

        // Đăng nhập bằng Google
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        // Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
        });

        // Đăng ký tài khoản
        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class)); // <--- THAY ĐỔI            finish();
        }
    }

    private void loginWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showToast("Vui lòng nhập email", true);
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ", true);
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Vui lòng nhập mật khẩu", true);
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự", true);
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");

                    if (task.isSuccessful()) {
                        showToast("🎉 Đăng nhập thành công!", false);
                        startActivity(new Intent(MainActivity.this, HomeActivity.class)); // <--- THAY ĐỔI                        finish();
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        showToast(errorMessage, true);
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                showToast("❌ Đăng nhập Google thất bại", true);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showToast("🎉 Đăng nhập Google thành công!", false);
                        startActivity(new Intent(MainActivity.this, HomeActivity.class)); // <--- THAY ĐỔI                        finish();
                    } else {
                        showToast("❌ Đăng nhập Google thất bại", true);
                    }
                });
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null) return "❌ Đã xảy ra lỗi";

        String errorMsg = exception.getMessage();
        if (errorMsg == null) return "❌ Đã xảy ra lỗi";

        if (errorMsg.contains("user-not-found")) {
            return "❌ Không tìm thấy tài khoản với email này";
        } else if (errorMsg.contains("wrong-password")) {
            return "❌ Mật khẩu không đúng";
        } else if (errorMsg.contains("invalid-email")) {
            return "❌ Email không hợp lệ";
        } else if (errorMsg.contains("network error")) {
            return "❌ Lỗi kết nối mạng";
        } else if (errorMsg.contains("too many requests")) {
            return "❌ Quá nhiều lần thử. Vui lòng thử lại sau";
        } else {
            return "❌ Đăng nhập thất bại";
        }
    }

    private void showToast(String message, boolean isError) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}