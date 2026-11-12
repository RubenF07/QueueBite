package model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class OrderTest {

    @Test
    public void testValidOrder() {
        List<OrderItem> items = Arrays.asList(new OrderItem("Burger", 1),
            new OrderItem("Fries", 2));
        Order order = new Order(1, items);
        assertEquals(1, order.getOrderId());
        assertEquals(2, order.getItems().size());
        assertEquals("Burger", order.getItems().get(0).getName());
    }

    @Test
    public void testChangingSourceListDoesNotChangeOrder() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem("Burger", 1));
        Order order = new Order(1, items);
        items.add(new OrderItem("Fries", 1));
        assertEquals(1, order.getItems().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testItemsCannotBeModified() {
        Order order = new Order(1, Arrays.asList(new OrderItem("Burger", 1)));
        order.getItems().add(new OrderItem("Fries", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyItemsRejected() {
        new Order(1, new ArrayList<OrderItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullItemsRejected() {
        new Order(1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullItemInListRejected() {
        new Order(1, Arrays.asList(new OrderItem("Burger", 1), null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonPositiveIdRejected() {
        new Order(0, Arrays.asList(new OrderItem("Burger", 1)));
    }
}
