package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final int orderId;
    private final List<OrderItem> items;
    private final boolean rush;

    public Order(int orderId, List<OrderItem> items) {
        this(orderId, items, false);
    }

    public Order(int orderId, List<OrderItem> items, boolean rush) {
        if (orderId < 1) {
            throw new IllegalArgumentException("Order ID must be positive");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                "An order needs at least one item");
        }
        if (items.contains(null)) {
            throw new IllegalArgumentException("Order items can't be null");
        }
        this.orderId = orderId;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.rush = rush;
    }

    public int getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public boolean isRush() {
        return rush;
    }
}
