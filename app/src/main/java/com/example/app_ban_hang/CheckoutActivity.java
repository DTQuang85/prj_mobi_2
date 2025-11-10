package com.example.app_ban_hang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.bumptech.glide.Glide;

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

public class CheckoutActivity extends AppCompatActivity {

    private static final String BASE = "https://<REGION>-<PROJECT_ID>.cloudfunctions.net/api";

    private ImageView ivQr;        // nếu bạn vẫn hiển thị ảnh minh hoạ
    private TextView tvTotal, tvStatus;
    private Button btnPayVnpay;

    private final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private String currentOrderId;
    private int pollCount = 0;
    private static final int POLL_LIMIT = 30; // 60s (2s * 30)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        ivQr = findViewById(R.id.ivQr);
        tvTotal = findViewById(R.id.tvTotal);
        tvStatus = findViewById(R.id.tvStatus);
        btnPayVnpay = findViewById(R.id.btnPayVnpay);

        double total = CartManager.get().getTotal();
        NumberFormat vn = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));
        tvTotal.setText(vn.format(total));

        // Tuỳ chọn: ảnh minh hoạ
        Glide.with(this).load("https://i.imgur.com/ZmQ2pQG.png")
                .placeholder(R.drawable.logo)
                .into(ivQr);

        btnPayVnpay.setOnClickListener(v -> {
            btnPayVnpay.setEnabled(false);
            tvStatus.setText("Đang tạo giao dịch...");
            createPayment((int) total);
        });
    }

    private void createPayment(int amountVnd) {
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            this.currentOrderId = orderId;

            JSONObject payload = new JSONObject();
            payload.put("amount", amountVnd);
            payload.put("orderId", orderId);

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.parse("application/json")
            );

            Request req = new Request.Builder()
                    .url(BASE + "/createPayment")
                    .post(body)
                    .build();

            http.newCall(req).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        tvStatus.setText("Lỗi tạo giao dịch: " + e.getMessage());
                        btnPayVnpay.setEnabled(true);
                    });
                }

                @Override public void onResponse(Call call, Response resp) throws IOException {
                    if (!resp.isSuccessful()) {
                        runOnUiThread(() -> {
                            tvStatus.setText("Tạo giao dịch thất bại (" + resp.code() + ")");
                            btnPayVnpay.setEnabled(true);
                        });
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(resp.body().string());
                        String paymentUrl = json.getString("paymentUrl");
                        runOnUiThread(() -> {
                            tvStatus.setText("Mở VNPAY...");
                            openVnpay(paymentUrl);
                            startPolling();
                        });
                    } catch (Exception ex) {
                        runOnUiThread(() -> {
                            tvStatus.setText("Lỗi đọc phản hồi server");
                            btnPayVnpay.setEnabled(true);
                        });
                    }
                }
            });
        } catch (Exception e) {
            tvStatus.setText("Lỗi payload: " + e.getMessage());
            btnPayVnpay.setEnabled(true);
        }
    }

    private void openVnpay(String url) {
        try {
            CustomTabsIntent tabs = new CustomTabsIntent.Builder().build();
            tabs.launchUrl(this, Uri.parse(url));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    private void startPolling() {
        pollCount = 0;
        tvStatus.setText("Đang chờ kết quả thanh toán...");
        handler.postDelayed(this::pollOnce, 2000);
    }

    private void pollOnce() {
        if (currentOrderId == null) {
            tvStatus.setText("Thiếu orderId để kiểm tra.");
            btnPayVnpay.setEnabled(true);
            return;
        }
        Request req = new Request.Builder()
                .url(BASE + "/orderStatus?orderId=" + currentOrderId)
                .get().build();

        http.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) { scheduleNextPoll(); }

            @Override public void onResponse(Call call, Response resp) throws IOException {
                if (!resp.isSuccessful()) { scheduleNextPoll(); return; }
                try {
                    JSONObject json = new JSONObject(resp.body().string());
                    String status = json.optString("status", "pending");
                    if ("paid".equalsIgnoreCase(status)) runOnUiThread(() -> onPaid());
                    else if ("failed".equalsIgnoreCase(status)) runOnUiThread(() -> onFailed());
                    else scheduleNextPoll();
                } catch (Exception ex) { scheduleNextPoll(); }
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

    private void onPaid() {
        tvStatus.setText("Thanh toán thành công!");
        Toast.makeText(this, "Đã thanh toán", Toast.LENGTH_SHORT).show();
        CartManager.get().clear();
        btnPayVnpay.setEnabled(true);
        // TODO: chuyển về màn hình đơn hàng / cảm ơn
    }

    private void onFailed() {
        tvStatus.setText("Thanh toán thất bại hoặc bị huỷ.");
        Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
        btnPayVnpay.setEnabled(true);
    }
}
