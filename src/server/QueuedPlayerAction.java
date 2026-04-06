package server;

import common.UdpActionMessage;

public class QueuedPlayerAction implements Comparable<QueuedPlayerAction> {
    private final UdpActionMessage actionMessage;
    private final long serverReceivedAtMs;

    public QueuedPlayerAction(UdpActionMessage actionMessage, long serverReceivedAtMs) {
        this.actionMessage = actionMessage;
        this.serverReceivedAtMs = serverReceivedAtMs;
    }

    public UdpActionMessage getActionMessage() {
        return actionMessage;
    }

    public long getServerReceivedAtMs() {
        return serverReceivedAtMs;
    }

    @Override
    public int compareTo(QueuedPlayerAction other) {
        int timeCompare = Long.compare(this.serverReceivedAtMs, other.serverReceivedAtMs);
        if (timeCompare != 0) {
            return timeCompare;
        }

        int playerCompare = Integer.compare(
                this.actionMessage.getPlayerId(),
                other.actionMessage.getPlayerId()
        );
        if (playerCompare != 0) {
            return playerCompare;
        }

        return Long.compare(
                this.actionMessage.getSequenceNumber(),
                other.actionMessage.getSequenceNumber()
        );
    }
}