package com.example.app_ban_hang;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final CartManager INSTANCE = new CartManager();
    public static CartManager get() { return INSTANCE; }

    private final List<CartItem> items = new ArrayList<>();

    public void add(Product p, int qty) {
        // Gộp theo name (demo). Nếu có id trong Firestore thì nên dùng id.
        for (CartItem it : items) {
            if (it.product.name.equals(p.name)) {
                it.quantity += qty;
                return;
            }
        }
        items.add(new CartItem(p, Math.max(1, qty)));
    }

    public void setQuantity(int index, int qty) {
        if (index >= 0 && index < items.size()) {
            if (qty <= 0) {
                items.remove(index);
            } else {
                items.get(index).quantity = qty;
            }
        }
    }

    public void remove(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items); // Trả về bản copy để tránh modify trực tiếp
    }

    public double getTotal() {
        double t = 0;
        for (CartItem it : items) t += it.lineTotal();
        return t;
    }

    public void clear() { items.clear(); }
}