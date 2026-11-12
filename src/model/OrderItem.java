package model;

public class OrderItem {
    private final String name;
    private final int quantity;

    public OrderItem(String name, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name can't be blank");
        }
        String trimmed = name.trim();
        if (trimmed.matches("\\d+")) {
            throw new IllegalArgumentException(
                "Item name can't be only digits");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity has to be at least 1");
        }
        this.name = trimmed;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
