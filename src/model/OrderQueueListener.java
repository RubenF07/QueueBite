package model;

public interface OrderQueueListener {
    void ordersChanged(Order[] snapshot);
}
