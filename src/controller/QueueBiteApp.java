package controller;

import model.OrderManager;
import view.QueueBiteView;

public class QueueBiteApp {
    public static void main(String[] args) {
        OrderManager orderManager = new OrderManager();
        QueueBiteView view = new QueueBiteView();
        QueueBiteController controller =
            new QueueBiteController(orderManager, view);
        orderManager.addListener(view);
        controller.run();
    }
}
