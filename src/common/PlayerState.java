package common;

import java.io.Serializable;

public class PlayerState implements Serializable {
    private static final long serialVersionUID = 1L;

    private int playerId;
    private String playerName;
    private Position position;
    private int score;
    private boolean connected;
    private boolean frozen;
    private long frozenUntilEpochMs;
    private boolean freezePowerAvailable;
    private long freezeCooldownUntilEpochMs;
    private int hp;

    public PlayerState() {
    }

    public PlayerState(int playerId, String playerName, Position position) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
        this.score = 0;
        this.connected = true;
        this.frozen = false;
        this.frozenUntilEpochMs = 0L;
        this.freezePowerAvailable = false;
        this.freezeCooldownUntilEpochMs = 0L;
        this.hp = 100;
    }

    public PlayerState copy() {
        PlayerState copy = new PlayerState();
        copy.playerId = this.playerId;
        copy.playerName = this.playerName;
        copy.position = this.position == null ? null : this.position.copy();
        copy.score = this.score;
        copy.connected = this.connected;
        copy.frozen = this.frozen;
        copy.frozenUntilEpochMs = this.frozenUntilEpochMs;
        copy.freezePowerAvailable = this.freezePowerAvailable;
        copy.freezeCooldownUntilEpochMs = this.freezeCooldownUntilEpochMs;
        copy.hp = this.hp;
        return copy;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public long getFrozenUntilEpochMs() {
        return frozenUntilEpochMs;
    }

    public void setFrozenUntilEpochMs(long frozenUntilEpochMs) {
        this.frozenUntilEpochMs = frozenUntilEpochMs;
    }

    public boolean isFreezePowerAvailable() {
        return freezePowerAvailable;
    }

    public void setFreezePowerAvailable(boolean freezePowerAvailable) {
        this.freezePowerAvailable = freezePowerAvailable;
    }

    public long getFreezeCooldownUntilEpochMs() {
        return freezeCooldownUntilEpochMs;
    }

    public void setFreezeCooldownUntilEpochMs(long freezeCooldownUntilEpochMs) {
        this.freezeCooldownUntilEpochMs = freezeCooldownUntilEpochMs;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}