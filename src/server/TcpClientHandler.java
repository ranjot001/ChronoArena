package server;

import common.Constants;
import common.JoinRequest;
import common.JoinResponse;
import common.Position;
import common.TcpMessage;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TcpClientHandler implements Runnable {

    private final Socket clientSocket;
    private final GameServer gameServer;

    public TcpClientHandler(Socket clientSocket, GameServer gameServer) {
        this.clientSocket = clientSocket;
        this.gameServer = gameServer;
    }

    @Override
    public void run() {
        int playerId = -1;

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            Object raw = inputStream.readObject();
            if (!(raw instanceof TcpMessage)) {
                outputStream.writeObject(TcpMessage.of(
                        Constants.MESSAGE_TYPE_JOIN_RESPONSE,
                        new JoinResponse(false, -1, null, "Invalid join message")
                ));
                outputStream.flush();
                return;
            }

            TcpMessage tcpMessage = (TcpMessage) raw;
            if (!Constants.MESSAGE_TYPE_JOIN_REQUEST.equals(tcpMessage.getType())
                    || !(tcpMessage.getPayload() instanceof JoinRequest)) {
                outputStream.writeObject(TcpMessage.of(
                        Constants.MESSAGE_TYPE_JOIN_RESPONSE,
                        new JoinResponse(false, -1, null, "Expected JOIN_REQUEST")
                ));
                outputStream.flush();
                return;
            }

            JoinRequest joinRequest = (JoinRequest) tcpMessage.getPayload();
            String playerName = sanitizePlayerName(joinRequest.getPlayerName());

            ClientSession session = gameServer.registerClient(
                    playerName,
                    clientSocket,
                    outputStream,
                    clientSocket.getInetAddress(),
                    joinRequest.getUdpPort()
            );

            playerId = session.getPlayerId();
            Position spawnPosition = gameServer.getSpawnForPlayer(playerId);

            JoinResponse joinResponse = new JoinResponse(
                    true,
                    playerId,
                    spawnPosition,
                    "Welcome to ChronoArena"
            );

            outputStream.writeObject(TcpMessage.of(Constants.MESSAGE_TYPE_JOIN_RESPONSE, joinResponse));
            outputStream.flush();

            System.out.println("Player joined: id=" + playerId + ", name=" + playerName);

            while (session.isConnected() && !clientSocket.isClosed()) {
                Object incoming = inputStream.readObject();

                if (incoming instanceof TcpMessage) {
                    TcpMessage message = (TcpMessage) incoming;

                    if (Constants.MESSAGE_TYPE_CLIENT_DISCONNECT.equals(message.getType())) {
                        break;
                    }
                }
            }

        } catch (EOFException eofException) {
            if (playerId != -1) {
                System.out.println("Client disconnected: playerId=" + playerId);
            }
        } catch (SocketException socketException) {
            String msg = socketException.getMessage();
            if (msg != null && msg.equalsIgnoreCase("Socket closed")) {
                if (playerId != -1) {
                    System.out.println("Client socket closed: playerId=" + playerId);
                }
            } else {
                System.err.println("TCP client handler socket error: " + socketException.getMessage());
            }
        } catch (Exception e) {
            System.err.println("TCP client handler error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (playerId != -1) {
                gameServer.disconnectClient(playerId);
            }

            try {
                clientSocket.close();
            } catch (Exception ignored) {
            }
        }
    }

    private String sanitizePlayerName(String input) {
        if (input == null || input.isBlank()) {
            return "Player";
        }

        String trimmed = input.trim();
        if (trimmed.length() > 16) {
            return trimmed.substring(0, 16);
        }
        return trimmed;
    }
}