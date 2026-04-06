package client;

import common.GameSnapshot;
import common.JoinResponse;
import common.PlayerState;

public class ClientState {

    private volatile int localPlayerId = -1;
    private volatile String localPlayerName = "Player";
    private volatile boolean connected = false;
    private volatile boolean joinAccepted = false;
    private volatile JoinResponse joinResponse;
    private volatile GameSnapshot latestSnapshot;

    public int getLocalPlayerId() {
        return localPlayerId;
    }

    public void setLocalPlayerId(int localPlayerId) {
        this.localPlayerId = localPlayerId;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public void setLocalPlayerName(String localPlayerName) {
        this.localPlayerName = localPlayerName;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isJoinAccepted() {
        return joinAccepted;
    }

    public void setJoinAccepted(boolean joinAccepted) {
        this.joinAccepted = joinAccepted;
    }

    public JoinResponse getJoinResponse() {
        return joinResponse;
    }

    public void setJoinResponse(JoinResponse joinResponse) {
        this.joinResponse = joinResponse;
    }

    public GameSnapshot getLatestSnapshot() {
        return latestSnapshot;
    }

    public void setLatestSnapshot(GameSnapshot latestSnapshot) {
        this.latestSnapshot = latestSnapshot;
    }

    public PlayerState getLocalPlayer() {
        GameSnapshot snapshot = latestSnapshot;
        if (snapshot == null || snapshot.getPlayers() == null) {
            return null;
        }

        for (PlayerState player : snapshot.getPlayers()) {
            if (player.getPlayerId() == localPlayerId) {
                return player;
            }
        }
        return null;
    }
}