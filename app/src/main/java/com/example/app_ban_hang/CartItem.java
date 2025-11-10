package com.example.app_ban_hang;

public class CartItem {
    public final Product product;
    public int quantity;

    public CartItem(Product p, int q) {
        this.product = p;
        this.quantity = q;
    }

    public double lineTotal() {
        return product.priceVnd * quantity;
    }
}
