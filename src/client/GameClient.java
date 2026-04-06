package client;

import common.Constants;
import common.JoinRequest;
import common.JoinResponse;
import common.PropertiesLoader;
import common.TcpMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class GameClient {

    private final String serverHost;
    private final int serverTcpPort;
    private final int serverUdpPort;
    private final String playerName;
    private final int localUdpPort;

    private final ClientState clientState;

    private Socket tcpSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private DatagramSocket udpSocket;

    private UdpSender udpSender;
    private TcpServerListener tcpServerListener;
    private Thread tcpListenerThread;

    public GameClient(String propertiesPath) {
        Properties properties = PropertiesLoader.load(propertiesPath);

        this.serverHost = PropertiesLoader.getRequired(properties, "server.host");
        this.serverTcpPort = PropertiesLoader.getRequiredInt(properties, "server.tcp.port");
        this.serverUdpPort = PropertiesLoader.getRequiredInt(properties, "server.udp.port");
        this.playerName = PropertiesLoader.getRequired(properties, "client.player.name");
        this.localUdpPort = PropertiesLoader.getInt(properties, "client.local.udp.port", 0);

        this.clientState = new ClientState();
        this.clientState.setLocalPlayerName(playerName);
    }

    public void connect() throws Exception {
        InetAddress serverAddress = InetAddress.getByName(serverHost);

        udpSocket = (localUdpPort == 0) ? new DatagramSocket() : new DatagramSocket(localUdpPort);

        tcpSocket = new Socket(serverHost, serverTcpPort);
        outputStream = new ObjectOutputStream(tcpSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(tcpSocket.getInputStream());

        udpSender = new UdpSender(udpSocket, serverAddress, serverUdpPort, clientState);

        tcpServerListener = new TcpServerListener(tcpSocket, inputStream, clientState);
        tcpListenerThread = new Thread(tcpServerListener);
        tcpListenerThread.setName("tcp-server-listener");
        tcpListenerThread.start();

        sendJoinRequest();

        waitForJoinResponse();
    }

    private void sendJoinRequest() throws Exception {
        JoinRequest joinRequest = new JoinRequest(playerName, udpSocket.getLocalPort());
        TcpMessage joinMessage = TcpMessage.of(Constants.MESSAGE_TYPE_JOIN_REQUEST, joinRequest);

        synchronized (outputStream) {
            outputStream.writeObject(joinMessage);
            outputStream.flush();
            outputStream.reset();
        }
    }

    private void waitForJoinResponse() throws InterruptedException {
        long start = System.currentTimeMillis();
        long timeoutMs = 5000L;

        while (System.currentTimeMillis() - start < timeoutMs) {
            JoinResponse joinResponse = clientState.getJoinResponse();
            if (joinResponse != null) {
                if (!joinResponse.isAccepted()) {
                    throw new RuntimeException("Join rejected: " + joinResponse.getMessage());
                }
                return;
            }
            Thread.sleep(50);
        }

        throw new RuntimeException("Timed out waiting for join response");
    }

    public void sendDisconnectNotice() {
        if (outputStream == null) {
            return;
        }

        try {
            synchronized (outputStream) {
                outputStream.writeObject(TcpMessage.of(Constants.MESSAGE_TYPE_CLIENT_DISCONNECT, "bye"));
                outputStream.flush();
                outputStream.reset();
            }
        } catch (Exception ignored) {
        }
    }

    public void shutdown() {
        sendDisconnectNotice();

        if (tcpServerListener != null) {
            tcpServerListener.shutdown();
        }

        try {
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception ignored) {
        }

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

        clientState.setConnected(false);
    }

    public ClientState getClientState() {
        return clientState;
    }

    public UdpSender getUdpSender() {
        return udpSender;
    }
}