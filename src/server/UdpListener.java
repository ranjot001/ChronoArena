package server;

import common.SerializationUtils;
import common.UdpActionMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UdpListener implements Runnable {

    private final int udpPort;
    private final FairActionQueue fairActionQueue;
    private final GameStateManager gameStateManager;
    private final KillSwitchManager killSwitchManager;
    private final GameServer gameServer;

    private volatile boolean running;
    private DatagramSocket udpSocket;

    public UdpListener(int udpPort,
                       FairActionQueue fairActionQueue,
                       GameStateManager gameStateManager,
                       KillSwitchManager killSwitchManager,
                       GameServer gameServer) {
        this.udpPort = udpPort;
        this.fairActionQueue = fairActionQueue;
        this.gameStateManager = gameStateManager;
        this.killSwitchManager = killSwitchManager;
        this.gameServer = gameServer;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            udpSocket = new DatagramSocket(udpPort);
            System.out.println("UDP listener started on port " + udpPort);

            while (running) {
                byte[] buffer = new byte[8192];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);

                byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
                Object deserialized = SerializationUtils.deserialize(data);

                if (!(deserialized instanceof UdpActionMessage)) {
                    continue;
                }

                UdpActionMessage actionMessage = (UdpActionMessage) deserialized;

                if (killSwitchManager.isKilled(actionMessage.getPlayerId())) {
                    continue;
                }

                if (!gameStateManager.hasPlayer(actionMessage.getPlayerId())) {
                    continue;
                }

                ClientSession session = gameServer.getSession(actionMessage.getPlayerId());
                if (session == null || !session.isConnected()) {
                    continue;
                }

                synchronized (session) {
                    if (!session.shouldAcceptSequence(actionMessage.getSequenceNumber())) {
                        continue;
                    }
                    session.markSequenceProcessed(actionMessage.getSequenceNumber());
                }

                fairActionQueue.enqueue(actionMessage);
            }
        } catch (Exception e) {
            if (running) {
                System.err.println("UDP listener stopped with error: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
    }
}