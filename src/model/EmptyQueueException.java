package model;

public class EmptyQueueException extends RuntimeException {
    public EmptyQueueException() {
        super("The queue is empty");
    }
}
