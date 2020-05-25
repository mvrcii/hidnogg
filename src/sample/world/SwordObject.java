package sample.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.DataController;
import sample.enums.AnimationType;
import sample.enums.Direction;

import java.util.ArrayList;

public class SwordObject extends GameObject{

    private Animation animation = DataController.getInstance().getAnimation(AnimationType.SWORD);
    private PlayerObject playerObject;

    private int currentAngle;

    public SwordObject(int x, int y, Direction direction, PlayerObject playerObject) {
        super(x, y, direction);
        this.playerObject = playerObject;
        if(this.playerObject != null){
            this.direction = playerObject.getDirection();
        }
        this.currentAngle = calculateRotationAngle();
    }


    private void updateCoordinates() {
        switch (direction){
            case LEFT ->
                    {
                        this.x = playerObject.x + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPointInverted().getX()
                        - (int) animation.getCurrentFrame().getSwordStartPointInverted().getX();
                        this.y = playerObject.y + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPointInverted().getY()
                                - (int) animation.getCurrentFrame().getSwordStartPointInverted().getY();
                    }
            case RIGHT ->
                    {
                        this.x = playerObject.x + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX()
                        - (int) animation.getCurrentFrame().getSwordStartPoint().getX();
                        this.y = playerObject.y + (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getY()
                        - (int) animation.getCurrentFrame().getSwordStartPoint().getY();
                    }
        }
    }

    private int calculateRotationAngle(){
        FrameData frameData = playerObject.getAnimation().getCurrentFrame();
        return (int) frameData.getSwordStartPoint().angle(frameData.getSwordEndPoint());
    }

    private void updateAngle(){
        FrameData f = playerObject.getAnimation().getCurrentFrame();
        int newAngle = (int) new Point2D(1,0).angle(f.getSwordEndPoint().getX()-f.getSwordStartPoint().getX(),
                f.getSwordEndPoint().getY()-f.getSwordStartPoint().getY());
        if(currentAngle != newAngle){
            currentAngle = newAngle;
            if(currentAngle == 0){
                animation = DataController.getInstance().getAnimation(animation.getAnimationType());
            }else{
                animation = DataController.getInstance().getAnimation(animation.getAnimationType(), currentAngle);
            }
        }
    }


    @Override
    public void update(long diffSeconds) {
        updateAngle();
        this.direction = playerObject.getDirection();
        animation.update(diffSeconds);
        updateCoordinates();
    }

    @Override
    public void draw(GraphicsContext gc) {
        switch (direction) {
            case LEFT -> FrameData.drawHorizontallyFlipped(gc, animation.getCurrentSprite(), x, y);
            case RIGHT -> gc.drawImage(animation.getCurrentSprite(), x, y);
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setPlayerObject(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }

    public PlayerObject getPlayerObject() {
        return playerObject;
    }
}
