package client;

import common.Constants;
import common.GameSnapshot;
import common.ItemState;
import common.ItemType;
import common.PlayerState;
import common.Position;
import common.ZoneControlState;
import common.ZoneStateModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel {

    private final ClientState clientState;

    public GamePanel(ClientState clientState) {
        this.clientState = clientState;
        setPreferredSize(new Dimension(Constants.MAP_WIDTH, Constants.MAP_HEIGHT));
        setBackground(new Color(20, 20, 20));
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            enableAntialiasing(g2);

            GameSnapshot snapshot = clientState.getLatestSnapshot();
            if (snapshot == null) {
                drawCenteredMessage(g2, "Waiting for snapshot...");
                return;
            }

            drawArenaGrid(g2);
            drawZones(g2, snapshot.getZones());
            drawItems(g2, snapshot.getItems());
            drawPlayers(g2, snapshot.getPlayers());

            if (snapshot.isMatchEnded()) {
                drawOverlayMessage(g2, "Match Ended");
            } else if (!snapshot.isMatchRunning()) {
                drawOverlayMessage(g2, "Waiting for players...");
            }

        } finally {
            g2.dispose();
        }
    }

    private void enableAntialiasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private void drawArenaGrid(Graphics2D g2) {
        g2.setColor(new Color(40, 40, 40));

        for (int x = 0; x <= Constants.MAP_WIDTH; x += 50) {
            g2.drawLine(x, 0, x, Constants.MAP_HEIGHT);
        }

        for (int y = 0; y <= Constants.MAP_HEIGHT; y += 50) {
            g2.drawLine(0, y, Constants.MAP_WIDTH, y);
        }
    }

    private void drawZones(Graphics2D g2, List<ZoneStateModel> zones) {
        for (ZoneStateModel zone : zones) {
            Color fill;
            switch (zone.getState()) {
                case CONTROLLED:
                    fill = new Color(80, 180, 100, 120);
                    break;
                case CAPTURING:
                    fill = new Color(80, 160, 220, 120);
                    break;
                case CONTESTED:
                    fill = new Color(220, 160, 60, 140);
                    break;
                case UNCLAIMED:
                default:
                    fill = new Color(120, 120, 120, 90);
                    break;
            }

            g2.setColor(fill);
            g2.fillRect(zone.getX(), zone.getY(), zone.getWidth(), zone.getHeight());

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(zone.getX(), zone.getY(), zone.getWidth(), zone.getHeight());

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString(zone.getZoneId(), zone.getX() + 10, zone.getY() + 20);

            String ownerText = "Owner: " + (zone.getOwnerPlayerId() == null ? "-" : "#" + zone.getOwnerPlayerId());
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString(ownerText, zone.getX() + 10, zone.getY() + 40);

            g2.drawString("State: " + zone.getState().name(), zone.getX() + 10, zone.getY() + 58);
        }
    }

    private void drawItems(Graphics2D g2, List<ItemState> items) {
        for (ItemState item : items) {
            Position p = item.getPosition();
            if (p == null) {
                continue;
            }

            if (item.getItemType() == ItemType.ENERGY) {
                g2.setColor(new Color(255, 220, 60));
                g2.fillOval(p.getX() - 10, p.getY() - 10, 20, 20);
                g2.setColor(Color.BLACK);
                g2.drawString("E", p.getX() - 4, p.getY() + 5);
            } else {
                g2.setColor(new Color(130, 220, 255));
                g2.fillOval(p.getX() - 10, p.getY() - 10, 20, 20);
                g2.setColor(Color.BLACK);
                g2.drawString("F", p.getX() - 4, p.getY() + 5);
            }
        }
    }

    private void drawPlayers(Graphics2D g2, List<PlayerState> players) {
        for (PlayerState player : players) {
            Position pos = player.getPosition();
            if (pos == null) {
                continue;
            }

            boolean isLocal = player.getPlayerId() == clientState.getLocalPlayerId();

            if (!player.isConnected()) {
                g2.setColor(new Color(120, 120, 120));
            } else if (player.isFrozen()) {
                g2.setColor(new Color(100, 180, 255));
            } else if (isLocal) {
                g2.setColor(new Color(255, 200, 70));
            } else {
                g2.setColor(new Color(200, 90, 120));
            }

            g2.fillRoundRect(
                    pos.getX(),
                    pos.getY(),
                    Constants.PLAYER_SIZE,
                    Constants.PLAYER_SIZE,
                    10,
                    10
            );

            g2.setColor(Color.WHITE);
            g2.drawRoundRect(
                    pos.getX(),
                    pos.getY(),
                    Constants.PLAYER_SIZE,
                    Constants.PLAYER_SIZE,
                    10,
                    10
            );

            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(
                    player.getPlayerName() + " (#" + player.getPlayerId() + ")",
                    pos.getX() - 10,
                    pos.getY() - 8
            );

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString(
                    "Score: " + player.getScore(),
                    pos.getX() - 4,
                    pos.getY() + Constants.PLAYER_SIZE + 15
            );

            if (player.isFreezePowerAvailable()) {
                g2.setColor(new Color(120, 240, 255));
                g2.drawString("Freeze Ready", pos.getX() - 4, pos.getY() + Constants.PLAYER_SIZE + 29);
            } else if (player.getFreezeCooldownUntilEpochMs() > System.currentTimeMillis()) {
                g2.setColor(new Color(190, 190, 255));
                g2.drawString("Cooldown", pos.getX() - 4, pos.getY() + Constants.PLAYER_SIZE + 29);
            }
        }
    }

    private void drawCenteredMessage(Graphics2D g2, String text) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;
        g2.drawString(text, x, y);
    }

    private void drawOverlayMessage(Graphics2D g2, String text) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(getWidth() / 2 - 180, getHeight() / 2 - 50, 360, 90, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2 + 10;
        g2.drawString(text, x, y);
    }
}