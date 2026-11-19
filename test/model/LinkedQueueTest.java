package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class LinkedQueueTest {

    private LinkedQueue<String> queue;

    @BeforeEach
    void setUp() {
        queue = new LinkedQueue<>();
    }


    @Test
    void enqueue_addsElementsInOrder() {
        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");

        assertEquals(3, queue.size());
        assertEquals(Arrays.asList("a", "b", "c"), queue.toList());
    }

    @Test
    void enqueue_nullIsRejected() {
        // Bad input: null must not be stored, and the queue must not change.
        assertThrows(IllegalArgumentException.class, () -> queue.enqueue(null));

        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
    }

    @Test
    void worksThroughQueueInterface() {
        // Code that only knows the interface can still use the queue.
        QueueInterface<String> q = new LinkedQueue<>();
        q.enqueue("a");
        q.enqueue("b");

        assertEquals("a", q.dequeue());
        assertEquals(1, q.size());
    }

    // ---------- dequeue ----------

    @Test
    void dequeue_returnsElementsInFifoOrder() {
        queue.enqueue("a");
        queue.enqueue("b");

        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void dequeue_onEmptyQueueThrows() {
        assertThrows(EmptyQueueException.class, () -> queue.dequeue());
        assertEquals(0, queue.size()); // failed call must not change size
    }

    // ---------- peek ----------

    @Test
    void peek_returnsHeadWithoutRemovingIt() {
        queue.enqueue("a");
        queue.enqueue("b");

        assertEquals("a", queue.peek());
        assertEquals("a", queue.peek());
        assertEquals(2, queue.size());
    }

    @Test
    void peek_onEmptyQueueThrows() {
        assertThrows(EmptyQueueException.class, () -> queue.peek());
    }

    // ---------- isEmpty ----------

    @Test
    void isEmpty_trueForNewQueueFalseAfterEnqueue() {
        assertTrue(queue.isEmpty());

        queue.enqueue("a");

        assertFalse(queue.isEmpty());
    }

    @Test
    void isEmpty_trueAgainAfterFailedDequeueOnEmpty() {
        // Bad-input case: a rejected operation must not corrupt emptiness.
        assertThrows(EmptyQueueException.class, () -> queue.dequeue());

        assertTrue(queue.isEmpty());
    }


    @Test
    void size_tracksEnqueuesAndDequeues() {
        assertEquals(0, queue.size());

        queue.enqueue("a");
        queue.enqueue("b");
        assertEquals(2, queue.size());

        queue.dequeue();
        assertEquals(1, queue.size());
    }

    @Test
    void size_unchangedWhenPeekOrFailedDequeueOnEmpty() {
        assertThrows(EmptyQueueException.class, () -> queue.peek());
        assertThrows(EmptyQueueException.class, () -> queue.dequeue());

        assertEquals(0, queue.size());
    }


    @Test
    void toList_returnsElementsHeadToTail() {
        queue.enqueue("x");
        queue.enqueue("y");

        assertEquals(Arrays.asList("x", "y"), queue.toList());
    }

    @Test
    void toList_onEmptyQueueReturnsEmptyList() {
        List<String> result = queue.toList();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toList_returnsIndependentCopy() {
        queue.enqueue("a");

        List<String> result = queue.toList();
        result.add("hacked");

        assertEquals(1, queue.size());
        assertEquals(Arrays.asList("a"), queue.toList());
    }


    @Test
    void tail_isNullAfterDequeueingLastElement() {
        queue.enqueue("a");
        queue.dequeue();

        assertNull(queue.head);
        assertNull(queue.tail);
    }

    @Test
    void enqueue_afterQueueEmptiedAgainWorksCorrectly() {
        queue.enqueue("a");
        queue.dequeue();          // tail must be reset to null here

        queue.enqueue("b");

        assertEquals(1, queue.size());
        assertEquals("b", queue.peek());
        assertSame(queue.head, queue.tail);
        assertEquals(Arrays.asList("b"), queue.toList());

        assertEquals("b", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void tail_staysTheSameWhenDequeueingFromMultiElementQueue() {
        queue.enqueue("a");
        queue.enqueue("b");
        queue.dequeue();

        assertNotNull(queue.tail);
        assertSame(queue.head, queue.tail);
        assertEquals("b", queue.peek());
    }
}