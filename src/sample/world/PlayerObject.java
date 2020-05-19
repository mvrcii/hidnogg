package sample.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.DataController;
import sample.controllers.KeyController;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.interfaces.InputSystem;



import static sample.enums.AnimationType.*;

public class PlayerObject extends MoveableObject implements InputSystem {

    private final PlayerType playerNumber;
    private SwordObject swordObject;
    boolean canAccelerate;

    private Animation animation = DataController.getInstance().getAnimation(PLAYER_IDLE_LOW);

    public PlayerObject(int x, int y, PlayerType playerNumber, Direction direction) {
        super(x, y, direction);
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
            y=100;
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
        KeyController keyController = KeyController.getInstance();
        DataController animationController = DataController.getInstance();


        if (playerNumber == PlayerType.PLAYER_ONE) {
            if (keyController.isKeyPressed(KeyCode.D)) {
                x += speed * diffMillis / 10;
            }
            if (keyController.isKeyPressed(KeyCode.A)) {
                x -= speed * diffMillis / 10;
            }
            if (keyController.isKeyPressed(KeyCode.S)) {
                if (animation.getAnimationType() == PLAYER_IDLE_MEDIUM) {
                    animation = animationController.getAnimation(PLAYER_IDLE_LOW);
                } else if (animation.getAnimationType() == PLAYER_IDLE_HIGH) {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                } else if (animation.getAnimationType() == PLAYER_IDLE_HOLD_UP) {
                    animation = animationController.getAnimation(PLAYER_IDLE_HIGH);
                }
            }

            if (keyController.isKeyPressed(KeyCode.W)) {
                if (animation.getAnimationType() == PLAYER_IDLE_LOW) {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                } else if (animation.getAnimationType() == PLAYER_IDLE_MEDIUM) {
                    animation = animationController.getAnimation(PLAYER_IDLE_HIGH);
                }
            }
            if (keyController.isKeyPressed(KeyCode.F)) {
                System.out.println(keyController.getKeyPressedTime(KeyCode.D));
                animation = animationController.getAnimation(PLAYER_IDLE_HOLD_UP);
            }
            if (keyController.isKeyPressed(KeyCode.SPACE)){
                if (y == 100) {
                    vy=20;
                    canAccelerate = true;
                }else {
                    if (vy > 30){
                        canAccelerate = false;
                    }
                    if(canAccelerate){
                        vy += (3 * diffMillis / 10);
                    }
                }
                /*
                if(animation.getAnimationType() == PLAYER_JUMP){

                }else{

                }
                */

            }
        } else if (playerNumber == PlayerType.PLAYER_TWO) {
            if (keyController.isKeyPressed(KeyCode.RIGHT)) {
                x += speed * diffMillis / 10;
            }
            if (keyController.isKeyPressed(KeyCode.LEFT)) {
                x -= speed * diffMillis / 10;
            }
            if (keyController.isKeyPressed(KeyCode.UP)) {
                if (animation.getAnimationType() == PLAYER_IDLE_LOW) {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                } else if (animation.getAnimationType() == PLAYER_IDLE_MEDIUM) {
                    animation = animationController.getAnimation(PLAYER_IDLE_HIGH);
                }
            }
            if (keyController.isKeyPressed(KeyCode.DOWN)) {
                if (animation.getAnimationType() == PLAYER_IDLE_MEDIUM) {
                    animation = animationController.getAnimation(PLAYER_IDLE_LOW);
                } else if (animation.getAnimationType() == PLAYER_IDLE_HIGH) {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                }
            }

        }
    }

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
