package com.vykio.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements KeyListener {

    public InputHandler(Game game) {
        game.addKeyListener(this);
    }

    public class Key {

        //private int numTimesPressed = 0;
        private boolean pressed = false;

        public void toggle(boolean isPressed) {
            pressed = isPressed;
            //if (isPressed) numTimesPressed++;
        }

        public boolean isPressed() {
            return pressed;
        }

        /*public int getNumTimesPressed() {
            return numTimesPressed;
        }*/
    }

    //public List<Key> keys = new ArrayList<Key>();

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);
    }

    public void toggleKey(int keyCode, boolean isPressed) {
        if (keyCode == KeyEvent.VK_Z || keyCode == KeyEvent.VK_UP) { up.toggle(isPressed); }
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) { down.toggle(isPressed); }
        if (keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_LEFT) { left.toggle(isPressed); }
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) { right.toggle(isPressed); }
    }

}
