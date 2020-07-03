package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import stickfight2d.GameLoop;
import stickfight2d.animation.Animation;
import stickfight2d.animation.FrameData;
import stickfight2d.controllers.CameraController;
import stickfight2d.controllers.DataController;
import stickfight2d.enums.AnimationType;
import stickfight2d.enums.DirectionType;
import stickfight2d.misc.Debugger;

public class SwordObject extends GameObject {

    private Animation animation = DataController.getInstance().getAnimation(AnimationType.SWORD);
    private PlayerObject playerObject;

    private boolean falling;
    private boolean onGround;
    private boolean throwing;

    private int currentAngle;
    private int bounceStartAngle = -1;

    private double diffSeconds = 0;
    private double timePassedGround = 0;
    private double bounceOffSet = 0;
    private double timePassedAir = 0;
    private int singleRotationTime = 2000; // 5seconds

    public SwordObject(int x, int y, DirectionType directionType, PlayerObject playerObject) {
        super(x, y, directionType);
        this.falling = false;
        this.onGround = false;
        this.throwing = false;
        this.playerObject = playerObject;

        if (playerObject != null) {
            this.directionType = playerObject.getDirectionType();
        }
        currentAngle = calculateRotationAngle();            // initial angle
    }


    @Override
    public void update(long diffSeconds) {
        this.diffSeconds = diffSeconds;

        updateAngle();                                          // Updating Angle
        if (!onGround) {
            if(playerObject != null){
                directionType = playerObject.getDirectionType();    // Updating Direction
            }
            animation.update(diffSeconds);                      // Updating Animation
        }
        updateCoordinates();
    }

    @Override
    public void draw(GraphicsContext gc) {
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

            if(playerObject == null){
                Debugger.log("Update Sword Coords: Player == null -> do nothing");
                switch (directionType){
                    case RIGHT -> x++;
                    case LEFT -> x--;
                }
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
            animation = DataController.getInstance().getSwordAnimAngle(currentAngle);

        } else if(throwing){

            timePassedAir += diffSeconds;
            if(playerObject == null){
                Debugger.log("Update Sword Angle: Player == null -> do nothing");

                if(timePassedAir >= singleRotationTime){
                    timePassedAir = 0;
                }
                currentAngle = (int) Math.floor(timePassedAir / (double) (singleRotationTime/360));

                if(currentAngle >= 360){
                    timePassedAir = 0;
                    currentAngle = 0;
                }

                animation = DataController.getInstance().getSwordAnimAngle(currentAngle);
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
                animation = DataController.getInstance().getSwordAnimAngle(currentAngle);
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
        if(this.playerObject != null){
            System.out.println("Start throw");
            int startAngle = calculateRotationAngle();
            throwing = true;


            // Clear bindings
            playerObject.setSwordObject(null);
;            this.playerObject = null;

            //fallToGround();
        }else{

        }
    }

    public void setPlayerObject(PlayerObject playerObject) {
        this.playerObject = playerObject;
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

}
