package com.example.app_ban_hang;

import java.io.Serializable;

public class Product implements Serializable {
    public String name;
    public String priceText;     // "2.890.000 ₫"
    public double priceVnd;      // 2890000 (để tính tiền)
    public String description;
    public String imageUrl;
    public String category;
    public String brand;

    public Product() {}

    public Product(String name, String priceText, double priceVnd, String description,
                   String imageUrl, String category, String brand) {
        this.name = name;
        this.priceText = priceText;
        this.priceVnd = priceVnd;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.brand = brand;
    }
}
