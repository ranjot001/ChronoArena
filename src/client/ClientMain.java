package client;

import javax.swing.*;

public class ClientMain {

    public static void main(String[] args) {
        String propertiesPath = "config/client.properties";
        if (args.length > 0) {
            propertiesPath = args[0];
        }

        GameClient gameClient = new GameClient(propertiesPath);

        Runtime.getRuntime().addShutdownHook(new Thread(gameClient::shutdown));

        try {
            gameClient.connect();

            SwingUtilities.invokeLater(() -> {
                GameFrame frame = new GameFrame(gameClient);
                frame.setVisible(true);
                frame.requestFocusInWindow();
            });

            System.out.println("Connected successfully as player " + gameClient.getClientState().getLocalPlayerId());
        } catch (Exception e) {
            System.err.println("Client failed: " + e.getMessage());
            e.printStackTrace();
            gameClient.shutdown();
        }
    }
}