package model;

import java.util.List;

public class QueueInterface {
    private LinkedQueue<Order> queue;

    public QueueInterface() {
        queue = new LinkedQueue<>();
    }

    public void addOrder(Order order) {
        queue.enqueue(order);
    }

    public Order removeNextOrder() {
        return queue.dequeue();
    }

    public Order viewNextOrder() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getSize() {
        return queue.size();
    }

    public List<Order> getOrders() {
        return queue.toList();
    }
}
