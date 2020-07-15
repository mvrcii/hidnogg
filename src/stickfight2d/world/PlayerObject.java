package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import stickfight2d.GameLoop;
import stickfight2d.animation.Animation;
import stickfight2d.animation.FrameData;
import stickfight2d.controllers.*;
import stickfight2d.enums.AnimationType;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.PlayerType;
import stickfight2d.enums.SoundType;
import stickfight2d.interfaces.InputSystem;
import stickfight2d.misc.Config;
import stickfight2d.misc.Debugger;
import stickfight2d.misc.KeySet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static stickfight2d.enums.AnimationType.*;
import static stickfight2d.misc.Config.*;

public class PlayerObject extends MoveableObject implements InputSystem {

    private final KeyController keyCon = KeyController.getInstance();
    private final AnimationFactory animCon = AnimationFactory.getInstance();
    private final SoundController soundCon = SoundController.getInstance();

    private final KeySet keySet;
    private final PlayerType playerNumber;
    private SwordObject swordObject;

    private boolean dropkick;
    private boolean canAccelerate;
    private boolean onGround;
    private boolean alive;
    private boolean deadAndMapChanged = false;

    private final boolean[] spread_blood = new boolean[10];

    private double time_passed = 0;

    private Animation animation = AnimationFactory.getInstance().getAnimation(INITIAL_ANIMATION_TYPE);
    private AnimationType lastIdleAnimationType = INITIAL_ANIMATION_TYPE;
    private AnimationType lastJumpAnimationType;

    // Gravity-Ground detection
    private final HashSet<AnimationType> jumps = Stream.of(
            AnimationType.PLAYER_JUMP_START,
            AnimationType.PLAYER_JUMP_PEAK).collect(Collectors.toCollection(HashSet::new));
    public RectangleObstacle currentObstacleStanding;


    public PlayerObject(int x, int y, PlayerType playerNumber, DirectionType directionType, KeySet keySet) {
        super(x, y, directionType);
        this.keySet = keySet;
        this.playerNumber = playerNumber;
        this.swordObject = new SwordObject(this.x, this.y, directionType, this);
        GameLoop.currentLevel.addSword(swordObject);

        Arrays.fill(spread_blood, Boolean.FALSE);
        this.onGround = true;
        this.alive = true;
        this.dropkick = false;
    }


    public void reset() {
        // Setting all the booleans
        onGround = true;
        alive = true;
        dropkick = false;
        canAccelerate = true;
        animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
        swordObject = new SwordObject(this.x, this.y, directionType, this);
        GameLoop.currentLevel.addSword(swordObject);
    }


    @Override
    public void update(long diffMillis) {
        int playerOffset = CollisionController.getInstance().getPlayersWidthHeight()[1];

        if(alive)
            deadAndMapChanged = false;

        animation.update(diffMillis);

        if (!(vy > 0 && CollisionController.getInstance().getPlayerHeadBump(this.playerNumber)))
            y -= vy * diffMillis / 100;

        if (!onGround || jumps.contains(this.getAnimation().getAnimationType())) {
            vy -= (2 * (double) diffMillis) / 10;    //gravity
        } else {
            vy = 0;
            y = currentObstacleStanding.getY() - playerOffset;
        }
    }


    @Override
    public void draw(GraphicsContext gc) {
        if(deadAndMapChanged)
            return;

        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
        switch (directionType) {
            case LEFT -> FrameData.drawHorizontallyFlipped(gc, animation.getCurrentSprite(), (int) drawPoint.getX(), (int) drawPoint.getY());
            case RIGHT -> gc.drawImage(animation.getCurrentSprite(), drawPoint.getX(), drawPoint.getY());
        }

        markPlayer(gc, drawPoint.getX(), drawPoint.getY());

        if (debug_mode) {
            this.showHitBoxState(gc, 1);
            this.showHitBoxState(gc, 2);
            this.showHitBoxState(gc, 3);
        }
    }


