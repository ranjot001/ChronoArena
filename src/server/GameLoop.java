package server;

import common.Constants;
import common.GameSnapshot;
import common.TcpMessage;

import java.io.ObjectOutputStream;
import java.util.List;

public class GameLoop implements Runnable {

    private final FairActionQueue fairActionQueue;
    private final GameStateManager gameStateManager;
    private final KillSwitchManager killSwitchManager;
    private final GameServer gameServer;

    private volatile boolean running;

    public GameLoop(FairActionQueue fairActionQueue,
                    GameStateManager gameStateManager,
                    KillSwitchManager killSwitchManager,
                    GameServer gameServer) {
        this.fairActionQueue = fairActionQueue;
        this.gameStateManager = gameStateManager;
        this.killSwitchManager = killSwitchManager;
        this.gameServer = gameServer;
        this.running = true;
    }

    @Override
    public void run() {
        long lastBroadcastTime = 0L;

        while (running) {
            long tickStart = System.currentTimeMillis();

            try {
                List<QueuedPlayerAction> actions = fairActionQueue.drainSorted();
                gameStateManager.processActions(actions, killSwitchManager);

                long now = System.currentTimeMillis();
                if (now - lastBroadcastTime >= Constants.SNAPSHOT_BROADCAST_INTERVAL_MS) {
                    broadcastSnapshot();
                    lastBroadcastTime = now;
                }

                long tickElapsed = System.currentTimeMillis() - tickStart;
                long sleepTime = Constants.TICK_MILLIS - tickElapsed;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                System.err.println("Game loop interrupted");
                break;
            } catch (Exception e) {
                System.err.println("Game loop error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void broadcastSnapshot() {
        GameSnapshot snapshot = gameStateManager.createSnapshot();
        TcpMessage snapshotMessage = TcpMessage.of(Constants.MESSAGE_TYPE_GAME_SNAPSHOT, snapshot);

        for (ClientSession session : gameServer.getConnectedSessions()) {
            if (!session.isConnected()) {
                continue;
            }

            try {
                ObjectOutputStream outputStream = session.getOutputStream();
                synchronized (outputStream) {
                    outputStream.writeObject(snapshotMessage);
                    outputStream.flush();
                    outputStream.reset();
                }
            } catch (Exception e) {
                System.err.println("Failed to send snapshot to player " + session.getPlayerId());
                gameServer.disconnectClient(session.getPlayerId());
            }
        }
    }

    public void shutdown() {
        running = false;
    }
}