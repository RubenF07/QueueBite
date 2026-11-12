package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class OrderManagerTest {
    private OrderManager manager;
    private RecordingListener listener;

    @Before
    public void setUp() {
        manager = new OrderManager();
        listener = new RecordingListener();
    }

    private List<OrderItem> items(String name) {
        return Arrays.asList(new OrderItem(name, 1));
    }

    @Test
    public void testPlaceOrderAssignsIncreasingIds() {
        Order first = manager.placeOrder(items("Burger"));
        Order second = manager.placeOrder(items("Fries"));
        assertEquals(1, first.getOrderId());
        assertEquals(2, second.getOrderId());
        assertEquals(2, manager.getPendingOrders().length);
    }

    @Test
    public void testInvalidOrderDoesNotUseIdOrNotify() {
        manager.addListener(listener);
        try {
            manager.placeOrder(new ArrayList<OrderItem>());
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(1, listener.getCallCount());
        assertEquals(0, manager.getPendingOrders().length);
        assertEquals(1, manager.placeOrder(items("Burger")).getOrderId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlaceOrderNullRejected() {
        manager.placeOrder(null);
    }

    @Test
    public void testCompleteNextOrderIsFifo() {
        manager.placeOrder(items("Burger"));
        manager.placeOrder(items("Fries"));
        assertEquals(1, manager.completeNextOrder().getOrderId());
        assertEquals(2, manager.completeNextOrder().getOrderId());
        assertEquals(0, manager.getPendingOrders().length);
    }

    @Test
    public void testCompletedIdsAreNotReused() {
        manager.placeOrder(items("Burger"));
        manager.completeNextOrder();
        assertEquals(2, manager.placeOrder(items("Fries")).getOrderId());
    }

    @Test
    public void testCompleteOnEmptyThrowsAndDoesNotNotify() {
        manager.addListener(listener);
        try {
            manager.completeNextOrder();
            fail("Expected EmptyQueueException");
        }
        catch (EmptyQueueException e) {
            // expected
        }
        assertEquals(1, listener.getCallCount());
    }

    @Test
    public void testAddListenerSendsCurrentSnapshot() {
        manager.placeOrder(items("Burger"));
        manager.addListener(listener);
        assertEquals(1, listener.getCallCount());
        assertEquals(1, listener.getLastSnapshot().length);
    }

    @Test
    public void testListenerNotifiedAfterPlaceAndComplete() {
        manager.addListener(listener);
        manager.placeOrder(items("Burger"));
        assertEquals(2, listener.getCallCount());
        assertEquals(1, listener.getLastSnapshot().length);
        manager.completeNextOrder();
        assertEquals(3, listener.getCallCount());
        assertEquals(0, listener.getLastSnapshot().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullListenerRejected() {
        manager.addListener(null);
    }

    @Test
    public void testSnapshotIsACopy() {
        manager.placeOrder(items("Burger"));
        Order[] snapshot = manager.getPendingOrders();
        snapshot[0] = null;
        assertEquals(1, manager.getPendingOrders()[0].getOrderId());
        assertNotSame(snapshot, manager.getPendingOrders());
    }
}