    @Override
    public void processInput(long diffMillis) {
        checkCollisions();
        checkWin();
        handleDeathAnimation(diffMillis);

        if (this.alive) {
            handleMovementKeys(diffMillis);
            handleUpKey();
            handleThrowing();
            handleDownKey();
            handleStabKey();
            handleJumpKey(diffMillis);
            handleDropKick(diffMillis);
        }
    }


    public void checkSwordInNewScreen() {
        if (alive && swordObject == null) {
            swordObject = new SwordObject(this.x, this.y, DirectionType.RIGHT, this);
            GameLoop.currentLevel.addSword(swordObject);
        }
    }


    private void checkWin() {
        if (CollisionController.getInstance().getWin(this.playerNumber)) {
            if (animation.getAnimationType() != PLAYER_WIN) {
                keyCon.setKeyPressBlockedP1(true);
                keyCon.setKeyPressBlockedP2(true);
                GameLoop.currentLevel.removeGameObject(swordObject);
                this.swordObject = null;
                animation = animCon.getAnimation(PLAYER_WIN);
                GameLoop.currentMusic.stop();
                GameLoop.currentMusic = SoundController.getInstance().getMusic(SoundType.MUSIC_GAME_WON);
                GameLoop.currentMusic.play(true, Config.volume);
            }
        }
    }


    private void checkCollisions() {

        CollisionController colCon = CollisionController.getInstance();
        onGround = colCon.getPlayerOnGround(this.playerNumber);

        // Player getting hit by other player
        if (colCon.getPlayerHit(this.playerNumber) && alive && !colCon.isAttackBlocked()) {
            Debugger.log(playerNumber + " got hit");
            alive = false;
            time_passed = 0;

            switch (playerNumber) {
                case PLAYER_ONE -> keyCon.setKeyPressBlockedP1(true);
                case PLAYER_TWO -> keyCon.setKeyPressBlockedP2(true);
            }

            keyCon.removePlayerKeyPress(this);   // only clear player specific keys

            if (swordObject != null) {    // only if PLAYER has a sword
                swordObject.fallToGround();
                this.swordObject = null;
            }
            animation = animCon.getAnimation(PLAYER_DYING);

            if (new Random().nextBoolean()) {
                soundCon.getSound(SoundType.SOUND_SWORD_SWING_FAST_HIT_BODY_1).play(volume);
            } else {
                soundCon.getSound(SoundType.SOUND_SWORD_SWING_FAST_HIT_BODY_2).play(volume);
            }

            if (animation.getAnimationType() == PLAYER_DYING) {
                Debugger.log("player dieing");
            }
        } else if (colCon.isAttackBlocked()) {
            if (!colCon.getPlayerHitsWallRight(this.playerNumber) && !colCon.getPlayerHitsWallLeft(this.playerNumber)) {
                switch (directionType) {
                    case LEFT -> this.x = x + KNOCKBACK_VALUE;
                    case RIGHT -> this.x = x - KNOCKBACK_VALUE;
                }
            }
        }

        /*
        // If the player runs against the wall, stop the movement and change direction
        if(colCon.getPlayerHitsWallRight(this.playerNumber) || colCon.getPlayerHitsWallLeft(this.playerNumber)){
            if(swordObject == null){
                if(animation.getAnimationType() != PLAYER_IDLE_NO_SWORD){
                    animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
                    DirectionController.getInstance().setManualControl(this, false);
                }
            }else{
                if(animation.getAnimationType() != lastIdleAnimationType){
                    animation = animCon.getAnimation(lastIdleAnimationType);
                    DirectionController.getInstance().setManualControl(this, false);
                }
            }
        }
        */


        // Player's sword hitting another player's sword
        if (colCon.getSwordsHitting()) {

            if (alive) {

                // This player is getting disarmed
                if (colCon.getPlayerBeingDisarmed(playerNumber)) {
                    if (swordObject != null) {        // only if PLAYER has a sword
                        swordObject.fallToGround();
                        this.swordObject = null;
                        animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
                    }
                    // HIT but not disarmed
                }else{
                    soundCon.getSound(SoundType.SOUND_SWORD_HIT_SWORD).play(volume);
                }



                if(swordCollisionParticles){
                    Point2D collisionPoint = colCon.getSwordCollisionPoint();
                    int x_ = (int) collisionPoint.getX();
                    int y_ = (int) collisionPoint.getY();
                    GameLoop.currentLevel.addGameObject(new ParticleEmitter(this, x_, y_, 50, 100, 2, 10, 180, 60, "0xd4af37", 2));
                }


                if (!colCon.getPlayerHitsWallRight(playerNumber) && !colCon.getPlayerHitsWallLeft(playerNumber)) {
                    switch (directionType) {
                        case LEFT -> this.x = x + KNOCKBACK_VALUE;
                        case RIGHT -> this.x = x - KNOCKBACK_VALUE;
                    }
                }
            }
        }

    }

/*
    private void createSwordCollisionParticles(PlayerObject p) {
        double x_ = 0;
        double y_ = 0;

        switch (p.swordObject.directionType) {
            case LEFT -> {
                x_ = p.x + p.animation.getCurrentFrame().getSwordStartPointInverted().getX()
                        - (int) p.swordObject.getAnimation().getCurrentFrame().getSwordStartPointInverted().getX()
                        + (64 - p.swordObject.getSwordTip().getX());
                y_ = p.x + p.animation.getCurrentFrame().getSwordStartPointInverted().getY()
                        - (int) p.swordObject.getAnimation().getCurrentFrame().getSwordStartPointInverted().getY()
                        + (64 - p.swordObject.getSwordTip().getY());
            }
            case RIGHT -> {
                x_ = p.x + p.animation.getCurrentFrame().getSwordStartPoint().getX()
                        - (int) p.swordObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX()
                        + p.swordObject.getSwordTip().getX();
                y_ = p.y + p.animation.getCurrentFrame().getSwordStartPoint().getY()
                        - (int) p.swordObject.getAnimation().getCurrentFrame().getSwordStartPoint().getY()
                        + p.swordObject.getSwordTip().getY();
            }
        }
    }
*/

