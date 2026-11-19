package model;

import java.util.List;

/**
 * A FIFO queue. Code that depends on this interface instead of a specific
 * class can have its queue implementation swapped without other changes.
 *
 * @param <T> the type of entry stored in the queue
 */
public interface QueueInterface<T> {

    /**
     * Adds an entry at the back of the queue.
     *
     * @param entry the entry to add
     * @throws IllegalArgumentException if entry is null
     */
    void enqueue(T entry);

    /**
     * Removes and returns the entry at the front of the queue.
     *
     * @return the front entry
     * @throws EmptyQueueException if the queue is empty
     */
    T dequeue();

    /**
     * Returns the entry at the front of the queue without removing it.
     *
     * @return the front entry
     * @throws EmptyQueueException if the queue is empty
     */
    T peek();

    /**
     * @return true if the queue has no entries
     */
    boolean isEmpty();

    /**
     * @return the number of entries in the queue
     */
    int size();

    /**
     * Returns a snapshot of the entries from front to back. Changing the
     * returned list does not change the queue.
     *
     * @return a new list of the entries, empty if the queue is empty
     */
    List<T> toList();
}
