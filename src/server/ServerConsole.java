package server;

import java.util.Scanner;

public class ServerConsole implements Runnable {

    private final GameServer gameServer;
    private volatile boolean running = true;

    public ServerConsole(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        printHelp();

        while (running) {
            try {
                System.out.print("server> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String line = scanner.nextLine();
                if (line == null) {
                    continue;
                }

                handleCommand(line.trim());
            } catch (Exception e) {
                System.err.println("Server console error: " + e.getMessage());
            }
        }
    }

    private void handleCommand(String line) {
        if (line.isBlank()) {
            return;
        }

        String[] parts = line.split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "help":
                printHelp();
                break;

            case "players":
                gameServer.printConnectedPlayers();
                break;

            case "kill":
                if (parts.length < 2) {
                    System.out.println("Usage: kill <playerId>");
                    return;
                }
                try {
                    int playerId = Integer.parseInt(parts[1]);
                    gameServer.killPlayer(playerId);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid playerId");
                }
                break;

            case "shutdown":
                System.out.println("Shutting down server...");
                running = false;
                gameServer.shutdown();
                break;

            default:
                System.out.println("Unknown command. Type 'help'");
        }
    }

    private void printHelp() {
        System.out.println("Server console commands:");
        System.out.println("  help       -> show commands");
        System.out.println("  players    -> list connected players");
        System.out.println("  kill <id>  -> kill a client");
        System.out.println("  shutdown   -> stop server");
    }
}