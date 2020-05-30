package sample.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import sample.GameLoop;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.CameraController;
import sample.controllers.CollisionController;
import sample.controllers.DataController;
import sample.controllers.KeyController;
import sample.enums.AnimationType;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.interfaces.InputSystem;

import javafx.scene.paint.Color;
import java.util.ArrayList;

import static sample.enums.AnimationType.*;

public class PlayerObject extends MoveableObject implements InputSystem {

    private final KeyController keyCon = KeyController.getInstance();
    private final DataController animCon = DataController.getInstance();

    private final KeySet keySet;
    private final PlayerType playerNumber;
    private SwordObject swordObject;

    private boolean canAccelerate;
    private boolean onGround;

    private Animation animation = DataController.getInstance().getAnimation(PLAYER_IDLE_LOW);
    private Animation lastIdleAnimation = DataController.getInstance().getAnimation(PLAYER_IDLE_LOW);

    public PlayerObject(int x, int y, PlayerType playerNumber, Direction direction, KeySet keySet) {
        super(x, y, direction);
        this.keySet = keySet;
        this.playerNumber = playerNumber;
        this.swordObject = null;
        this.onGround = true;
    }


    @Override
    public void update(long diffMillis) {
        int playerOffset = CollisionController.getInstance().getPlayersWidthHeight()[1];

        animation.update(diffMillis);

        y -= vy * diffMillis / 100;
        if (y < GameLoop.currentLevel.getGroundLevel() - playerOffset) {
            vy -= ((2 * (double) diffMillis) / 10);    //gravity
        } else {
            vy = 0;
            y = GameLoop.currentLevel.getGroundLevel() - playerOffset;
        }
    }


    @Override
    public void draw(GraphicsContext gc) {
        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
        switch (direction) {
            case LEFT -> FrameData.drawHorizontallyFlipped(gc, animation.getCurrentSprite(), (int) drawPoint.getX(), (int) drawPoint.getY());
            case RIGHT -> gc.drawImage(animation.getCurrentSprite(), drawPoint.getX(), drawPoint.getY());
        }
        //this.showHitBoxState(gc, 1);
        //this.showHitBoxState(gc, 2);
        //this.showHitBoxState(gc, 3);
    }


    @Override
    public void processInput(long diffMillis) {
        onGround = CollisionController.getInstance().getPlayerOnGround(this.playerNumber);
        double t_holdUp = 200;
        double t_crouch = 200;

        handleMovementKeys(diffMillis);
        handleUpKey(t_holdUp);
        handleDownKey(t_crouch);
        handleStabKey();
        handleJumpKey(diffMillis);
    }

    private void handleStabKey() {
        // TODO: Stab animation missing. Needs still implementation!
        if (keyCon.isKeyPressed(keySet.getStabKey())) {
            switch(animation.getAnimationType()){
                case PLAYER_IDLE_LOW -> animation = animCon.getAnimation(PLAYER_STAB_LOW);
                case PLAYER_IDLE_MEDIUM -> animation = animCon.getAnimation(PLAYER_STAB_MEDIUM);
                case PLAYER_IDLE_HIGH -> animation = animCon.getAnimation(PLAYER_STAB_HIGH);
            }
        }
        if (keyCon.isKeyReleased(keySet.getStabKey())) {

        }
    }


    private void handleMovementKeys(long diffMillis) {
        // MOVE RIGHT
        if (keyCon.isKeyPressed(keySet.getMoveRightKey())) {
            x += speed * diffMillis / 10;

            if(animation.getAnimationType() != PLAYER_WALK && onGround){
                lastIdleAnimation = animation;
                animation = animCon.getAnimation(PLAYER_WALK);
            }

        }
        // MOVE LEFT
        if (keyCon.isKeyPressed(keySet.getMoveLeftKey())) {
            x -= speed * diffMillis / 10;

            /*
            if(animation.getAnimationType() != PLAYER_WALK && onGround){
                animation = animCon.getAnimation(PLAYER_WALK);
            }
            */

        }

        if(keyCon.isKeyReleased(keySet.getMoveLeftKey()) || keyCon.isKeyReleased(keySet.getMoveRightKey())){
            animation = lastIdleAnimation;
        }
    }


