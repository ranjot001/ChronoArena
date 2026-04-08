package server;

import common.PlayerState;
import common.Position;
import common.PropertiesLoader;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {

    private final String host;
    private final int tcpPort;
    private final int udpPort;

    private final SpawnManager spawnManager;
    private final GameStateManager gameStateManager;
    private final FairActionQueue fairActionQueue;
    private final KillSwitchManager killSwitchManager;

    private final ConcurrentMap<Integer, ClientSession> sessions;
    private final AtomicInteger playerIdSequence;

    private volatile boolean running;
    private ServerSocket tcpServerSocket;

    private Thread udpThread;
    private Thread gameLoopThread;
    private Thread consoleThread;

    private UdpListener udpListener;
    private GameLoop gameLoop;

    public GameServer(String propertiesPath) {
        Properties properties = PropertiesLoader.load(propertiesPath);

        this.host = PropertiesLoader.getRequired(properties, "server.host");
        this.tcpPort = PropertiesLoader.getRequiredInt(properties, "server.tcp.port");
        this.udpPort = PropertiesLoader.getRequiredInt(properties, "server.udp.port");

        this.spawnManager = new SpawnManager();
        this.gameStateManager = new GameStateManager(spawnManager);
        this.fairActionQueue = new FairActionQueue();
        this.killSwitchManager = new KillSwitchManager();

        this.sessions = new ConcurrentHashMap<>();
        this.playerIdSequence = new AtomicInteger(1);

        this.running = true;
    }

    public void start() throws Exception {
        startBackgroundWorkers();

        tcpServerSocket = new ServerSocket(tcpPort);
        System.out.println("ChronoArena server started");
        System.out.println("TCP server listening on " + host + ":" + tcpPort);
        System.out.println("UDP server listening on " + host + ":" + udpPort);

        while (running) {
            Socket clientSocket = tcpServerSocket.accept();
            Thread tcpHandlerThread = new Thread(new TcpClientHandler(clientSocket, this));
            tcpHandlerThread.setName("tcp-client-" + clientSocket.getPort());
            tcpHandlerThread.start();
        }
    }

    private void startBackgroundWorkers() {
        udpListener = new UdpListener(udpPort, fairActionQueue, gameStateManager, killSwitchManager, this);
        udpThread = new Thread(udpListener);
        udpThread.setName("udp-listener");
        udpThread.start();

        gameLoop = new GameLoop(fairActionQueue, gameStateManager, killSwitchManager, this);
        gameLoopThread = new Thread(gameLoop);
        gameLoopThread.setName("game-loop");
        gameLoopThread.start();

        consoleThread = new Thread(new ServerConsole(this));
        consoleThread.setName("server-console");
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    public synchronized ClientSession registerClient(String playerName,
                                                     Socket tcpSocket,
                                                     ObjectOutputStream outputStream,
                                                     InetAddress clientAddress,
                                                     int clientUdpPort) {
        int playerId = playerIdSequence.getAndIncrement();

        ClientSession session = new ClientSession(playerId, playerName);
        session.setTcpSocket(tcpSocket);
        session.setOutputStream(outputStream);
        session.setClientAddress(clientAddress);
        session.setClientUdpPort(clientUdpPort);

        sessions.put(playerId, session);

        Position spawn = gameStateManager.getSpawnForNewPlayer(playerId - 1);
        gameStateManager.addPlayer(playerId, playerName, spawn);

        if (gameStateManager.getPlayerCount() >= 2) {
            gameStateManager.startMatch();
        }

        return session;
    }

    public Position getSpawnForPlayer(int playerId) {
        PlayerState playerState = gameStateManager.getPlayer(playerId);
        if (playerState != null) {
            return playerState.getPosition();
        }
        return new Position(50, 50);
    }

    public ClientSession getSession(int playerId) {
        return sessions.get(playerId);
    }

    public void disconnectClient(int playerId) {
        ClientSession session = sessions.get(playerId);
        if (session == null) {
            return;
        }

        session.setConnected(false);
        session.closeQuietly();

        gameStateManager.markPlayerDisconnected(playerId);
        sessions.remove(playerId);

        System.out.println("Disconnected player " + playerId);
    }

    public Collection<ClientSession> getConnectedSessions() {
        List<ClientSession> active = new ArrayList<>();
        for (ClientSession session : sessions.values()) {
            if (session.isConnected()) {
                active.add(session);
            }
        }
        return active;
    }

    public void printConnectedPlayers() {
        Collection<ClientSession> active = getConnectedSessions();
        if (active.isEmpty()) {
            System.out.println("No connected players.");
            return;
        }

        System.out.println("Connected players:");
        for (ClientSession session : active) {
            System.out.println("  #" + session.getPlayerId() + " " + session.getPlayerName());
        }
    }

    public void killPlayer(int playerId) {
        killSwitchManager.killPlayer(playerId);
        disconnectClient(playerId);
        gameStateManager.removePlayerCompletely(playerId);
        System.out.println("Kill switch activated for player " + playerId);
    }

    public void shutdown() {
        running = false;

        try {
            if (tcpServerSocket != null && !tcpServerSocket.isClosed()) {
                tcpServerSocket.close();
            }
        } catch (Exception ignored) {
        }

        if (udpListener != null) {
            udpListener.shutdown();
        }

        if (gameLoop != null) {
            gameLoop.shutdown();
        }

        for (ClientSession session : sessions.values()) {
            session.closeQuietly();
        }

        sessions.clear();
        System.out.println("Server shutdown complete");
    }
}