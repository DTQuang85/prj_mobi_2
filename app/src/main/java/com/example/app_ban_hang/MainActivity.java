package com.example.app_ban_hang;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private CheckBox cbRemember;
    private TextView tvForgot, tvSignup;

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
        bindEvents();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.cbRemember);
        tvForgot   = findViewById(R.id.tvForgotPassword);
        tvSignup   = findViewById(R.id.tvSignup);
    }

    private void bindEvents() {
        // Nhấn Done trên bàn phím ở ô mật khẩu cũng trigger đăng nhập
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN;
            if (actionId == EditorInfo.IME_ACTION_DONE || isEnter) {
                attemptLogin();
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
    }

    private void attemptLogin() {
        // Reset lỗi
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

        // Demo: kiểm tra giả lập (bạn thay bằng call API thật)
        // ... trong attemptLogin(), nhánh đăng nhập thành công:
        if (username.equals("admin") && password.equals("123456")) {
            String msg = cbRemember.isChecked()
                    ? "Đăng nhập thành công (đã ghi nhớ)!"
                    : "Đăng nhập thành công!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, ProductListActivity.class));
            // finish(); // nếu muốn không quay lại màn login
        } else {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }

    }
}