    private void handleDeathAnimation(double diffMillis) {

        Point2D[] bloodPoints = new Point2D[]{  //   Frame   X       Y
                new Point2D(12, 19),     //   0       12      19
                new Point2D(11, 55),     //   1       11      55
                new Point2D(9,  30),     //   2       9       30
                new Point2D(16, 30),     //   3       16      30
                new Point2D(23, 35),     //   4       23      35
                new Point2D(28, 38),     //   5       28      38
                new Point2D(29, 41),     //   6       29      41
                new Point2D(29, 41),     //   7       29      41
                new Point2D(30, 42),     //   8       30      42
                new Point2D(29, 42)};    //   9       29      42

        if (animation.getAnimationType() == PLAYER_DYING) {
            if (animation.isLastFrame()) {
                animation.stop();
            }
            int i = animation.getCurrentFrameNumber();

            if (!spread_blood[i]) {
                int xOffset = (int) bloodPoints[i].getX();
                int yOffset = (int) bloodPoints[i].getY();

                switch (directionType) {
                    case LEFT -> GameLoop.currentLevel.addGameObject(new ParticleEmitter(this, x, y + yOffset,30, 300, 2, 10, 180, 60, "0xFF0000", 4));
                    case RIGHT -> GameLoop.currentLevel.addGameObject(new ParticleEmitter(this, x + xOffset, y + yOffset, 30, 300, 2, 10, 180, 60, "0xFF0000", 4));
                }
                spread_blood[i] = true;
            }
        }

        if (!alive) {
            time_passed += diffMillis;

            if (time_passed > T_RESPAWN) {
                GameLoop.currentLevel.respawnPlayer(this);
                resetBloodArray();
            }
        }
    }


