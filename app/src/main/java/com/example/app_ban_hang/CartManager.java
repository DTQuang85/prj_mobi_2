package com.example.app_ban_hang;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CartManager {
    private static final CartManager INSTANCE = new CartManager();
    public static CartManager get() { return INSTANCE; }

    // Dữ liệu giỏ hàng được lưu trữ cục bộ
    private final List<CartItem> items = new ArrayList<>();

    private CartManager() {}

    // Thêm sản phẩm với size
    public void add(Product product, int quantity, @Nullable Integer size) {
        if (product == null || quantity <= 0) return;

        // Tìm item trùng (cùng product và cùng size)
        for (CartItem item : items) {
            // Giả định CartItem có phương thức isSameProduct()
            if (item.isSameProduct(new CartItem(product, 1, size))) {
                item.quantity += quantity;
                return;
            }
        }

        // Nếu không tìm thấy item trùng, thêm mới
        items.add(new CartItem(product, quantity, size));
    }

    // Cập nhật số lượng theo vị trí
    public void setQuantity(int index, int quantity) {
        if (index >= 0 && index < items.size()) {
            if (quantity <= 0) {
                items.remove(index);
            } else {
                items.get(index).quantity = quantity;
            }
        }
    }

    // Lấy danh sách items (read-only)
    public List<CartItem> getItems() {
        // Trả về bản sao để tránh thay đổi trực tiếp
        return new ArrayList<>(items);
    }

    // Xóa item theo vị trí
    public void remove(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    // Tính tổng tiền (CỤC BỘ)
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            // Giả định CartItem có phương thức lineTotal()
            total += item.lineTotal();
        }
        return total;
    }

    // Kiểm tra giỏ hàng trống (CỤC BỘ)
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Xóa toàn bộ giỏ hàng (CỤC BỘ)
    public void clear() {
        items.clear();
    }
}