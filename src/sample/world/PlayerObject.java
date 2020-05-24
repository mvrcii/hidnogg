package sample.world;

import javafx.scene.canvas.GraphicsContext;
import sample.GameLoop;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.DataController;
import sample.controllers.KeyController;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.interfaces.InputSystem;



import static sample.enums.AnimationType.*;

public class PlayerObject extends MoveableObject implements InputSystem {

    private KeyController keyCon;
    private DataController animCon;

    private final KeySet keySet;
    private final PlayerType playerNumber;

    private SwordObject swordObject;
    private boolean canAccelerate;

    private Animation animation = DataController.getInstance().getAnimation(PLAYER_IDLE_LOW);

    public PlayerObject(int x, int y, PlayerType playerNumber, Direction direction, KeySet keySet) {
        super(x, y, direction);
        this.keySet = keySet;
        this.playerNumber = playerNumber;
        this.swordObject = null;
    }


    @Override
    public void update(long diffMillis) {

        animation.update(diffMillis);
        y -= vy * diffMillis / 100;
        if (y < 100){
            vy -= (2*diffMillis/10);    //gravity
        }else{
            vy = 0;
            y= 100;
        }
    }


    @Override
    public void draw(GraphicsContext gc) {
        switch (direction) {
            case LEFT -> FrameData.drawHorizontallyFlipped(gc, animation.getCurrentSprite(), x, y);
            case RIGHT -> gc.drawImage(animation.getCurrentSprite(), x, y);
        }
    }


    @Override
    public void processInput(long diffMillis) {
        keyCon = KeyController.getInstance();
        animCon = DataController.getInstance();

        handleMovementKeys(diffMillis);
        handleUpKey();
        handleDownKey();
        handleJumpKey(diffMillis);
    }

    /**
     * Private Methods to handle key functionalities
     */

    private void handleMovementKeys(double diffMillis) {


        // MOVE RIGHT
        if(keyCon.isKeyPressed(keySet.getMoveRightKey())){
            x += speed * diffMillis / 10;
            System.out.println("right pressed");
        }

        // MOVE LEFT
        if(keyCon.isKeyPressed(keySet.getMoveLeftKey())){
            x -= speed * diffMillis / 10;
            System.out.println("left pressed");
        }
    }

    private void handleUpKey(){
        // CHANGE SWORD POSITION (LOW-HIGH)
        if(keyCon.isKeyPressed(keySet.getUpKey())){
            if (keyCon.getKeyPressedTime(keySet.getUpKey()) > 5){
                animation = animCon.getAnimation(PLAYER_IDLE_HOLD_UP);
            }
        }

        if(keyCon.isKeyReleased(keySet.getUpKey())){
            if(animation.getAnimationType() == PLAYER_IDLE_HOLD_UP){
                animation = animCon.getAnimation(PLAYER_IDLE_LOW);
            }
            if (animation.getAnimationType() == PLAYER_IDLE_LOW) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
            } else if (animation.getAnimationType() == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_HIGH);
            }
        }
    }

    private void handleDownKey() {
        // CROUCH & CHANGE SWORD POSITION
        if(keyCon.isKeyPressed(keySet.getCrouchKey())){
            if (animation.getAnimationType() == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_LOW);
            } else if (animation.getAnimationType() == PLAYER_IDLE_HIGH) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
            } else if (animation.getAnimationType() == PLAYER_IDLE_HOLD_UP) {
                animation = animCon.getAnimation(PLAYER_IDLE_HIGH);
            }
        }
    }

    private void handleJumpKey(double diffMillis) {
        // JUMP
        if(keyCon.isKeyPressed(keySet.getJumpKey())){
            if(!(animation.getAnimationType() == PLAYER_JUMP_PEAK)){
                animation = animCon.getAnimation(PLAYER_JUMP_PEAK);
            }
            if (y == GameLoop.groundLevel) {
                vy=20;
                canAccelerate = true;
            }else {
                if (vy > 30){
                    canAccelerate = false;
                }
                if (canAccelerate){
                    vy += (3 * diffMillis / 10);
                }
            }
        }
        if(keyCon.isKeyReleased(keySet.getJumpKey())){
            System.out.println("released");
            animation = animCon.getAnimation(PLAYER_IDLE_LOW);
        }
    }

    /**
     * Getter and Setter
     */

    public Animation getAnimation() {
        return animation;
    }

    public void setSwordObject(SwordObject swordObject) {
        this.swordObject = swordObject;
    }

    public SwordObject getSwordObject() {
        return swordObject;
    }

    public PlayerType getPlayerNumber() {
        return playerNumber;
    }
}
