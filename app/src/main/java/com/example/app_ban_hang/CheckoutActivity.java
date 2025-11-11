package com.example.app_ban_hang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.browser.customtabs.CustomTabsIntent;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckoutActivity extends BaseActivity {

    private static final String BASE_URL = "https://your-region-your-project.cloudfunctions.net/api"; // THAY THẾ URL THẬT

    private ImageView ivQr, ivAvatar;
    private TextView tvTotal, tvStatus;
    private Button btnPayVnpay, btnDone;
    private FirebaseAuth mAuth;

    private final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private String currentOrderId;
    private int pollCount = 0;
    private static final int POLL_LIMIT = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupBottomNavigation(R.id.nav_cart);
        setupPaymentInfo();
    }

    private void initViews() {
        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Thanh toán");
        }

        // ivAvatar (Giữ lại nếu có)
        ivQr = findViewById(R.id.ivQr);
        tvTotal = findViewById(R.id.tvTotal);
        tvStatus = findViewById(R.id.tvStatus);
        btnPayVnpay = findViewById(R.id.btnPayVnpay);
        btnDone = findViewById(R.id.btnDone);

        // Load user avatar (giữ lại nếu có)
        loadUserAvatar();

        btnPayVnpay.setOnClickListener(v -> {
            // Lấy tổng tiền đồng bộ trước khi tạo thanh toán
            double currentTotal = CartManager.get().getTotal();
            if (currentTotal > 0) {
                btnPayVnpay.setEnabled(false);
                tvStatus.setText("Đang tạo giao dịch...");
                createPayment((int) currentTotal);
            } else {
                tvStatus.setText("Giỏ hàng trống!");
                btnPayVnpay.setEnabled(false);
            }
        });

        btnDone.setOnClickListener(v -> {
            // Quay về trang chủ hoặc trang sản phẩm
            Intent intent = new Intent(CheckoutActivity.this, ProductListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupPaymentInfo() {
        // Dùng hàm đồng bộ getTotal()
        double total = CartManager.get().getTotal();
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotal.setText(vn.format(total));

        // Kiểm tra nếu giỏ hàng trống (Dùng hàm đồng bộ isEmpty())
        if (CartManager.get().isEmpty()) {
            btnPayVnpay.setEnabled(false);
            tvStatus.setText("Giỏ hàng trống");
        } else {
            btnPayVnpay.setEnabled(true);
            tvStatus.setText("Vui lòng thanh toán...");
        }

        // Hiển thị QR code mẫu hoặc ảnh minh họa
        Glide.with(this)
                .load(R.drawable.logo) // Hoặc URL QR code thật
                .placeholder(R.drawable.logo)
                .into(ivQr);
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

    // PHƯƠNG THỨC NÀY ĐÃ ĐƯỢC KHẮC PHỤC LỖI ClassCastException VÀ KHÔI PHỤC LOGOUT
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
        TextView btnLogout = dialogView.findViewById(R.id.btnProfileLogout); // ĐÃ SỬA THÀNH TEXTVIEW

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

        // KHÔI PHỤC CHỨC NĂNG LOGOUT
        btnLogout.setOnClickListener(v -> signOut());

        builder.setNegativeButton("Đóng", null);
        builder.show();
    }

    private void signOut() {
        mAuth.signOut();
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                .signOut().addOnCompleteListener(this, task -> {
                    Toast.makeText(CheckoutActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void createPayment(int amountVnd) {
        try {
            String orderId = "ORDER_" + System.currentTimeMillis();
            this.currentOrderId = orderId;

            JSONObject payload = new JSONObject();
            payload.put("amount", amountVnd);
            payload.put("orderId", orderId);
            payload.put("orderInfo", "Thanh toán đơn hàng " + orderId);

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request req = new Request.Builder()
                    .url(BASE_URL + "/createPayment")
                    .post(body)
                    .build();

            http.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        tvStatus.setText("Lỗi kết nối: " + e.getMessage());
                        btnPayVnpay.setEnabled(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response resp) throws IOException {
                    runOnUiThread(() -> {
                        if (!resp.isSuccessful()) {
                            tvStatus.setText("Lỗi server: " + resp.code());
                            btnPayVnpay.setEnabled(true);
                            return;
                        }

                        try {
                            String responseBody = resp.body().string();
                            JSONObject json = new JSONObject(responseBody);

                            if (json.has("paymentUrl")) {
                                String paymentUrl = json.getString("paymentUrl");
                                tvStatus.setText("Đang mở VNPAY...");
                                openVnpay(paymentUrl);
                                startPolling();
                            } else {
                                tvStatus.setText("Lỗi: Không nhận được paymentUrl");
                                btnPayVnpay.setEnabled(true);
                            }
                        } catch (Exception ex) {
                            tvStatus.setText("Lỗi xử lý phản hồi");
                            btnPayVnpay.setEnabled(true);
                        }
                    });
                }
            });
        } catch (Exception e) {
            tvStatus.setText("Lỗi tạo yêu cầu: " + e.getMessage());
            btnPayVnpay.setEnabled(true);
        }
    }

    private void openVnpay(String url) {
        try {
            CustomTabsIntent tabs = new CustomTabsIntent.Builder().build();
            tabs.launchUrl(this, Uri.parse(url));
        } catch (Exception e) {
            // Fallback: mở trình duyệt thông thường
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    private void startPolling() {
        pollCount = 0;
        tvStatus.setText("Đang kiểm tra trạng thái thanh toán...");
        handler.postDelayed(this::pollOnce, 2000);
    }

    private void pollOnce() {
        if (currentOrderId == null) {
            tvStatus.setText("Lỗi: Không có orderId");
            btnPayVnpay.setEnabled(true);
            return;
        }

        Request req = new Request.Builder()
                .url(BASE_URL + "/orderStatus?orderId=" + currentOrderId)
                .get()
                .build();

        http.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                scheduleNextPoll();
            }

            @Override
            public void onResponse(Call call, Response resp) throws IOException {
                if (!resp.isSuccessful()) {
                    scheduleNextPoll();
                    return;
                }

                try {
                    String responseBody = resp.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    String status = json.optString("status", "pending");

                    runOnUiThread(() -> {
                        if ("paid".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status)) {
                            onPaymentSuccess();
                        } else if ("failed".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
                            onPaymentFailed();
                        } else {
                            scheduleNextPoll();
                        }
                    });
                } catch (Exception ex) {
                    scheduleNextPoll();
                }
            }
        });
    }

    private void scheduleNextPoll() {
        pollCount++;
        if (pollCount >= POLL_LIMIT) {
            runOnUiThread(() -> {
                tvStatus.setText("Hết thời gian chờ. Vui lòng kiểm tra lại giao dịch.");
                btnPayVnpay.setEnabled(true);
            });
        } else {
            handler.postDelayed(this::pollOnce, 2000);
        }
    }

    private void onPaymentSuccess() {
        tvStatus.setText("✅ Thanh toán thành công!");
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

        // Xóa giỏ hàng (Dùng hàm đồng bộ clear() cục bộ)
        CartManager.get().clear();

        // Vô hiệu hóa nút thanh toán, hiển thị nút hoàn tất
        btnPayVnpay.setEnabled(false);
        btnDone.setVisibility(View.VISIBLE);

        // Có thể chuyển đến màn hình cảm ơn hoặc đơn hàng
        // startActivity(new Intent(this, OrderSuccessActivity.class));
    }

    private void onPaymentFailed() {
        tvStatus.setText("❌ Thanh toán thất bại hoặc bị hủy");
        Toast.makeText(this, "Thanh toán không thành công", Toast.LENGTH_SHORT).show();
        btnPayVnpay.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dọn dẹp handler để tránh memory leak
        handler.removeCallbacksAndMessages(null);
    }
}