package sample.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import sample.GameLoop;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.CameraController;
import sample.controllers.DataController;
import sample.enums.AnimationType;
import sample.enums.DirectionType;

public class SwordObject extends GameObject {

    private Animation animation = DataController.getInstance().getSwordAnimAngle(AnimationType.SWORD);
    private PlayerObject playerObject;

    private boolean falling;
    private boolean onGround;

    private int currentAngle;
    private int bounceStartAngle = -1;

    private double diffSeconds = 0;
    private double timePassed = 0;

    public SwordObject(int x, int y, DirectionType directionType, PlayerObject playerObject) {
        super(x, y, directionType);
        this.falling = false;
        this.onGround = false;
        this.playerObject = playerObject;

        if (playerObject != null) {
            this.directionType = playerObject.getDirectionType();
        }
        currentAngle = calculateRotationAngle();    // initial angle
    }


    @Override
    public void update(long diffSeconds) {
        this.diffSeconds = diffSeconds;

        updateAngle();                                          // Updating Angle
        if (!onGround) {
            directionType = playerObject.getDirectionType();    // Updating Direction
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
                /*
                if (bounceStartAngle == -1) {
                    bounceStartAngle = currentAngle;    // Angle from which the bouncing will start
                    System.out.println("Bounce Start Angle: " + bounceStartAngle);
                }

                 */
                playerObject = null;
                animation.stop();
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

    private int calculateRotationAngle() {
        FrameData frameData = playerObject.getAnimation().getCurrentFrame();
        return (int) frameData.getSwordStartPoint().angle(frameData.getSwordEndPoint());
    }

    private void updateAngle() {

        if (onGround) {
            timePassed += diffSeconds;
            double a = 0.5;
            double w = 3;

            currentAngle = (int) Math.round(Math.exp(-a * (timePassed / 150)) * Math.cos(w * timePassed / 150) * bounceStartAngle);

            // If angle is negative, the sword needs to be shifted in y direction
            if (currentAngle < 0) {
                //System.out.println(x+"|"+y);

                Point2D p1 = animation.getCurrentFrame().getSwordStartPoint();
                Point2D p2 = animation.getCurrentFrame().getSwordEndPoint();

                System.out.println("Angle: "+(360+currentAngle)+" ("+currentAngle+")");
                double offSet = Math.sin(360+currentAngle) * Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
                System.out.println("Offset: "+offSet);
            }

            animation = DataController.getInstance().getSwordAnimAngle(currentAngle);

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


    public void fallToGround() {
        falling = true;
        vy = -10;
        bounceStartAngle = currentAngle;
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
