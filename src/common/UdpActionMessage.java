package common;

import java.io.Serializable;

public class UdpActionMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private int playerId;
    private long sequenceNumber;
    private long clientTimeMs;
    private PlayerActionType actionType;

    public UdpActionMessage() {
    }

    public UdpActionMessage(int playerId, long sequenceNumber, long clientTimeMs, PlayerActionType actionType) {
        this.playerId = playerId;
        this.sequenceNumber = sequenceNumber;
        this.clientTimeMs = clientTimeMs;
        this.actionType = actionType;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getClientTimeMs() {
        return clientTimeMs;
    }

    public void setClientTimeMs(long clientTimeMs) {
        this.clientTimeMs = clientTimeMs;
    }

    public PlayerActionType getActionType() {
        return actionType;
    }

    public void setActionType(PlayerActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "UdpActionMessage{" +
                "playerId=" + playerId +
                ", sequenceNumber=" + sequenceNumber +
                ", clientTimeMs=" + clientTimeMs +
                ", actionType=" + actionType +
                '}';
    }
}