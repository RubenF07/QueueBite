package view;

import java.util.Scanner;

import model.Order;
import model.OrderItem;
import model.OrderQueueListener;

public class QueueBiteView implements OrderQueueListener {
    private final Scanner scanner;
    private Order[] currentOrders;

    public QueueBiteView() {
        scanner = new Scanner(System.in);
        currentOrders = new Order[0];
    }

    public int readMenuChoice() {
        System.out.println();
        System.out.println("1. View queue");
        System.out.println("2. Add order");
        System.out.println("3. Complete next order");
        return readInt("Choose an option: ");
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a number");
            }
        }
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    @Override
    public void ordersChanged(Order[] snapshot) {
        currentOrders = snapshot;
        showQueue();
    }

    public void showQueue() {
        if (currentOrders.length == 0) {
            System.out.println("No orders in the queue right now");
            return;
        }
        System.out.println("Current queue:");
        for (int i = 0; i < currentOrders.length; i++) {
            Order order = currentOrders[i];
            System.out.println((i + 1) + ". Order #" + order.getOrderId());
            for (OrderItem item : order.getItems()) {
                System.out.println("     " + item.getQuantity() + " x "
                    + item.getName());
            }
        }
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