    /**
     * Controls upKey
     * @param t_holdUp = fixed delta time (ms) for holdUp
     *        t_pressed = = pressed time in ms
     *
     * Handling keyPressed:
     *                 (t_pressed > t_holdUp)           --> PLAYER_IDLE_HOLD_UP
     *
     * Handling keyReleased:
     *                 animation == PLAYER_IDLE_LOW     --> PLAYER_IDLE_MEDIUM
     *                 animation == PLAYER_IDLE_MEDIUM  --> PLAYER_IDLE_HIGH
     *                 animation == PLAYER_HOLD_UP      --> PLAYER_IDLE_LOW
     */

    private void handleUpKey(double t_holdUp) {
        if (keyCon.isKeyPressed(keySet.getUpKey())) {
            double t_pressed = keyCon.getKeyPressedTime(keySet.getUpKey());
            if(t_pressed > t_holdUp){
                if(animation.getAnimationType() != PLAYER_IDLE_HOLD_UP){
                    lastIdleAnimation = animation;
                }
                animation = animCon.getAnimation(PLAYER_IDLE_HOLD_UP);
            }
        }

        if (keyCon.isKeyReleased(keySet.getUpKey())){
            AnimationType animType = animation.getAnimationType();
            if (animType == PLAYER_IDLE_LOW) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
            } else if (animType == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_HIGH);
            } else if (animType == PLAYER_IDLE_HOLD_UP) {
                animation = lastIdleAnimation;
                //animation = animCon.getAnimation(PLAYER_IDLE_LOW);
            }
        }
    }


    /**
     * Controls downKey
     * @param t_crouch = fixed delta time (ms) for crouching
     *        t_pressed = = pressed time in ms
     *
     * Handling keyPressed:
     *                 (t_pressed > t_crouch)           --> PLAYER_CROUCH
     *
     * Handling keyReleased:
     *                 animation == PLAYER_IDLE_HIGH    --> PLAYER_IDLE_MEDIUM
     *                 animation == PLAYER_IDLE_MEDIUM  --> PLAYER_IDLE_LOW
     *                 animation == PLAYER_CROUCH       --> PLAYER_IDLE_LOW
     */

    private void handleDownKey(double t_crouch) {

        if(keyCon.isKeyPressed(keySet.getDownKey())){
            double t_pressed = keyCon.getKeyPressedTime(keySet.getDownKey());
            if(t_pressed > t_crouch){
                //animation = animCon.getAnimation(PLAYER_CROUCH);
            }
        }

        if(keyCon.isKeyPressed(keySet.getDownKey())){
            AnimationType animType = animation.getAnimationType();
            if (animType == PLAYER_IDLE_HIGH) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
            } else if (animType == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_LOW);
            } //else if (animType == PLAYER_CROUCH) {
            // animation = animCon.getAnimation(PLAYER_IDLE_LOW);
            //}
        }

        // CROUCH & CHANGE SWORD POSITION
        if (keyCon.isKeyPressed(keySet.getDownKey())) {
            if (animation.getAnimationType() == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_LOW);
            } else if (animation.getAnimationType() == PLAYER_IDLE_HIGH) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
            } else if (animation.getAnimationType() == PLAYER_IDLE_HOLD_UP) {
                animation = animCon.getAnimation(PLAYER_IDLE_HIGH);
            }
        }
    }


    private void handleJumpKey(long diffMillis) {


        if (keyCon.isKeyPressed(keySet.getJumpKey())) {

            System.out.println(onGround);
            if (onGround) {
                animation = animCon.getAnimation(PLAYER_JUMP_START);
                vy = 20;
                canAccelerate = true;
            } else {
                if(vy >= 30){
                    canAccelerate = false;
                }
                if(canAccelerate){
                    vy += ((3 * (double) diffMillis) / 10);
                }
            }
        }else{
            canAccelerate = false;
        }
        if(animation.getAnimationType() == PLAYER_JUMP_START && vy >= 20){
            animation = animCon.getAnimation(PLAYER_JUMP_PEAK);
        }
        if(animation.getAnimationType() == PLAYER_JUMP_PEAK && vy <= -15){
            animation = animCon.getAnimation(PLAYER_JUMP_END);
        }
        if(animation.getAnimationType() == PLAYER_JUMP_END && vy <= 1 && vy >= -1){
            animation = animCon.getAnimation(PLAYER_IDLE_LOW);
        }

    }

    /**
     * Test method >> can be removed
     */
    private void showHitBoxState(GraphicsContext gc, int testId) {
        int[] playerWidthHeight = CollisionController.getInstance().getPlayersWidthHeight();

        switch (testId) {
            // TESTING swordPoints ----------------------------------------------------------------------------------------------------
            case 1 -> {
                int gripX;
                int gripY;
                int swordLength = CollisionController.getInstance().getSwordLength();
                if (this.direction == Direction.RIGHT) {
                    gripX = (int) this.getAnimation().getCurrentFrame().getSwordStartPoint().getX();
                    gripY = (int) this.getAnimation().getCurrentFrame().getSwordStartPoint().getY();
                } else {
                    gripX = (int) this.getAnimation().getCurrentFrame().getSwordStartPointInverted().getX() - CollisionController.getInstance().getPlayersWidthHeight()[0];
                    gripY = (int) this.getAnimation().getCurrentFrame().getSwordStartPointInverted().getY();
                    swordLength *= (-1);
                }
                if (CollisionController.getInstance().getSwordsHitting())
                    System.out.println("SWORDS COLLIDING");
                if (CollisionController.getInstance().getPlayer1HitPlayer2() && this.playerNumber == PlayerType.PLAYER_ONE) // Testing player1_hit_player2
                    System.out.println("PLAYER1 HIT DETECTED");
                gc.setFill(Color.GREEN); // SwordMount
                gc.fillRect(this.x + gripX, this.y + gripY, 8, 8);
                gc.setFill(Color.PINK); // SwordTip
                gc.fillRect(this.x + gripX + swordLength, this.y + gripY, 8, 8);
            }
            // TESTING rectangleHitBox ----------------------------------------------------------------------------------------------------
            case 2 -> {
                gc.setStroke(Color.GREEN);
                boolean playerOnGround = CollisionController.getInstance().getPlayerOnGround(this.playerNumber);
                boolean playerHitsWall = CollisionController.getInstance().getPlayerHitsWall(this.playerNumber);
                if (playerOnGround && playerHitsWall)
                    gc.setStroke(Color.BLACK);
                else if (playerOnGround)
                    gc.setStroke(Color.RED);
                else if (playerHitsWall)
                    gc.setStroke(Color.BLUE);
                else
                    gc.setStroke(Color.GREEN);
                Point2D[] playerXY = CollisionController.getInstance().getRectHitBoxP1_P2();
                gc.strokeRect(this.x + playerXY[0].getX(), this.y + playerXY[0].getY(), playerWidthHeight[0], playerWidthHeight[1]);
            }
            // TESTING outLineHitBox ----------------------------------------------------------------------------------------------------
            case 3 -> {
                gc.setFill(Color.ORANGE);
                ArrayList<Point2D> hitBox;
                int offset = 0;
                if (this.direction == Direction.RIGHT) {
                    hitBox = this.getAnimation().getCurrentFrame().getHitBox();
                } else {
                    hitBox = this.getAnimation().getCurrentFrame().getHitBoxInverted();
                    offset = playerWidthHeight[0] + 2;
                }
                for (Point2D point : hitBox) {
                    gc.fillRect(this.x + point.getX() - offset, this.y + point.getY(), 2, 2);
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

