package client;

import common.Constants;
import common.GameSnapshot;
import common.JoinResponse;
import common.TcpMessage;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TcpServerListener implements Runnable {

    private final Socket tcpSocket;
    private final ObjectInputStream inputStream;
    private final ClientState clientState;
    private volatile boolean running = true;

    public TcpServerListener(Socket tcpSocket,
                             ObjectInputStream inputStream,
                             ClientState clientState) {
        this.tcpSocket = tcpSocket;
        this.inputStream = inputStream;
        this.clientState = clientState;
    }

    @Override
    public void run() {
        try {
            while (running && !tcpSocket.isClosed()) {
                Object raw = inputStream.readObject();
                if (!(raw instanceof TcpMessage)) {
                    continue;
                }

                TcpMessage message = (TcpMessage) raw;
                handleMessage(message);
            }
        } catch (EOFException eofException) {
            System.out.println("Server connection closed.");
        } catch (Exception e) {
            if (running) {
                System.err.println("TCP listener error: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            clientState.setConnected(false);
            shutdown();
        }
    }

    private void handleMessage(TcpMessage message) {
        if (message == null || message.getType() == null) {
            return;
        }

        switch (message.getType()) {
            case Constants.MESSAGE_TYPE_JOIN_RESPONSE:
                if (message.getPayload() instanceof JoinResponse) {
                    JoinResponse joinResponse = (JoinResponse) message.getPayload();
                    clientState.setJoinResponse(joinResponse);
                    clientState.setJoinAccepted(joinResponse.isAccepted());
                    clientState.setConnected(joinResponse.isAccepted());

                    if (joinResponse.isAccepted()) {
                        clientState.setLocalPlayerId(joinResponse.getPlayerId());
                    }
                }
                break;

            case Constants.MESSAGE_TYPE_GAME_SNAPSHOT:
                if (message.getPayload() instanceof GameSnapshot) {
                    GameSnapshot snapshot = (GameSnapshot) message.getPayload();
                    clientState.setLatestSnapshot(snapshot);
                }
                break;

            case Constants.MESSAGE_TYPE_SERVER_NOTICE:
            default:
                break;
        }
    }

    public void shutdown() {
        running = false;
        try {
            if (!tcpSocket.isClosed()) {
                tcpSocket.close();
            }
        } catch (Exception ignored) {
        }
    }
}