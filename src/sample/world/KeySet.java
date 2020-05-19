package sample.world;

import javafx.scene.input.KeyCode;


public class KeySet {

    private final KeyCode moveLeftKey;
    private final KeyCode moveRightKey;

    private final KeyCode crouchKey;
    private final KeyCode upKey;

    private final KeyCode holdUpKey;
    private final KeyCode jumpKey;


    public KeySet(KeyCode moveLeftKey, KeyCode moveRightKey, KeyCode crouchKey, KeyCode upKey, KeyCode holdUpKey, KeyCode jumpKey){
        this.moveLeftKey = moveLeftKey;
        this.moveRightKey = moveRightKey;
        this.crouchKey = crouchKey;
        this.holdUpKey = holdUpKey;
        this.jumpKey = jumpKey;
        this.upKey = upKey;
    }


    public KeyCode getMoveLeftKey() {
        return moveLeftKey;
    }

    public KeyCode getMoveRightKey() {
        return moveRightKey;
    }

    public KeyCode getCrouchKey() {
        return crouchKey;
    }

    public KeyCode getHoldUpKey() {
        return holdUpKey;
    }

    public KeyCode getJumpKey() {
        return jumpKey;
    }

    public KeyCode getUpKey() {
        return upKey;
    }
}
