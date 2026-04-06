package client;

import common.GameSnapshot;
import common.PlayerState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudPanel extends JPanel {

    private final ClientState clientState;

    public HudPanel(ClientState clientState) {
        this.clientState = clientState;
        setPreferredSize(new Dimension(280, 700));
        setBackground(new Color(30, 30, 30));
        setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.drawString("ChronoArena", 20, 35);

            GameSnapshot snapshot = clientState.getLatestSnapshot();
            if (snapshot == null) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString("Waiting for game state...", 20, 70);
                return;
            }

            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2.drawString("Player: " + clientState.getLocalPlayerName(), 20, 70);
            g2.drawString("Player ID: " + clientState.getLocalPlayerId(), 20, 95);
            g2.drawString("Time Left: " + snapshot.getTimeLeftSeconds() + "s", 20, 120);

            String matchStatus;
            if (snapshot.isMatchEnded()) {
                matchStatus = "Ended";
            } else if (snapshot.isMatchRunning()) {
                matchStatus = "Running";
            } else {
                matchStatus = "Waiting";
            }

            g2.drawString("Match: " + matchStatus, 20, 145);

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Controls", 20, 185);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.drawString("Move: W A S D / Arrow Keys", 20, 210);
            g2.drawString("Freeze Attack: Space or F", 20, 232);

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Scoreboard", 20, 275);

            List<PlayerState> players = new ArrayList<>(snapshot.getPlayers());
            players.sort(Comparator.comparingInt(PlayerState::getScore).reversed()
                    .thenComparingInt(PlayerState::getPlayerId));

            int y = 305;
            g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
            for (PlayerState player : players) {
                String label = "#" + player.getPlayerId() + " " + player.getPlayerName()
                        + " : " + player.getScore();

                if (player.getPlayerId() == clientState.getLocalPlayerId()) {
                    g2.setColor(new Color(255, 230, 140));
                } else if (!player.isConnected()) {
                    g2.setColor(Color.GRAY);
                } else {
                    g2.setColor(Color.WHITE);
                }

                g2.drawString(label, 20, y);
                y += 24;
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Server Notice", 20, Math.max(460, y + 20));

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            drawWrappedText(
                    g2,
                    snapshot.getServerNotice() == null ? "-" : snapshot.getServerNotice(),
                    20,
                    Math.max(485, y + 45),
                    230,
                    18
            );

        } finally {
            g2.dispose();
        }
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.isBlank()) {
            g2.drawString("-", x, y);
            return;
        }

        FontMetrics metrics = g2.getFontMetrics();
        String[] words = text.split("\\s+");

        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (metrics.stringWidth(candidate) > maxWidth) {
                g2.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                line = new StringBuilder(candidate);
            }
        }

        if (line.length() > 0) {
            g2.drawString(line.toString(), x, currentY);
        }
    }
}