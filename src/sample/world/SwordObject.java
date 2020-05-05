package sample.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;
import sample.animation.Animation;
import sample.animation.FrameData;
import sample.controllers.DataController;
import sample.enums.AnimationType;
import sample.enums.Direction;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class SwordObject extends GameObject{

    private Animation animation = DataController.getInstance().getAnimation(AnimationType.SWORD);
    private PlayerObject playerObject;

    public SwordObject(int x, int y, Direction direction) {
        super(x, y, direction);
        animation.start();
    }

    @Override
    public void update() {
        animation.update();
        calculateCoordinates();
    }

    private void calculateCoordinates(){
            this.x = playerObject.x+
                    (int)playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getX()-
                    (int)animation.getCurrentFrame().getSwordStartPoint().getX();
            this.y = playerObject.y+
                    (int)playerObject.getAnimation().getCurrentFrame().getSwordStartPoint().getY()-
                    (int)animation.getCurrentFrame().getSwordStartPoint().getY();
    }

    private double calculateRotationAngle(){
        FrameData frameData = playerObject.getAnimation().getCurrentFrame();
        return frameData.getSwordStartPoint().angle(frameData.getSwordEndPoint());
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(animation.getSprite(), x, y, animation.getSprite().getWidth()* SCALE_FACTOR,animation.getSprite().getHeight()* SCALE_FACTOR);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setPlayerObject(PlayerObject playerObject) {
        this.playerObject = playerObject;
    }


}
