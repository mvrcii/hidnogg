package sample.animation;

import javafx.scene.image.Image;
import sample.enums.AnimationType;

public class Animation {

    private final AnimationType animationType;
    private final AnimationData animationData;

    private int currentFrame;
    private final int totalFrames;
    private double animationTimer;

    public Animation(AnimationType type, AnimationData animationData) {
        this.animationType = type;
        this.animationData = animationData;

        this.currentFrame = 0;
        this.totalFrames = animationData.getFrames().size();

        this.animationTimer = 0;
    }

    public void reset()
    {
        currentFrame = 0;
    }


    public void update(long diffMillis)
    {
        animationTimer += diffMillis;


        while (animationTimer >= animationType.getDuration())
        {
            animationTimer -= animationType.getDuration();

            if (currentFrame == totalFrames -1)
            {
                currentFrame = 0;
            }
            else
            {
                currentFrame++;
            }
        }
    }

    public int getCurrentFrameNumber(){
        return currentFrame;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public Image getCurrentSprite() {
        return animationData.getFrames().get(currentFrame).getImage();
    }

    public FrameData getCurrentFrame(){ return animationData.getFrames().get(currentFrame);}

}