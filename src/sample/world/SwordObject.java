package sample.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.CameraController;
import sample.controllers.DataController;
import sample.enums.AnimationType;
import sample.enums.DirectionType;

public class SwordObject extends GameObject{

    private Animation animation = DataController.getInstance().getAnimation(AnimationType.SWORD);
    private PlayerObject playerObject;

    private int currentAngle;

    public SwordObject(int x, int y, DirectionType directionType, PlayerObject playerObject) {
        super(x, y, directionType);
        this.playerObject = playerObject;
        if(this.playerObject != null){
            this.directionType = playerObject.getDirectionType();
        }
        this.currentAngle = calculateRotationAngle();
    }


    private void updateCoordinates() {

        switch (directionType){
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

        //System.out.println(f.getFrameNumber()+" "+f.getSwordStartPoint()+" "+f.getSwordEndPoint()+" "+f.getSwordStartPointInverted());
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
        this.directionType = playerObject.getDirectionType();
        animation.update(diffSeconds);
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
