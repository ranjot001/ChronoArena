package client;

import common.PlayerActionType;
import common.SerializationUtils;
import common.UdpActionMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicLong;

public class UdpSender {

    private final DatagramSocket udpSocket;
    private final InetAddress serverAddress;
    private final int serverUdpPort;
    private final ClientState clientState;
    private final AtomicLong sequenceGenerator = new AtomicLong(0);

    public UdpSender(DatagramSocket udpSocket,
                     InetAddress serverAddress,
                     int serverUdpPort,
                     ClientState clientState) {
        this.udpSocket = udpSocket;
        this.serverAddress = serverAddress;
        this.serverUdpPort = serverUdpPort;
        this.clientState = clientState;
    }

    public void sendAction(PlayerActionType actionType) {
        if (actionType == null) {
            return;
        }

        int playerId = clientState.getLocalPlayerId();
        if (playerId <= 0) {
            return;
        }

        try {
            UdpActionMessage actionMessage = new UdpActionMessage(
                    playerId,
                    sequenceGenerator.incrementAndGet(),
                    System.currentTimeMillis(),
                    actionType
            );

            byte[] data = SerializationUtils.serialize(actionMessage);
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    serverAddress,
                    serverUdpPort
            );

            udpSocket.send(packet);
        } catch (Exception e) {
            System.err.println("Failed to send UDP action: " + e.getMessage());
        }
    }
}