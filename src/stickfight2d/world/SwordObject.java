package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import stickfight2d.GameLoop;
import stickfight2d.animation.Animation;
import stickfight2d.animation.FrameData;
import stickfight2d.controllers.AnimationFactory;
import stickfight2d.controllers.CameraController;
import stickfight2d.enums.AnimationType;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.ParticleType;
import stickfight2d.enums.PlayerType;
import stickfight2d.interfaces.ParticleOwner;

public class SwordObject extends GameObject implements ParticleOwner {

    private Animation animation = AnimationFactory.getInstance().getAnimation(AnimationType.SWORD);
    private PlayerObject playerObject;

    private boolean falling;
    private boolean onGround;
    private boolean throwing;

    private int currentAngle;
    private int bounceStartAngle = -1;

    private int x0 = 0;
    private int y0 = 0;

    private double diffSeconds = 0;
    private double particleTimer = 0;
    private double timePassedGround = 0;
    private double bounceOffSet = 0;
    private double timePassedAir;
    private double timePassedAirCoordinates;
    private final static int SINGLE_ROTATION_TIME = 600; // 1seconds

    public SwordObject(int x, int y, DirectionType directionType, PlayerObject playerObject) {
        super(x, y, directionType);
        this.falling = false;
        this.onGround = false;
        this.throwing = false;
        this.playerObject = playerObject;
        this.vx = 200;

        if (playerObject != null) {
            this.directionType = playerObject.getDirectionType();
        }
        currentAngle = calculateRotationAngle();            // initial angle
    }


    @Override
    public void update(long diffSeconds) {
        this.diffSeconds = diffSeconds;

        updateAngle();                                              // Updating Angle
        if (!onGround) {
            if(playerObject != null){
                directionType = playerObject.getDirectionType();    // Updating Direction
            }
            animation.update(diffSeconds);                          // Updating Animation
            updateFireParticles();                                // Update Fire Particles
        }
        updateCoordinates();
    }

    @Override
    public void draw(GraphicsContext gc) {
        if(playerObject != null && playerObject.isDeadAndMapChanged())
            return;

        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
        switch (directionType) {
            case LEFT -> FrameData.drawHorizontallyFlipped(gc, animation.getCurrentSprite(), (int) drawPoint.getX(), (int) drawPoint.getY());
            case RIGHT -> gc.drawImage(animation.getCurrentSprite(), drawPoint.getX(), drawPoint.getY());
        }
    }


    private void updateCoordinates() {

        if (falling) {
            int swordOffset = (int) animation.getCurrentFrame().getSwordStartPoint().getX() + 10;

            y -= vy * diffSeconds / 100;

            if (y < GameLoop.currentLevel.getGroundLevel() - swordOffset) {
                vy -= (2 * diffSeconds) / 10;    //gravity
            } else {
                vy = 0;
                y = GameLoop.currentLevel.getGroundLevel() - swordOffset;
                onGround = true;
                falling = false;
                playerObject = null;
                animation.stop();
            }

        } else if(throwing){
            timePassedAirCoordinates += diffSeconds;

            if(playerObject == null){
                update_x();
                update_y();
            }

        } else {
            if (playerObject != null) {
                switch (directionType) {
                    case LEFT -> {
                        this.x = playerObject.x + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPointInverted().getX()
                                - (int) animation.getCurrentFrame().getSwordStartPointInverted().getX();
                        this.y = playerObject.y + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPointInverted().getY()
                                - (int) animation.getCurrentFrame().getSwordStartPointInverted().getY();
                    }
                    case RIGHT -> {
                        this.x = playerObject.x + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX()
                                - (int) animation.getCurrentFrame().getSwordStartPoint().getX();
                        this.y = playerObject.y + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getY()
                                - (int) animation.getCurrentFrame().getSwordStartPoint().getY();
                    }
                }
            }
        }

    }


    private void updateFireParticles(){
        particleTimer += diffSeconds;

        if (!onGround) {
            if (particleTimer > 300) {
                int x_ = 0;
                int y_ = (int) (y + (animation.getCurrentFrame().getSwordEndPoint().getY()));

                if(directionType == DirectionType.RIGHT) {
                    x_ = (int) (x + animation.getCurrentFrame().getSwordEndPoint().getX());
                }else{
                    x_ = (int) (x - animation.getCurrentFrame().getSwordEndPointInverted().getX());
                }

                /* For debugging
                if(playerObject.getPlayerNumber() == PlayerType.PLAYER_ONE)
                    System.out.println("X="+x_+" | PlayerX="+playerObject.x+" + PlayerSwordStartPointX="+
                            (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX()+" - SwordSwordStartPointX="+
                            (int) animation.getCurrentFrame().getSwordStartPoint().getX()+" + SwordSwordEndPointX="+endPoint.getX()+" Angle="+animation.getCurrentFrame().getAngle());
                */

                GameLoop.currentLevel.addGameObject(new ParticleEmitter(ParticleType.SWORD_FIRE, null, x_, y_, 200, 30, 5, 5, 90, 50, "#fc5a03", 1));
                particleTimer = 0;
            }
        }
    }


