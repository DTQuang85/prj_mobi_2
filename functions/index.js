const functions = require("firebase-functions");
const admin = require("firebase-admin");
const express = require("express");
const cors = require("cors");
const crypto = require("crypto");
const qs = require("qs");

admin.initializeApp();
const app = express();
app.use(cors({ origin: true }));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

// Cấu hình VNPay sandbox (bạn đã đăng ký sẵn)
const vnp_TmnCode = "IYT8EBZ6";
const vnp_HashSecret = "84JWKFVZRGPCRGGE6YMZJ8SBP793I5K5";
const vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
const vnp_ReturnUrl = "https://us-central1-sneakersstore-45b4b.cloudfunctions.net/api/vnpayReturn";

function sortObject(obj) {
  const sorted = {};
  const keys = Object.keys(obj).sort();
  for (const key of keys) sorted[key] = obj[key];
  return sorted;
}

// ✅ Tạo URL thanh toán
app.post("/createPayment", (req, res) => {
  try {
    const date = new Date();
    const orderId = date.getTime().toString();
    const amount = req.body.amount || 10000;

    const ipAddr =
      req.headers["x-forwarded-for"] ||
      req.connection.remoteAddress ||
      req.socket.remoteAddress;

    const vnp_Params = {
      vnp_Version: "2.1.0",
      vnp_Command: "pay",
      vnp_TmnCode,
      vnp_Locale: "vn",
      vnp_CurrCode: "VND",
      vnp_TxnRef: orderId,
      vnp_OrderInfo: `Thanh toán đơn hàng #${orderId}`,
      vnp_OrderType: "other",
      vnp_Amount: amount * 100,
      vnp_ReturnUrl,
      vnp_IpAddr: ipAddr,
      vnp_CreateDate: date
        .toISOString()
        .replace(/[-T:.Z]/g, "")
        .slice(0, 14),
    };

    const sorted = sortObject(vnp_Params);
    const signData = qs.stringify(sorted, { encode: false });
    const hmac = crypto.createHmac("sha512", vnp_HashSecret);
    const signed = hmac.update(Buffer.from(signData, "utf-8")).digest("hex");
    sorted["vnp_SecureHash"] = signed;

    const paymentUrl = vnp_Url + "?" + qs.stringify(sorted, { encode: false });
    return res.json({ paymentUrl });
  } catch (err) {
    console.error("Error creating payment:", err);
    res.status(500).json({ error: "Internal Server Error" });
  }
});

// ✅ Nhận callback sau khi thanh toán
app.get("/vnpayReturn", (req, res) => {
  const vnp_ResponseCode = req.query.vnp_ResponseCode;
  if (vnp_ResponseCode === "00") {
    res.status(200).send("✅ Thanh toán thành công!");
  } else {
    res.status(200).send("❌ Thanh toán thất bại hoặc bị huỷ.");
  }
});

exports.api = functions.https.onRequest(app);
