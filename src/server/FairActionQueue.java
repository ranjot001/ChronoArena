package server;

import common.UdpActionMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FairActionQueue {
    private final ConcurrentLinkedQueue<QueuedPlayerAction> queue = new ConcurrentLinkedQueue<>();

    public void enqueue(UdpActionMessage message) {
        if (message == null) {
            return;
        }
        queue.offer(new QueuedPlayerAction(message, System.currentTimeMillis()));
    }

    public List<QueuedPlayerAction> drainSorted() {
        List<QueuedPlayerAction> drained = new ArrayList<>();
        QueuedPlayerAction current;

        while ((current = queue.poll()) != null) {
            drained.add(current);
        }

        Collections.sort(drained);
        return drained;
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }
}