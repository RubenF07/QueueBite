package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import model.EmptyQueueException;
import model.Order;
import model.OrderItem;
import model.OrderManager;
import view.QueueBiteView;

public class QueueBiteController {
    private final OrderManager model;
    private final QueueBiteView view;
    private boolean running;

    public QueueBiteController(OrderManager model, QueueBiteView view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException(
                "Model and view cannot be null");
        }
        this.model = model;
        this.view = view;
    }

    public void run() {
        running = true;
        while (running) {
            try {
                int choice = view.readMenuChoice();
                switch (choice) {
                    case 1:
                        view.showQueue();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        completeNextOrder();
                        break;
                    default:
                        view.showMessage("That isn't an option, pick 1, 2 or 3");
                }
            }
            catch (NoSuchElementException e) {
                // Input has ended
                running = false;
            }
        }
    }

    private void addOrder() {
        List<OrderItem> items = new ArrayList<>();
        while (true) {
            String name = view.readLine("Item name (or 'done' to finish): ");
            if (name.trim().equalsIgnoreCase("done")) {
                break;
            }
            int quantity = view.readInt("Quantity: ");
            try {
                items.add(new OrderItem(name, quantity));
            }
            catch (IllegalArgumentException e) {
                view.showMessage(e.getMessage());
            }
        }
        try {
            Order order = model.placeOrder(items);
            view.showMessage("Order #" + order.getOrderId() + " placed.");
        }
        catch (IllegalArgumentException e) {
            view.showMessage("An order needs at least one item");
        }
    }

    private void completeNextOrder() {
        try {
            Order order = model.completeNextOrder();
            view.showMessage("Order #" + order.getOrderId() + " completed.");
        }
        catch (EmptyQueueException e) {
            view.showMessage("No orders waiting.");
        }
    }
}
