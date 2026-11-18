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
        System.out.println("0. Exit");
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

    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) {
                return true;
            }
            if (line.equals("n") || line.equals("no")) {
                return false;
            }
            System.out.println("Please enter y or n");
        }
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
            String tag = order.isRush() ? " [RUSH]" : "";
            System.out.println((i + 1) + ". Order #" + order.getOrderId()
                + tag);
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
