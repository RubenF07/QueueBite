package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
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

    // ---------- rush lane ----------

    @Test
    public void testRushOrderCompletedBeforeOlderRegularOrder() {
        manager.placeOrder(items("Burger"), false);
        manager.placeOrder(items("Fries"), true);
        Order first = manager.completeNextOrder();
        assertEquals(2, first.getOrderId());
        assertTrue(first.isRush());
        assertEquals(1, manager.completeNextOrder().getOrderId());
    }

    @Test
    public void testEachLaneIsFifo() {
        manager.placeOrder(items("A"), true);
        manager.placeOrder(items("B"), false);
        manager.placeOrder(items("C"), true);
        manager.placeOrder(items("D"), false);
        assertEquals(1, manager.completeNextOrder().getOrderId());
        assertEquals(3, manager.completeNextOrder().getOrderId());
        assertEquals(2, manager.completeNextOrder().getOrderId());
        assertEquals(4, manager.completeNextOrder().getOrderId());
    }

    @Test
    public void testIdsSharedAcrossLanes() {
        assertEquals(1, manager.placeOrder(items("A"), false).getOrderId());
        assertEquals(2, manager.placeOrder(items("B"), true).getOrderId());
        assertEquals(3, manager.placeOrder(items("C"), false).getOrderId());
    }

    @Test
    public void testSnapshotListsRushOrdersFirst() {
        manager.placeOrder(items("A"), false);
        manager.placeOrder(items("B"), true);
        manager.placeOrder(items("C"), false);
        Order[] snapshot = manager.getPendingOrders();
        assertEquals(3, snapshot.length);
        assertEquals(2, snapshot[0].getOrderId());
        assertEquals(1, snapshot[1].getOrderId());
        assertEquals(3, snapshot[2].getOrderId());
    }

    @Test
    public void testRushPlacementNotifiesListener() {
        manager.addListener(listener);
        manager.placeOrder(items("A"), true);
        assertEquals(2, listener.getCallCount());
        assertTrue(listener.getLastSnapshot()[0].isRush());
    }

    @Test
    public void testInvalidRushOrderUsesNoIdAndDoesNotNotify() {
        manager.addListener(listener);
        try {
            manager.placeOrder(new ArrayList<OrderItem>(), true);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals(1, listener.getCallCount());
        assertEquals(0, manager.getPendingOrders().length);
        assertEquals(1, manager.placeOrder(items("A"), true).getOrderId());
    }

    @Test
    public void testCompleteWithBothLanesEmptyThrows() {
        manager.placeOrder(items("A"), true);
        manager.placeOrder(items("B"), false);
        manager.completeNextOrder();
        manager.completeNextOrder();
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
}
