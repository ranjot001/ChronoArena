package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private long serverTimeMs;
    private int timeLeftSeconds;
    private boolean matchRunning;
    private boolean matchEnded;
    private Integer winnerPlayerId;
    private List<PlayerState> players;
    private List<ZoneStateModel> zones;
    private List<ItemState> items;
    private String serverNotice;

    public GameSnapshot() {
        this.players = new ArrayList<>();
        this.zones = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public GameSnapshot copy() {
        GameSnapshot copy = new GameSnapshot();
        copy.serverTimeMs = this.serverTimeMs;
        copy.timeLeftSeconds = this.timeLeftSeconds;
        copy.matchRunning = this.matchRunning;
        copy.matchEnded = this.matchEnded;
        copy.winnerPlayerId = this.winnerPlayerId;
        copy.serverNotice = this.serverNotice;

        for (PlayerState player : this.players) {
            copy.players.add(player.copy());
        }
        for (ZoneStateModel zone : this.zones) {
            copy.zones.add(zone.copy());
        }
        for (ItemState item : this.items) {
            copy.items.add(item.copy());
        }
        return copy;
    }

    public long getServerTimeMs() {
        return serverTimeMs;
    }

    public void setServerTimeMs(long serverTimeMs) {
        this.serverTimeMs = serverTimeMs;
    }

    public int getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    public void setTimeLeftSeconds(int timeLeftSeconds) {
        this.timeLeftSeconds = timeLeftSeconds;
    }

    public boolean isMatchRunning() {
        return matchRunning;
    }

    public void setMatchRunning(boolean matchRunning) {
        this.matchRunning = matchRunning;
    }

    public boolean isMatchEnded() {
        return matchEnded;
    }

    public void setMatchEnded(boolean matchEnded) {
        this.matchEnded = matchEnded;
    }

    public Integer getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public void setWinnerPlayerId(Integer winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerState> players) {
        this.players = players;
    }

    public List<ZoneStateModel> getZones() {
        return zones;
    }

    public void setZones(List<ZoneStateModel> zones) {
        this.zones = zones;
    }

    public List<ItemState> getItems() {
        return items;
    }

    public void setItems(List<ItemState> items) {
        this.items = items;
    }

    public String getServerNotice() {
        return serverNotice;
    }

    public void setServerNotice(String serverNotice) {
        this.serverNotice = serverNotice;
    }
}