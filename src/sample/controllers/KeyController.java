package sample.controllers;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.Main;

import java.util.ArrayList;

public class KeyController extends Controller {

    private KeyObject keyObject, previousKeyObject;
    private Canvas canvas;

    private static KeyController instance;

    public static KeyController getInstance() {
        if (instance == null) {
            System.out.println("Key Controller instantiated");
            instance = new KeyController();
        }
        return instance;
    }

    private KeyController() {

        keyObject = new KeyObject();
        previousKeyObject = new KeyObject();
        this.canvas = Main.canvas;

        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!keyObject.keys.contains(keyEvent.getCode())) {
                    keyObject.keys.add(keyEvent.getCode());
                }
            }
        });

        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyObject.keys.contains(keyEvent.getCode())) {
                    keyObject.keys.remove(keyEvent.getCode());
                }
            }
        });
    }

    @Override
    public void update(long diffMillis) {
        previousKeyObject = keyObject;
        keyObject = new KeyObject();
        keyObject.getKeys().addAll(previousKeyObject.getKeys());
    }

    public boolean isKeyPressed(KeyCode keyCode)
    {
        if (!keyObject.getKeys().contains(keyCode))
        {
            return false;
        }

        if (isSinglePressKey(keyCode))
        {
            if(!previousKeyObject.getKeys().contains(keyCode))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    // Keys which are only allowed to activate once per press
    private boolean isSinglePressKey(KeyCode keyCode) {
        switch (keyCode) {
            case W:
                return true;
            case S:
                return true;
            case UP:
                return true;
            case DOWN:
                return true;
            case F:
                return true;
            default:
                return false;
        }
    }

    public static class KeyObject {

        private ArrayList<KeyCode> keys;

        public KeyObject() {
            keys = new ArrayList<>();
        }

        public ArrayList<KeyCode> getKeys() {
            return keys;
        }

    }

    public KeyObject getKeyObject() {
        return keyObject;
    }

}
