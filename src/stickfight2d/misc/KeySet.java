package stickfight2d.misc;

import javafx.scene.input.KeyCode;

import java.util.ArrayList;


public class KeySet {



    private final KeyCode moveLeftKey;
    private final KeyCode moveRightKey;

    private final KeyCode downKey;
    private final KeyCode upKey;

    private final KeyCode stabKey;
    private final KeyCode jumpKey;

    private final ArrayList<KeyCode> keyCodes = new ArrayList<>();


    public KeySet(KeyCode moveLeftKey, KeyCode moveRightKey, KeyCode downKey, KeyCode upKey, KeyCode stabKey, KeyCode jumpKey){
        this.moveLeftKey = moveLeftKey;
        this.moveRightKey = moveRightKey;
        this.downKey = downKey;
        this.stabKey = stabKey;
        this.jumpKey = jumpKey;
        this.upKey = upKey;
        keyCodes.add(moveLeftKey);
        keyCodes.add(moveRightKey);
        keyCodes.add(downKey);
        keyCodes.add(stabKey);
        keyCodes.add(jumpKey);
        keyCodes.add(upKey);
    }



    public KeyCode getMoveLeftKey() {
        return moveLeftKey;
    }

    public KeyCode getMoveRightKey() {
        return moveRightKey;
    }

    public KeyCode getDownKey() {
        return downKey;
    }

    public KeyCode getStabKey() {
        return stabKey;
    }

    public KeyCode getJumpKey() {
        return jumpKey;
    }

    public KeyCode getUpKey() {
        return upKey;
    }

    public boolean containsKeyCode(KeyCode keyCode){
        return keyCodes.contains(keyCode);
    }
}
