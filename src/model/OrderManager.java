package model;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private final LinkedQueue<Order> queue;
    private final List<OrderQueueListener> listeners;
    private int nextOrderId;

    public OrderManager() {
        queue = new LinkedQueue<>();
        listeners = new ArrayList<>();
        nextOrderId = 1;
    }

    public Order placeOrder(List<OrderItem> items) {
        // Order's constructor validates; if it throws, no ID is used up
        Order order = new Order(nextOrderId, items);
        queue.enqueue(order);
        nextOrderId++;
        notifyListeners();
        return order;
    }

    public Order completeNextOrder() {
        Order order = queue.dequeue();
        notifyListeners();
        return order;
    }

    public Order[] getPendingOrders() {
        return queue.toList().toArray(new Order[0]);
    }

    public void addListener(OrderQueueListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener can't be null");
        }
        listeners.add(listener);
        listener.ordersChanged(getPendingOrders());
    }

    private void notifyListeners() {
        Order[] snapshot = getPendingOrders();
        for (OrderQueueListener listener : listeners) {
            listener.ordersChanged(snapshot);
        }
    }
}
