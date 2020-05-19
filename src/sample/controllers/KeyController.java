package sample.controllers;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.Main;

import java.util.HashMap;

public class KeyController extends Controller {

    private KeyObject keyObject, previousKeyObject;
    private Canvas canvas;

    long time;

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
                if (!keyObject.keys.containsKey(keyEvent.getCode())) {
                    keyObject.keys.put(keyEvent.getCode(), 0L);
                }else{
                    keyObject.keys.replace(keyEvent.getCode(), keyObject.keys.get(keyEvent.getCode())+time);
                }
            }
        });

        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                keyObject.keys.remove(keyEvent.getCode());
            }
        });
    }

    @Override
    public void update(long diffMillis) {
        time = diffMillis;
        //keyObject.print();

        previousKeyObject = keyObject;          // Derzeitiges KeyObject abspeichern
        keyObject = new KeyObject();            // Neues KeyObject erzeugen

        /*HashMap<KeyCode, Long> hashMap = previousKeyObject.getKeyHashMap();
        hashMap.forEach((key, value) ->
        {
            //System.out.println("Value vorher " + value);
            value =  value + diffMillis;
            //System.out.println("Key: "+key+" Value danach: "+value);
        });*/

        keyObject.getKeyHashMap().putAll(previousKeyObject.getKeyHashMap());    // Alle vorherigen Keys in das neue KeyObject kopieren
    }

    public boolean isKeyPressed(KeyCode keyCode)
    {
        if (!keyObject.getKeyHashMap().containsKey(keyCode))
        {
            return false;
        }

        if (isSinglePressKey(keyCode))
        {
            return !previousKeyObject.getKeyHashMap().containsKey(keyCode);
        }
        return true;
    }

    // Keys which are only allowed to activate once per press
    private boolean isSinglePressKey(KeyCode keyCode) {
        return switch (keyCode) {
            case W, S, UP, DOWN, F -> true;
            default -> false;
        };
    }

    public static class KeyObject {

        private final HashMap<KeyCode, Long> keys;

        public KeyObject() {
            keys = new HashMap<>();
        }

        public HashMap<KeyCode, Long> getKeyHashMap() {
            return keys;
        }

        public void print(){
            keys.forEach((key, value) -> System.out.println("Key: "+key+" Value: "+value));
        }

    }

    public long getKeyPressedTime(KeyCode keyCode){
        return keyObject.getKeyHashMap().get(keyCode);
    }

}
