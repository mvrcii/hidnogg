package sample.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.DataController;
import sample.enums.AnimationType;
import sample.enums.Direction;

public class SwordObject extends GameObject{

    private Animation animation = DataController.getInstance().getAnimation(AnimationType.SWORD);
    private PlayerObject playerObject;

    private int currentAngle;

    public SwordObject(int x, int y, Direction direction, PlayerObject playerObject) {
        super(x, y, direction);
        this.playerObject = playerObject;
        animation.start();
        currentAngle = calculateRotationAngle();
    }


    private void calculateCoordinates() {
        this.x = playerObject.x +
                (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX() -
                (int) animation.getCurrentFrame().getSwordStartPoint().getX();
        this.y = playerObject.y +
                (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getY() -
                (int) animation.getCurrentFrame().getSwordStartPoint().getY();
    }

    private int calculateRotationAngle(){
        FrameData frameData = playerObject.getAnimation().getCurrentFrame();
        int angle = (int) frameData.getSwordStartPoint().angle(frameData.getSwordEndPoint());
        return angle;
    }

    private void updateAngle(){
        FrameData f = playerObject.getAnimation().getCurrentFrame();
        int newAngle = (int) new Point2D(1,0).angle(f.getSwordEndPoint().getX()-f.getSwordStartPoint().getX(),f.getSwordEndPoint().getY()-f.getSwordStartPoint().getY());
        if(currentAngle != newAngle){
            currentAngle = newAngle;
            System.out.println("Angle changed to: "+currentAngle);
            if(currentAngle==0){
                animation = DataController.getInstance().getAnimation(animation.getType());
            }else{
                animation = DataController.getInstance().getAnimation(animation.getType(), currentAngle);
            }
            animation.start();
        }
    }

    @Override
    public void update() {
        updateAngle();
        animation.update();
        calculateCoordinates();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(animation.getSprite(), x, y, animation.getSprite().getWidth(),animation.getSprite().getHeight());
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
