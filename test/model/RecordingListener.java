package model;

public class RecordingListener implements OrderQueueListener {
    private int callCount;
    private Order[] lastSnapshot;

    @Override
    public void ordersChanged(Order[] snapshot) {
        callCount++;
        lastSnapshot = snapshot;
    }

    public int getCallCount() {
        return callCount;
    }

    public Order[] getLastSnapshot() {
        return lastSnapshot;
    }
}
