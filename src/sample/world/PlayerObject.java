package sample.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sample.GameLoop;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.*;
import sample.enums.AnimationType;
import sample.enums.DirectionType;
import sample.enums.PlayerType;
import sample.interfaces.InputSystem;

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
    private boolean alive;

    private double time_passed = 0;

    private Animation animation = DataController.getInstance().getAnimation(PLAYER_IDLE_LOW);
    private AnimationType lastIdleAnimationType = PLAYER_IDLE_LOW;

    public PlayerObject(int x, int y, PlayerType playerNumber, DirectionType directionType, KeySet keySet) {
        super(x, y, directionType);
        this.keySet = keySet;
        this.playerNumber = playerNumber;
        this.swordObject = new SwordObject(this.x, this.y, DirectionType.RIGHT, this);
        GameLoop.currentLevel.addSword(swordObject);

        this.onGround = true;
        this.alive = true;
    }

    /*
    public void reset(){
        onGround = true;
        alive = true;
        animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
        swordObject = new SwordObject(this.x, this.y, DirectionType.RIGHT, this);
        GameLoop.currentLevel.addSword(swordObject);
    }
     */

    @Override
    public void update(long diffMillis) {
        int playerOffset = CollisionController.getInstance().getPlayersWidthHeight()[1];

        animation.update(diffMillis);

        y -= vy * diffMillis / 100;
        if (y < GameLoop.currentLevel.getGroundLevel() - playerOffset) {
            vy -= (2 * (double) diffMillis) / 10;    //gravity
        } else {
            vy = 0;
            y = GameLoop.currentLevel.getGroundLevel() - playerOffset;
        }
    }


    @Override
    public void draw(GraphicsContext gc) {
        // TODO: Implement the usage of the world coordinates
        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
        switch (directionType) {
            case LEFT -> FrameData.drawHorizontallyFlipped(gc, animation.getCurrentSprite(), (int) drawPoint.getX(), (int) drawPoint.getY());
            case RIGHT -> gc.drawImage(animation.getCurrentSprite(), drawPoint.getX(), drawPoint.getY());
        }
        this.showHitBoxState(gc, 1);
        this.showHitBoxState(gc, 2);
        this.showHitBoxState(gc, 3);
    }


    @Override
    public void processInput(long diffMillis) {
        checkCollisions();

        //handleDeathAnimation(diffMillis);
        handleMovementKeys(diffMillis);
        handleUpKey();
        handleDownKey();
        handleStabKey();
        handleJumpKey(diffMillis);
    }

    private void checkCollisions() {
        CollisionController colCon = CollisionController.getInstance();

        onGround = colCon.getPlayerOnGround(this.playerNumber);

        /*
        // Player getting hit by other player
        if(colCon.getPlayerHit(this.playerNumber) && alive){
            alive = false;
            time_passed = 0;
            keyCon.setKeyPressBlockedP1(true);
            keyCon.removeAllKeyPress();

            if(swordObject != null){    // only if PLAYER has a sword
                swordObject.fallToGround();
                this.swordObject = null;
            }
            animation = animCon.getAnimation(PLAYER_DYING);

            //System.out.println("started player dieing animation");
        }
        */


    }

    /*
    private void handleDeathAnimation(double diffMillis) {
        if(animation.isLastFrame() && animation.getAnimationType() == PLAYER_DYING){
           animation.stop();
        }
        if(!alive){
            time_passed += diffMillis;
            if(time_passed > Config.T_RESPAWN){
                GameLoop.currentLevel.respawnPlayer(this);
            }
        }
    }
    */


    private void handleStabKey() {

        if (keyCon.isKeyPressed(keySet.getStabKey())) {
            switch (animation.getAnimationType()) {
                case PLAYER_IDLE_LOW, PLAYER_IDLE_MEDIUM, PLAYER_IDLE_HIGH -> animation = animCon.getStabAnim(lastIdleAnimationType);
                case PLAYER_WALK -> {
                    // Manual control is usually turned on while walking to the left / right
                    // by setting it to false here, the player always stabs towards his enemy
                    DirectionController.getInstance().setManualControl(this, false);
                    animation = animCon.getStabAnim(lastIdleAnimationType);
                    switch (directionType) {
                        case LEFT -> keyCon.removeKeyPress(keySet.getMoveLeftKey());
                        case RIGHT -> keyCon.removeKeyPress(keySet.getMoveRightKey());
                    }
                }
            }
        }

        if (animation.isLastFrame()) {
            switch (animation.getAnimationType()) {
                case PLAYER_STAB_LOW, PLAYER_STAB_HIGH, PLAYER_STAB_MEDIUM -> animation = animCon.getAnimation(lastIdleAnimationType);
            }

        }

    }


    private void handleMovementKeys(long diffMillis) {

        // RIGHT
        if (keyCon.isKeyPressed(keySet.getMoveRightKey()) && !keyCon.isKeyPressed(keySet.getMoveLeftKey())) {
            x += speed * diffMillis / 10;
            double t_pressed = keyCon.getKeyPressedTime(keySet.getMoveRightKey());

            if (animation.getAnimationType() != PLAYER_WALK && onGround) {
                DirectionController.getInstance().setManualControl(this, true);
                directionType = DirectionType.RIGHT;

                if(t_pressed > 100){
                    animation = animCon.getAnimation(PLAYER_WALK);
                }else{
                    //animation = animCon.getAnimation(PLAYER_STEP);
                }
            }
        }

        // LEFT
        if (keyCon.isKeyPressed(keySet.getMoveLeftKey()) && !keyCon.isKeyPressed(keySet.getMoveRightKey())) {
            x -= speed * diffMillis / 10;
            double t_pressed = keyCon.getKeyPressedTime(keySet.getMoveLeftKey());

            if (animation.getAnimationType() != PLAYER_WALK && onGround) {
                DirectionController.getInstance().setManualControl(this, true);
                directionType = DirectionType.LEFT;

                if(t_pressed > 100){
                    animation = animCon.getAnimation(PLAYER_WALK);
                }else{
                    //animation = animCon.getAnimation(PLAYER_STEP);
                }
            }
        }

        if (keyCon.isKeyPressed(keySet.getMoveLeftKey()) && keyCon.isKeyPressed(keySet.getMoveRightKey())){
            if(animation.getAnimationType() != lastIdleAnimationType){
                animation = animCon.getAnimation(lastIdleAnimationType);
            }
        }

        if (keyCon.isKeyReleased(keySet.getMoveLeftKey()) || keyCon.isKeyReleased(keySet.getMoveRightKey())) {
            DirectionController.getInstance().setManualControl(this, false);
            animation = animCon.getAnimation(lastIdleAnimationType);
        }
    }


    /**
     * Controls upKey
     *        T_HOLDUP = fixed delta time (ms) for holdUp
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

    private void handleUpKey() {
        if (keyCon.isKeyPressed(keySet.getUpKey())) {
            double t_pressed = keyCon.getKeyPressedTime(keySet.getUpKey());
            if(t_pressed > Config.T_HOLDUP){
                animation = animCon.getAnimation(PLAYER_IDLE_HOLD_UP);
            }
        }

        if (keyCon.isKeyReleased(keySet.getUpKey())){
            AnimationType animType = animation.getAnimationType();
            if (animType == PLAYER_IDLE_LOW) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
                lastIdleAnimationType = animation.getAnimationType();
            } else if (animType == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_HIGH);
                lastIdleAnimationType = animation.getAnimationType();
            } else if (animType == PLAYER_IDLE_HOLD_UP) {
                animation = animCon.getAnimation(lastIdleAnimationType);
            }
        }
    }


    /**
     * Controls downKey
     *        T_CROUCH = fixed delta time (ms) for crouching
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

    private void handleDownKey() {
        // TODO: Needs implementation for crouching
        if(keyCon.isKeyPressed(keySet.getDownKey())){
            double t_pressed = keyCon.getKeyPressedTime(keySet.getDownKey());
            if(t_pressed > Config.T_CROUCH){
                /* // TODO: Activate as soon as PLAYER_CROUCH Animation is implemented
                if(animation.getAnimationType() != PLAYER_CROUCH){
                    lastIdleAnimationType = animation.getAnimationType();
                }
                animation = animCon.getAnimation(PLAYER_CROUCH);
                 */
                if(swordObject == null){
                    GameLoop.currentLevel.takeSwordFromGround(this);
                }
            }
        }

        if(keyCon.isKeyReleased(keySet.getDownKey())){
            AnimationType animType = animation.getAnimationType();
            if (animType == PLAYER_IDLE_HIGH) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
                lastIdleAnimationType = animation.getAnimationType();
            } else if (animType == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_LOW);
                lastIdleAnimationType = animation.getAnimationType();
            }
            /* // TODO: Activate as soon as PLAYER_CROUCH Animation is implemented
            else if (animType == PLAYER_CROUCH) {
                animation = animCon.getAnimation(lastIdleAnimationType);
            }
            */
        }
    }


    private void handleJumpKey(long diffMillis) {

        if (keyCon.isKeyPressed(keySet.getJumpKey())) {

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
            animation = animCon.getAnimation(lastIdleAnimationType);
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
                if (this.directionType == DirectionType.RIGHT) {
                    gripX = (int) this.getAnimation().getCurrentFrame().getSwordStartPoint().getX();
                    gripY = (int) this.getAnimation().getCurrentFrame().getSwordStartPoint().getY();
                } else {
                    gripX = (int) this.getAnimation().getCurrentFrame().getSwordStartPointInverted().getX() - CollisionController.getInstance().getPlayersWidthHeight()[0];
                    gripY = (int) this.getAnimation().getCurrentFrame().getSwordStartPointInverted().getY();
                    swordLength *= (-1);
                }
                if (CollisionController.getInstance().getSwordsHitting())
                    System.out.println("SWORDS COLLIDING");
                if (CollisionController.getInstance().getPlayerHitOtherPlayer(this.playerNumber) && this.playerNumber == PlayerType.PLAYER_ONE) // Testing player1_hit_player2
                    System.out.println("PLAYER1 HIT DETECTED");
                //recalculate coordinates
                Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);

                gc.setFill(Color.GREEN); // SwordMount
                gc.fillRect(drawPoint.getX() + gripX, drawPoint.getY() + gripY, 8, 8);
                gc.setFill(Color.PINK); // SwordTip
                gc.fillRect(drawPoint.getX() + gripX + swordLength, drawPoint.getY() + gripY, 8, 8);
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
                Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
                gc.strokeRect(drawPoint.getX() + playerXY[0].getX(), drawPoint.getY() + playerXY[0].getY(), playerWidthHeight[0], playerWidthHeight[1]);
            }
            // TESTING outLineHitBox ----------------------------------------------------------------------------------------------------
            case 3 -> {
                gc.setFill(Color.ORANGE);
                ArrayList<Point2D> hitBox;
                int offset = 0;
                if (this.directionType == DirectionType.RIGHT) {
                    hitBox = this.getAnimation().getCurrentFrame().getHitBox();
                } else {
                    hitBox = this.getAnimation().getCurrentFrame().getHitBoxInverted();
                    offset = playerWidthHeight[0] + 2;
                }
                for (Point2D point : hitBox) {
                    Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
                    gc.fillRect(drawPoint.getX() + point.getX() - offset, drawPoint.getY() + point.getY(), 2, 2);
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

    public boolean isOnGround() {
        return onGround;
    }

    public boolean getAlive() {
        return this.alive;
    }
}