    private void handleStabKey() {
        if (keyCon.isKeyPressed(keySet.getStabKey())) {
            switch (animation.getAnimationType()) {
                case PLAYER_IDLE_LOW, PLAYER_IDLE_MEDIUM, PLAYER_IDLE_HIGH -> animation = animCon.getStabAnim(lastIdleAnimationType);
                case PLAYER_IDLE_NO_SWORD -> animation = animCon.getAnimation(PLAYER_STAB_NO_SWORD);
                case PLAYER_WALK -> {
                    // Manual control is usually turned on while walking to the left / right
                    // by setting it to false here, the player always stabs towards his enemy
                    DirectionController.getInstance().setManualControl(this, false);
                    if (swordObject == null) {
                        animation = animCon.getAnimation(PLAYER_STAB_NO_SWORD);
                    } else {
                        animation = animCon.getStabAnim(lastIdleAnimationType);
                    }
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
                case PLAYER_STAB_NO_SWORD -> animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
            }
        }

    }


    private void handleMovementKeys(long diffMillis) {

        // RIGHT
        if (keyCon.isKeyPressed(keySet.getMoveRightKey()) && !keyCon.isKeyPressed(keySet.getMoveLeftKey()) && !CollisionController.getInstance().getPlayerHitsWallLeft(this.playerNumber)) {

            x += speed * diffMillis / 10;
            double t_pressed = keyCon.getKeyPressedTime(keySet.getMoveRightKey());

            if (animation.getAnimationType() != PLAYER_WALK && onGround) {
                DirectionController.getInstance().setManualControl(this, true);
                directionType = DirectionType.RIGHT;

                if (t_pressed > 100) {
                    animation = animCon.getAnimation(PLAYER_WALK);
                } else {
                    animation = animCon.getStepAnim(lastIdleAnimationType);
                }
            }

        }


        // LEFT
        if (keyCon.isKeyPressed(keySet.getMoveLeftKey()) && !keyCon.isKeyPressed(keySet.getMoveRightKey()) && !CollisionController.getInstance().getPlayerHitsWallRight(this.playerNumber)) {

            x -= speed * diffMillis / 10;
            double t_pressed = keyCon.getKeyPressedTime(keySet.getMoveLeftKey());

            if (animation.getAnimationType() != PLAYER_WALK && onGround) {
                DirectionController.getInstance().setManualControl(this, true);
                directionType = DirectionType.LEFT;

                if (t_pressed > 100) {
                    animation = animCon.getAnimation(PLAYER_WALK);
                } else {
                    animation = animCon.getStepAnim(lastIdleAnimationType);
                }
            }
        }

        if (keyCon.isKeyPressed(keySet.getMoveLeftKey()) && keyCon.isKeyPressed(keySet.getMoveRightKey())) {
            if (animation.getAnimationType() != lastIdleAnimationType) {
                if (swordObject == null) {
                    if (animation.getAnimationType() != PLAYER_IDLE_NO_SWORD) {
                        animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
                    }
                } else {
                    animation = animCon.getAnimation(lastIdleAnimationType);
                }
            }
        }

        if (keyCon.isKeyReleased(keySet.getMoveLeftKey()) || keyCon.isKeyReleased(keySet.getMoveRightKey())) {
            DirectionController.getInstance().setManualControl(this, false);
            if (swordObject == null) {
                animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
            } else {
                animation = animCon.getAnimation(lastIdleAnimationType);
            }
        }

    }


    private void handleUpKey() {
        if (swordObject != null) {
            if (keyCon.isKeyPressed(keySet.getUpKey())) {
                double t_pressed = keyCon.getKeyPressedTime(keySet.getUpKey());
                if (t_pressed > T_HOLDUP) {
                    if (animation.getAnimationType() != PLAYER_IDLE_HOLD_UP) {
                        animation = animCon.getAnimation(PLAYER_IDLE_HOLD_UP);
                    }
                }
            }

            if (keyCon.isKeyReleased(keySet.getUpKey())) {
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
    }


    private void handleThrowing() {
        if (animation.getAnimationType() == PLAYER_IDLE_HOLD_UP) {
            if (keyCon.isKeyPressed(keySet.getStabKey())) {
                if (swordObject != null) {
                    swordObject.startThrowing();
                    animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
                }
            }
        }
    }


    private void handleDownKey() {
        if (keyCon.isKeyPressed(keySet.getDownKey())) {
            double t_pressed = keyCon.getKeyPressedTime(keySet.getDownKey());

            if (t_pressed > T_CROUCH) {

                if (animation.getAnimationType() != PLAYER_CROUCH) {
                    animation = animCon.getAnimation(PLAYER_CROUCH);
                }
                if (swordObject == null) {
                    GameLoop.currentLevel.takeSwordFromGround(this);
                }
            }
        }
        if (keyCon.isKeyReleased(keySet.getDownKey())) {
            AnimationType animType = animation.getAnimationType();
            if (animType == PLAYER_IDLE_HIGH) {
                animation = animCon.getAnimation(PLAYER_IDLE_MEDIUM);
                lastIdleAnimationType = animation.getAnimationType();
            } else if (animType == PLAYER_IDLE_MEDIUM) {
                animation = animCon.getAnimation(PLAYER_IDLE_LOW);
                lastIdleAnimationType = animation.getAnimationType();
            }
        }

        if (animation.isLastFrame()) {
            if (animation.getAnimationType() == PLAYER_CROUCH) {
                if (swordObject != null) {
                    animation = animCon.getAnimation(lastIdleAnimationType);
                } else {
                    animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
                }

                keyCon.removeKeyPress(keySet.getDownKey());
            }
        }
    }


    private void handleJumpKey(long diffMillis) {
        if (keyCon.isKeyPressed(keySet.getJumpKey())) {
            if (onGround) {
                animation = animCon.getAnimation(PLAYER_JUMP_START);
                lastJumpAnimationType = PLAYER_JUMP_START;
                vy = JUMP_VY;
                canAccelerate = true;
            } else {
                if (vy >= 1.5*JUMP_VY) {
                    canAccelerate = false;
                }
                if (canAccelerate) {
                    vy += ((3 * (double) diffMillis) / 10);
                }
            }
        } else {
            canAccelerate = false;
        }
        if (animation.getAnimationType() == PLAYER_JUMP_START && vy >= JUMP_VY) {
            animation = animCon.getAnimation(PLAYER_JUMP_PEAK);
            lastJumpAnimationType = PLAYER_JUMP_PEAK;
        }
        if (animation.getAnimationType() == PLAYER_JUMP_PEAK && vy <= -0.75*JUMP_VY) {
            animation = animCon.getAnimation(PLAYER_JUMP_END);
            lastJumpAnimationType = PLAYER_JUMP_END;
        }
        if (animation.getAnimationType() == PLAYER_JUMP_END && vy <= 1 && vy >= -1) {
            if (swordObject == null) {
                animation = animCon.getAnimation(PLAYER_IDLE_NO_SWORD);
            } else {
                animation = animCon.getAnimation(lastIdleAnimationType);
            }
        }
        AnimationType animType = animation.getAnimationType();
        if(!onGround && animType != PLAYER_JUMP_PEAK
        && animType != PLAYER_JUMP_START
        && animType != PLAYER_JUMP_END
        && animType != PLAYER_DROPKICK){
            animation = animCon.getAnimation(lastJumpAnimationType);
        }

    }


    private void handleDropKick(long diffMillis) {
        int prevX;

        if (keyCon.isKeyPressed(keySet.getJumpKey())
                || animation.getAnimationType() == PLAYER_JUMP_PEAK
                || animation.getAnimationType() == PLAYER_JUMP_START
                || animation.getAnimationType() == PLAYER_JUMP_END)
        {
            if (keyCon.isKeyPressed(keySet.getStabKey())) {
                dropkick = true;
                animation = animCon.getAnimation(PLAYER_DROPKICK);
                if (directionType == DirectionType.RIGHT) {
                    vx = DROPKICK_VX;
                } else {
                    vx = -DROPKICK_VX;
                }
            }
        }

        if (dropkick) {
            if (onGround) {
                vx = 0;
                dropkick = false;
                animation = animCon.getAnimation(lastIdleAnimationType);
            } else {
                vx -= (vx > 0) ? (2 * (double) diffMillis) / 100 : 0;
                prevX = this.x;
                x += vx * diffMillis / 100;
                if (CollisionController.getInstance().getPlayerHitsWall(this.playerNumber)) {
                    this.x = prevX;
                    dropkick = false;
                }
            }
        }
    }

    /**
     * HitBox Test method
     */
    private void showHitBoxState(GraphicsContext gc, int testId) {
        CollisionController colCon = CollisionController.getInstance();
        int[] playerWidthHeight = colCon.getPlayersWidthHeight();

        switch (testId) {
            // TESTING swordPoints ----------------------------------------------------------------------------------------------------
            case 1 -> {
                if (this.getSwordObject() != null && !colCon.getNonStabAnimations().contains(this.getAnimation().getAnimationType())) {
                    int gripX;
                    int gripY;
                    int swordLength = colCon.getSwordLength();
                    if (this.directionType == DirectionType.RIGHT) {
                        gripX = (int) this.getAnimation().getCurrentFrame().getSwordStartPoint().getX();
                        gripY = (int) this.getAnimation().getCurrentFrame().getSwordStartPoint().getY();
                    } else {
                        gripX = (int) this.getAnimation().getCurrentFrame().getSwordStartPointInverted().getX() - colCon.getPlayersWidthHeight()[0];
                        gripY = (int) this.getAnimation().getCurrentFrame().getSwordStartPointInverted().getY();
                        swordLength *= (-1);
                    }
                    if (colCon.getSwordsHitting())
                        Debugger.log("SWORDS COLLIDING");
                    if (colCon.getPlayerHitOtherPlayer(this.playerNumber) && this.playerNumber == PlayerType.PLAYER_ONE) // Testing player1_hit_player2
                        Debugger.log("PLAYER1 HIT DETECTED");
                    //recalculate coordinates
                    Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);

                    gc.setFill(Color.GREEN); // SwordMount
                    gc.fillRect(drawPoint.getX() + gripX, drawPoint.getY() + gripY, 4, 4);
                    gc.setFill(Color.PINK); // SwordTip
                    gc.fillRect(drawPoint.getX() + gripX + swordLength, drawPoint.getY() + gripY, 4, 4);
                }
            }
            // TESTING rectangleHitBox ----------------------------------------------------------------------------------------------------
            case 2 -> {
                gc.setStroke(Color.GREEN);
                boolean playerOnGround = colCon.getPlayerOnGround(this.playerNumber);
                boolean playerHitsWall = (colCon.getPlayerHitsWallLeft(this.playerNumber) || colCon.getPlayerHitsWallRight(this.playerNumber));
                if (playerOnGround && playerHitsWall)
                    gc.setStroke(Color.BLACK);
                else if (playerOnGround)
                    gc.setStroke(Color.RED);
                else if (playerHitsWall)
                    gc.setStroke(Color.BLUE);
                else
                    gc.setStroke(Color.GREEN);
                Point2D[] playerXY = colCon.getRectHitBoxP1_P2();
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


    public void setTimePassed(double value){
        if(value >= 0){
            time_passed = value;
        }else{
            throw new IllegalArgumentException("No valid time value");
        }
    }


    public void resetBloodArray(){
        Arrays.fill(spread_blood, Boolean.FALSE);
    }


    private void markPlayer(GraphicsContext gc, double x, double y) {
        if (playerNumber == PlayerType.PLAYER_ONE)
            gc.setFill(Color.CYAN);
        else
            gc.setFill(Color.ORANGERED);

        int playerWidth = CollisionController.getInstance().getPlayersWidthHeight()[0];
        gc.fillPolygon(new double[]{x + 10, x + 14, x - 14 + playerWidth, x - 10 + playerWidth, x + playerWidth / 2.0}, new double[]{y - 25, y - 18, y - 18, y - 25, y - 8}, 5);
    }


    public void resetAnimationToIdle() {
        this.animation = animCon.getAnimation(lastIdleAnimationType);
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


    public void setXY(int x, int y) {
        this.setX(x);
        this.setY(y);
    }


    public boolean isOnGround() {
        return onGround;
    }


    public KeySet getKeySet() {
        return keySet;
    }


    public boolean isAlive(){
        return this.alive;
    }


    public boolean isDeadAndMapChanged() {
        return this.deadAndMapChanged;
    }


    public void setDeadAndMapChanged(boolean mapChanged) {
        this.deadAndMapChanged = mapChanged;
    }
}