    private void update_x(){
        if(directionType == DirectionType.RIGHT){
            this.x = x0 + (int) (vx * timePassedAirCoordinates/1000);
        }else{
            this.x = x0 + (int) (-vx * timePassedAirCoordinates/1000);
        }
    }

    private void update_y(){
        int swordOffset = (int) animation.getCurrentFrame().getSwordStartPoint().getX() + 10;
        if (y < GameLoop.currentLevel.getGroundLevel() - swordOffset) {
            this.y = y0 + (int) (0.5 * 9.81 * Math.pow(timePassedAirCoordinates/1000, 2));
        } else{
            this.y = GameLoop.currentLevel.getGroundLevel() - swordOffset;
            onGround = true;
            throwing = false;

        }

    }

    private void updateAngle() {
        if (onGround) {
            timePassedGround += diffSeconds;
            double a = 0.5;
            double w = 3;

            currentAngle = (int) Math.round(Math.exp(-a * (timePassedGround / 150)) * Math.cos(w * timePassedGround / 150) * bounceStartAngle);

            // If angle is negative, the sword needs to be shifted in y direction
            if (currentAngle < 0) {

                Point2D p1 = animation.getCurrentFrame().getSwordStartPoint();
                Point2D p2 = animation.getCurrentFrame().getSwordEndPoint();
                bounceOffSet = Math.sin(360+currentAngle) * Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
            }
            animation = AnimationFactory.getInstance().getSwordAnimAngle(currentAngle);

        } else if(throwing){

            timePassedAir += diffSeconds;

            if(playerObject == null){

                if(timePassedAir >= SINGLE_ROTATION_TIME){
                    timePassedAir = 0;
                }

                currentAngle = 360 - (int) Math.floor(timePassedAir / (double) (SINGLE_ROTATION_TIME/360));

                if(currentAngle <= 0){
                    timePassedAir = 0;
                    currentAngle = 0;
                }
                //System.out.println(currentAngle);
                animation = AnimationFactory.getInstance().getSwordAnimAngle(currentAngle);
            }

        } else {
            FrameData f = playerObject.getAnimation().getCurrentFrame();
            double x_start = f.getSwordStartPoint().getX();
            double y_start = f.getSwordStartPoint().getY();
            double x_end = f.getSwordEndPoint().getX();
            double y_end = f.getSwordEndPoint().getY();

            int nextAngle = (int) new Point2D(1, 0).angle(x_end - x_start, y_end - y_start);

            if (currentAngle != nextAngle) {
                currentAngle = nextAngle;
                animation = AnimationFactory.getInstance().getSwordAnimAngle(currentAngle);
            }
        }

    }

    private int calculateRotationAngle() {
        FrameData frameData = playerObject.getAnimation().getCurrentFrame();
        return (int) frameData.getSwordStartPoint().angle(frameData.getSwordEndPoint());
    }


    public void fallToGround() {
        falling = true;
        vy = -10;
        bounceStartAngle = currentAngle;
    }

    public void startThrowing(){
        if(playerObject != null){
            int startAngle = calculateRotationAngle();
            timePassedAirCoordinates = 0;
            vx = 150;
            timePassedAir = (int) (startAngle * SINGLE_ROTATION_TIME / 360);
            x0 = x;
            y0 = y;
            throwing = true;

            // Clear bindings
            playerObject.setSwordObject(null);
            playerObject = null;
        }
    }

    public void setPlayerObject(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }

    public PlayerObject getPlayerObject() {
        return playerObject;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isFalling() {
        return falling;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public Animation getAnimation() {
        return animation;
    }

    public Point2D getSwordTip(){
        return animation.getCurrentFrame().getSwordEndPoint();
    }

    @Override
    public String toString() {
        return "SwordObject{" +
                "playerObject=" + playerObject +
                ", falling=" + falling +
                ", onGround=" + onGround +
                ", throwing=" + throwing +
                '}';
    }

    @Override
    public boolean isClearCondition() {
        return false;
    }
}
