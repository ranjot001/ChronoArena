package common;

import java.io.Serializable;

public class JoinResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean accepted;
    private int playerId;
    private Position spawnPosition;
    private String message;

    public JoinResponse() {
    }

    public JoinResponse(boolean accepted, int playerId, Position spawnPosition, String message) {
        this.accepted = accepted;
        this.playerId = playerId;
        this.spawnPosition = spawnPosition;
        this.message = message;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Position spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}