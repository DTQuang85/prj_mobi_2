package com.example.app_ban_hang;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {
    private static final WishlistManager INSTANCE = new WishlistManager();
    public static WishlistManager get() { return INSTANCE; }

    private final List<WishlistItem> items = new ArrayList<>();

    public void add(Product p, Integer size) {
        for (WishlistItem it : items) {
            if (it.product.name.equals(p.name) &&
                    ((it.size == null && size == null) || (it.size != null && it.size.equals(size)))) {
                return; // đã có rồi
            }
        }
        items.add(new WishlistItem(p, size));
    }

    public List<WishlistItem> getItems() {
        return new ArrayList<>(items);
    }

    public void remove(int index) {
        if (index >= 0 && index < items.size()) items.remove(index);
    }

    public void clear() { items.clear(); }
}
