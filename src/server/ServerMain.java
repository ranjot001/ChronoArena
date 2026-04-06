package server;

public class ServerMain {

    public static void main(String[] args) {
        String propertiesPath = "config/server.properties";
        if (args.length > 0) {
            propertiesPath = args[0];
        }

        GameServer gameServer = new GameServer(propertiesPath);

        Runtime.getRuntime().addShutdownHook(new Thread(gameServer::shutdown));

        try {
            gameServer.start();
        } catch (Exception e) {
            System.err.println("Server failed: " + e.getMessage());
            e.printStackTrace();
            gameServer.shutdown();
        }
    }
}