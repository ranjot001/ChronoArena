package server;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSession {
    private final int playerId;
    private final String playerName;

    private Socket tcpSocket;
    private ObjectOutputStream outputStream;

    private InetAddress clientAddress;
    private int clientUdpPort;

    private final AtomicBoolean connected;
    private volatile long lastProcessedSequenceNumber;
    private volatile long lastHeardFromEpochMs;

    public ClientSession(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.connected = new AtomicBoolean(true);
        this.lastProcessedSequenceNumber = -1L;
        this.lastHeardFromEpochMs = System.currentTimeMillis();
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Socket getTcpSocket() {
        return tcpSocket;
    }

    public void setTcpSocket(Socket tcpSocket) {
        this.tcpSocket = tcpSocket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public int getClientUdpPort() {
        return clientUdpPort;
    }

    public void setClientUdpPort(int clientUdpPort) {
        this.clientUdpPort = clientUdpPort;
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void setConnected(boolean value) {
        connected.set(value);
    }

    public long getLastProcessedSequenceNumber() {
        return lastProcessedSequenceNumber;
    }

    public void setLastProcessedSequenceNumber(long lastProcessedSequenceNumber) {
        this.lastProcessedSequenceNumber = lastProcessedSequenceNumber;
    }

    public boolean shouldAcceptSequence(long incomingSequence) {
        return incomingSequence > lastProcessedSequenceNumber;
    }

    public void markSequenceProcessed(long sequenceNumber) {
        this.lastProcessedSequenceNumber = sequenceNumber;
        refreshHeartbeat();
    }

    public long getLastHeardFromEpochMs() {
        return lastHeardFromEpochMs;
    }

    public void setLastHeardFromEpochMs(long lastHeardFromEpochMs) {
        this.lastHeardFromEpochMs = lastHeardFromEpochMs;
    }

    public void refreshHeartbeat() {
        this.lastHeardFromEpochMs = System.currentTimeMillis();
    }

    public void closeQuietly() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (tcpSocket != null && !tcpSocket.isClosed()) {
                tcpSocket.close();
            }
        } catch (Exception ignored) {
        }
    }
}