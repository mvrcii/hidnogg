package stickfight2d.animation;

import javafx.scene.image.Image;
import stickfight2d.enums.AnimationType;

public class Animation {

    private final AnimationType animationType;
    private final AnimationData animationData;

    private int currentFrame;
    private final int totalFrames;
    private double animationTimer;
    private boolean stopped = false;

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

    public void stop(){
        stopped = true;
    }

    public void run(){
        stopped = false;
    }

    public void update(long diffMillis) {
        animationTimer += diffMillis;

        while (animationTimer >= animationType.getDuration()) {
            animationTimer -= animationType.getDuration();

            if (!stopped) {
                if (currentFrame == totalFrames - 1) {
                    currentFrame = 0;
                } else {
                    currentFrame++;
                }
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

    public boolean isLastFrame() {
        return currentFrame == totalFrames-1;
    }

    public String toString(){
        return animationType.toString();
    }
}