package com.example.app_ban_hang;

public class Product {
    public final String name;
    public final String priceText;
    public final String description;
    public final int imageRes;

    public Product(String name, String priceText, String description, int imageRes) {
        this.name = name;
        this.priceText = priceText;
        this.description = description;
        this.imageRes = imageRes;
    }
}
