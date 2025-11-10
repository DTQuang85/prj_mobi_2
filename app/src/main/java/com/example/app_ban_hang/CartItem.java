package com.example.app_ban_hang;

import androidx.annotation.Nullable;
import java.io.Serializable;

public class CartItem implements Serializable {
    public final Product product;
    public int quantity;
    @Nullable
    public final Integer size;

    public CartItem(Product product, int quantity) {
        this(product, quantity, null);
    }

    public CartItem(Product product, int quantity, @Nullable Integer size) {
        this.product = product;
        this.quantity = Math.max(1, quantity);
        this.size = size;
    }

    public double unitPrice() {
        return product.priceVnd > 0 ? product.priceVnd : 0;
    }

    public double lineTotal() {
        return unitPrice() * quantity;
    }

    // Thêm phương thức để so sánh 2 CartItem
    public boolean isSameProduct(CartItem other) {
        if (other == null) return false;
        if (this == other) return true;

        // So sánh tên sản phẩm và size
        boolean sameProduct = product.name != null && product.name.equals(other.product.name);
        boolean sameSize = (size == null && other.size == null) ||
                (size != null && size.equals(other.size));

        return sameProduct && sameSize;
    }
}