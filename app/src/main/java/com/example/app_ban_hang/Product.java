package com.example.app_ban_hang;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    // ------------------------------------------
    // 1. TRƯỜNG DỮ LIỆU (Fields)
    // ------------------------------------------
    public String id; // <--- RẤT QUAN TRỌNG: Để định danh trong Firestore
    public String name;
    public String priceText; // Dạng chuỗi (VD: "2.600.000 ₫")
    public double priceVnd;  // Dạng số (VD: 2600000)
    public String description;
    public String imageUrl;
    public String category;
    public String brand;
    public List<Integer> sizes;

    // ------------------------------------------
    // 2. CONSTRUCTORS
    // ------------------------------------------

    // Constructor rỗng (BẮT BUỘC cho Firestore)
    public Product() {}

    // Constructor cho việc lấy dữ liệu đầy đủ từ Firestore
    public Product(String id, String name, String priceText, double priceVnd, String description, String imageUrl, String category, String brand, List<Integer> sizes) {
        this.id = id;
        this.name = name;
        this.priceText = priceText;
        this.priceVnd = priceVnd;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.brand = brand;
        this.sizes = sizes;
    }

    // Constructor đơn giản (QUAN TRỌNG cho CartManager.addOrUpdate)
    // Dùng để tạo đối tượng Product tạm thời khi cập nhật giỏ hàng
    public Product(String id, String name, double priceVnd) {
        this.id = id;
        this.name = name;
        this.priceVnd = priceVnd;
    }

    // ------------------------------------------
    // 3. GETTERS/SETTERS (Nếu cần, Firestore dùng reflection/public fields)
    // ------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}