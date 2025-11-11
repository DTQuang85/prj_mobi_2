package com.example.app_ban_hang;

// Lớp này dùng để ánh xạ dữ liệu từ Firestore Document
public class CartItemFirestore {

    // Các trường dữ liệu (phải khớp với tên trên Firestore)
    public String productId;
    public int quantity;
    public Integer size;
    public String productName;
    public double priceVnd;

    // Trường này để lưu Document ID (dùng khi xóa hoặc cập nhật)
    public String cartItemId;

    // Constructor rỗng (BẮT BUỘC cho Firestore)
    public CartItemFirestore() {}

    // Constructor đầy đủ (Tùy chọn, nhưng hữu ích)
    public CartItemFirestore(String productId, int quantity, Integer size, String productName, double priceVnd) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.productName = productName;
        this.priceVnd = priceVnd;
    }

    // Các phương thức tính toán (Không cần lưu lên Firestore)
    public double lineTotal() {
        return priceVnd * quantity;
    }
}