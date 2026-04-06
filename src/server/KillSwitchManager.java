package server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KillSwitchManager {
    private final Set<Integer> killedPlayers = ConcurrentHashMap.newKeySet();

    public void killPlayer(int playerId) {
        killedPlayers.add(playerId);
    }

    public void revivePlayer(int playerId) {
        killedPlayers.remove(playerId);
    }

    public boolean isKilled(int playerId) {
        return killedPlayers.contains(playerId);
    }
}