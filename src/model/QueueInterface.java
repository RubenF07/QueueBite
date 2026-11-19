package model;

import java.util.List;

public class QueueInterface<T> {
    private LinkedQueue<T> queue;

    public QueueInterface() {
        queue = new LinkedQueue<>();
    }

    public void addOrder(T order) {
        queue.enqueue(order);
    }

    public T removeNextOrder() {
        return queue.dequeue();
    }

    public T viewNextOrder() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getSize() {
        return queue.size();
    }

    public List<T> getOrders() {
        return queue.toList();
    }
}
