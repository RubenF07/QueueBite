package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A singly linked FIFO queue. Enqueue at the tail and dequeue at the head
 * are both O(1).
 *
 * @param <T> the type of entry stored in the queue
 */
public class LinkedQueue<T> implements QueueInterface<T> {
    private static class Node<T> {
        private final T data;
        private Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    // Package-private so LinkedQueueTest can check the head/tail invariants
    Node<T> head;
    Node<T> tail;
    private int size;

    @Override
    public void enqueue(T entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Can't enqueue null");
        }
        Node<T> newNode = new Node<>(entry);
        if (isEmpty()) {
            head = newNode;
        }
        else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            throw new EmptyQueueException();
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return data;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyQueueException();
        }
        return head.data;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}
