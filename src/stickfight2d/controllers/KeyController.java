package stickfight2d.controllers;

import javafx.scene.input.KeyCode;
import stickfight2d.Main;
import stickfight2d.misc.Config;
import stickfight2d.misc.Debugger;
import stickfight2d.misc.KeySet;
import stickfight2d.world.PlayerObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class KeyController extends Controller {

    private KeyObject keyObject, previousKeyObject;

    private boolean keyPressBlockedP1 = false;
    private boolean keyPressBlockedP2 = false;

    private static KeyController instance;

    public static KeyController getInstance() {
        if (instance == null) {
            Debugger.log("Key Controller instantiated");
            instance = new KeyController();
        }
        return instance;
    }

    private KeyController() {
        keyObject = new KeyObject();
        previousKeyObject = new KeyObject();

        Main.canvas.setOnKeyPressed(keyEvent -> {
            if(!keyPressBlockedP2 && Config.keySet2.containsKeyCode(keyEvent.getCode())){
                if (!keyObject.keys.containsKey(keyEvent.getCode())) {
                    keyObject.keys.put(keyEvent.getCode(), 0L);
                }
            }else if(!keyPressBlockedP1 && Config.keySet1.containsKeyCode(keyEvent.getCode())){
                if (!keyObject.keys.containsKey(keyEvent.getCode())) {
                    keyObject.keys.put(keyEvent.getCode(), 0L);
                }
            }else if(!keyPressBlockedP2 && !keyPressBlockedP1){
                if (!keyObject.keys.containsKey(keyEvent.getCode())) {
                    keyObject.keys.put(keyEvent.getCode(), 0L);
                }
            }
        });

        Main.canvas.setOnKeyReleased(keyEvent -> keyObject.keys.remove(keyEvent.getCode()));
    }

    @Override
    public void update(long diffMillis) {

        previousKeyObject = keyObject;          // Derzeitiges KeyObject abspeichern
        keyObject = new KeyObject();            // Neues KeyObject erzeugen

        keyObject.getKeyHashMap().putAll(previousKeyObject.getKeyHashMap());    // Alle vorherigen Keys in das neue KeyObject kopieren

        keyObject.keys.replaceAll((c, v) -> keyObject.keys.get(c) + diffMillis);

    }

    public boolean isKeyPressed(KeyCode keyCode) {
        if (!keyObject.getKeyHashMap().containsKey(keyCode)) {
            return false;
        }

        if (isSinglePressKey(keyCode)) {
            return !previousKeyObject.getKeyHashMap().containsKey(keyCode);
        }
        return true;
    }

    // Keys which are only allowed to activate once per press
    private boolean isSinglePressKey(KeyCode keyCode) {
        return switch (keyCode) {
            case SPACE, ENTER, F, N -> true;
            default -> false;
        };
    }

    public static class KeyObject {

        private final ConcurrentHashMap<KeyCode, Long> keys;

        public KeyObject() {
            keys = new ConcurrentHashMap<>();
        }

        public ConcurrentHashMap<KeyCode, Long> getKeyHashMap() {
            return keys;
        }

    }

    public boolean isKeyReleased(KeyCode keyCode){
        return !keyObject.getKeyHashMap().containsKey(keyCode) && previousKeyObject.getKeyHashMap().containsKey(keyCode);
    }

    public long getKeyPressedTime(KeyCode keyCode){
        if(keyObject.getKeyHashMap().containsKey(keyCode)){
            return keyObject.getKeyHashMap().get(keyCode);
        }
        return 0;
    }

    public void removeKeyPress(KeyCode keycode){
        previousKeyObject.getKeyHashMap().remove(keycode);
        keyObject.getKeyHashMap().remove(keycode);
    }

    public void removeAllKeyPress(){
        keyObject.getKeyHashMap().clear();
    }

    public void removePlayerKeyPress(PlayerObject playerObject) {
        ArrayList<KeyCode> keyCodes = playerObject.getKeySet().getKeyCodes();
        for (KeyCode keyCode : keyCodes) {
            previousKeyObject.getKeyHashMap().remove(keyCode);
            keyObject.getKeyHashMap().remove(keyCode);
        }
    }

    public void setKeyPressBlockedP2(boolean keyPressBlockedP2) {
        System.out.println("Player2 KeyBlock: "+keyPressBlockedP2);
        this.keyPressBlockedP2 = keyPressBlockedP2;
    }

    public void setKeyPressBlockedP1(boolean keyPressBlockedP1) {
        System.out.println("Player1 KeyBlock: "+keyPressBlockedP1);
        this.keyPressBlockedP1 = keyPressBlockedP1;
    }
}
