package client;

import common.PlayerActionType;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputController extends KeyAdapter {

    private final UdpSender udpSender;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Timer movementTimer;

    public InputController(UdpSender udpSender) {
        this.udpSender = udpSender;
        this.movementTimer = new Timer(60, e -> sendCurrentMovement());
        this.movementTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);

        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_F) {
            udpSender.sendAction(PlayerActionType.FREEZE_ATTACK);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());

        if (isMovementKey(e.getKeyCode()) && !hasMovementKeyPressed()) {
            udpSender.sendAction(PlayerActionType.STOP);
        }
    }

    private void sendCurrentMovement() {
        if (pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_UP)) {
            udpSender.sendAction(PlayerActionType.MOVE_UP);
            return;
        }
        if (pressedKeys.contains(KeyEvent.VK_S) || pressedKeys.contains(KeyEvent.VK_DOWN)) {
            udpSender.sendAction(PlayerActionType.MOVE_DOWN);
            return;
        }
        if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT)) {
            udpSender.sendAction(PlayerActionType.MOVE_LEFT);
            return;
        }
        if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            udpSender.sendAction(PlayerActionType.MOVE_RIGHT);
            return;
        }
    }

    private boolean isMovementKey(int keyCode) {
        return keyCode == KeyEvent.VK_W ||
                keyCode == KeyEvent.VK_A ||
                keyCode == KeyEvent.VK_S ||
                keyCode == KeyEvent.VK_D ||
                keyCode == KeyEvent.VK_UP ||
                keyCode == KeyEvent.VK_DOWN ||
                keyCode == KeyEvent.VK_LEFT ||
                keyCode == KeyEvent.VK_RIGHT;
    }

    private boolean hasMovementKeyPressed() {
        for (Integer key : pressedKeys) {
            if (isMovementKey(key)) {
                return true;
            }
        }
        return false;
    }
}