package client;

import common.PlayerActionType;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputController extends KeyAdapter {

    private final UdpSender udpSender;
    private final Set<Integer> pressedKeys = new HashSet<>();

    public InputController(UdpSender udpSender) {
        this.udpSender = udpSender;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (pressedKeys.contains(keyCode)) {
            return;
        }
        pressedKeys.add(keyCode);

        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                udpSender.sendAction(PlayerActionType.MOVE_UP);
                break;

            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                udpSender.sendAction(PlayerActionType.MOVE_DOWN);
                break;

            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                udpSender.sendAction(PlayerActionType.MOVE_LEFT);
                break;

            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                udpSender.sendAction(PlayerActionType.MOVE_RIGHT);
                break;

            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_F:
                udpSender.sendAction(PlayerActionType.FREEZE_ATTACK);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                udpSender.sendAction(PlayerActionType.STOP);
                break;

            default:
                break;
        }
    }
}