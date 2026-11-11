package model;

import java.util.ArrayList;
import java.util.List;

public class LinkedQueue<T> {
    private class Node<T> {
        private T data;
        private Node<T> prev;
        private Node<T> next;

        Node(T val, Node<T> prev, Node<T> next) {
            this.data = val;
            this.prev = prev;
            this.next = next;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }
    }
    
    Node<T> head;
    Node<T> tail;
    private int size;

    public void enqueue(T val) {
        Node<T> newNode = new Node<>(val, tail, null);
        if (isEmpty()) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            // TODO
        }
        T val = head.getData();
        head = head.getNext();
        if (head == null) {
            tail = null;
        } else {
            head.setPrev(null);
        }
        size--;
        return val;
    }

    public T peek() {
        if (isEmpty()) {
            // TODO
        }
        return head.getData();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
    
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        return list;
    }
}
