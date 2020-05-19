package sample.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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

    int swordOffSetXRight;
    int swordOffSetYRight;
    int playerOffSetXRight;
    int playerOffSetYRight;

    public SwordObject(int x, int y, Direction direction, PlayerObject playerObject) {
        super(x, y, direction);
        this.playerObject = playerObject;
        if(this.playerObject != null){
            this.direction = playerObject.getDirection();
        }
        this.currentAngle = calculateRotationAngle();
    }


    private void updateCoordinates() {
        int swordOffSetXLeft = animation.getCurrentFrame().getBufferedImage().getWidth() - (int) animation.getCurrentFrame().getSwordStartPoint().getX();
        int swordOffSetYLeft = animation.getCurrentFrame().getBufferedImage().getWidth() - (int) animation.getCurrentFrame().getSwordStartPoint().getY();


        swordOffSetXRight = (int) animation.getCurrentFrame().getSwordStartPoint().getX();
        swordOffSetYRight = (int) animation.getCurrentFrame().getSwordStartPoint().getY();
        playerOffSetXRight = (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX();
        playerOffSetYRight = (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getY();

        //System.out.println("Sword Offset LEFT("+swordOffSetXLeft+"|"+swordOffSetYLeft+")");
        //System.out.println("Sword Offset RIGHT("+swordOffSetXRight+"|"+swordOffSetYRight+")");
        //System.out.println("Sword Offset RIGHT("+(swordOffSetXRight+swordOffSetXLeft)+"|"+(swordOffSetYRight+swordOffSetYLeft)+")\n");

        switch (direction){
            case RIGHT ->
                    {
                        this.x = playerObject.x + playerOffSetXRight - swordOffSetXRight;
                        this.y = playerObject.y + playerOffSetYRight - swordOffSetYRight;
                    }
            case LEFT ->
                    {
                        this.x = playerObject.x +
                                (playerObject.getAnimation().getCurrentFrame().getBufferedImage().getWidth() -
                                        (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX());

                        this.y = playerObject.y -
                                (playerObject.getAnimation().getCurrentFrame().getBufferedImage().getHeight() -
                                        (int) playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getY());

                    }
        };

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
            //System.out.println("Angle changed to: "+currentAngle);
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
            case LEFT -> {
                FrameData.drawHorizontallyFlipped(gc, animation.getCurrentSprite(), x, y);
                gc.setLineWidth(1);
                gc.setStroke(Color.GREEN);
                gc.strokeLine(0,0, x, y);
            }
            case RIGHT -> {
                gc.drawImage(animation.getCurrentSprite(), x, y);
                gc.setLineWidth(1);
                gc.setStroke(Color.GREEN);
                gc.strokeLine(0,0, x+swordOffSetXRight, y+swordOffSetYRight);
            }
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


    public ArrayList<Point2D> getSwordSpike(){
        // TODO || Returns the points of the sword which are the closest to the enemy player
        ArrayList<Point2D> tmp = new ArrayList<>();
        tmp.add(new Point2D(0,0));
        return tmp;
    }
}
