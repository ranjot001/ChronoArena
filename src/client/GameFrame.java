package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {

    private final GameClient gameClient;
    private final GamePanel gamePanel;
    private final HudPanel hudPanel;

    public GameFrame(GameClient gameClient) {
        this.gameClient = gameClient;
        this.gamePanel = new GamePanel(gameClient.getClientState());
        this.hudPanel = new HudPanel(gameClient.getClientState());

        setTitle("ChronoArena");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        buildLayout();
        attachListeners();
        startRepaintLoop();

        pack();
        setLocationRelativeTo(null);
    }

    private void buildLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(gamePanel, BorderLayout.CENTER);
        root.add(hudPanel, BorderLayout.EAST);

        setContentPane(root);
    }

    private void attachListeners() {
        InputController inputController = new InputController(gameClient.getUdpSender());
        addKeyListener(inputController);
        gamePanel.addKeyListener(inputController);
        setFocusable(true);
        gamePanel.setFocusable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameClient.shutdown();
                dispose();
                System.exit(0);
            }
        });
    }

    private void startRepaintLoop() {
        Timer timer = new Timer(50, e -> {
            gamePanel.repaint();
            hudPanel.repaint();
        });
        timer.start();
    }
}