package common;

import java.io.Serializable;

public class JoinRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int udpPort;

    public JoinRequest() {
    }

    public JoinRequest(String playerName, int udpPort) {
        this.playerName = playerName;
        this.udpPort = udpPort;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }
}
