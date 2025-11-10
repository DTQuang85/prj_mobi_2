package com.example.app_ban_hang;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CartManager {
    private static final CartManager INSTANCE = new CartManager();

    public static CartManager get() {
        return INSTANCE;
    }

    private final List<CartItem> items = new ArrayList<>();

    private CartManager() {}

    // Thêm sản phẩm với số lượng mặc định 1
    public void add(Product product) {
        add(product, 1, null);
    }

    // Thêm sản phẩm với số lượng
    public void add(Product product, int quantity) {
        add(product, quantity, null);
    }

    // Thêm sản phẩm với size
    public void add(Product product, int quantity, @Nullable Integer size) {
        if (product == null || quantity <= 0) return;

        // Tìm item trùng (cùng product và cùng size)
        for (CartItem item : items) {
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

    // Xóa item theo vị trí
    public void remove(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    // Xóa item theo product và size
    public void remove(Product product, @Nullable Integer size) {
        Iterator<CartItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.isSameProduct(new CartItem(product, 1, size))) {
                iterator.remove();
                break;
            }
        }
    }

    // Lấy danh sách items (read-only)
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    // Tính tổng tiền
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.lineTotal();
        }
        return total;
    }

    // Lấy tổng số lượng sản phẩm
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items) {
            total += item.quantity;
        }
        return total;
    }

    // Kiểm tra giỏ hàng trống
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Xóa toàn bộ giỏ hàng
    public void clear() {
        items.clear();
    }

    // Lấy số lượng item trong giỏ
    public int getItemCount() {
        return items.size();
    }
}