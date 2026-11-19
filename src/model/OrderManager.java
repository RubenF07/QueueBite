package model;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    // Declared as the interface so the queue implementation can be swapped
    // by changing only the two lines in the constructor
    private final QueueInterface<Order> regularQueue;
    private final QueueInterface<Order> rushQueue;
    private final List<OrderQueueListener> listeners;
    private int nextOrderId;

    public OrderManager() {
        regularQueue = new LinkedQueue<>();
        rushQueue = new LinkedQueue<>();
        listeners = new ArrayList<>();
        nextOrderId = 1;
    }

    public Order placeOrder(List<OrderItem> items) {
        return placeOrder(items, false);
    }

    public Order placeOrder(List<OrderItem> items, boolean rush) {
        // Order's constructor validates; if it throws, no ID is used up
        Order order = new Order(nextOrderId, items, rush);
        if (rush) {
            rushQueue.enqueue(order);
        }
        else {
            regularQueue.enqueue(order);
        }
        nextOrderId++;
        notifyListeners();
        return order;
    }

    public Order completeNextOrder() {
        // Rush orders are always served first; if both lanes are empty,
        // regularQueue.dequeue() throws EmptyQueueException
        Order order;
        if (!rushQueue.isEmpty()) {
            order = rushQueue.dequeue();
        }
        else {
            order = regularQueue.dequeue();
        }
        notifyListeners();
        return order;
    }

    public Order[] getPendingOrders() {
        List<Order> all = rushQueue.toList();
        all.addAll(regularQueue.toList());
        return all.toArray(new Order[0]);
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
