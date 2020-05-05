package sample.animation;

import javafx.scene.image.Image;
import sample.enums.AnimationType;

import java.util.ArrayList;


public class Animation {

    private final AnimationData animationData;
    private AnimationType type;
    private ArrayList<FrameData> frames;


    private int frameCount;                 // Counts ticks for change
    private final int frameDelay;                 // frame delay 1-12 (You will have to play around with this)
    private int currentFrame;               // animations current frame
    private int animationDirection;         // animation direction (i.e counting forward or backward)
    private int totalFrames;                // total amount of frames for your animation
    private boolean stopped;                // has animations stopped



    public Animation(AnimationType type, AnimationData animationData) {
        this.animationData = animationData;
        this.type = type;
        this.frames = animationData.getFrames();

        this.frameDelay = type.getDuration();
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
        this.animationDirection = 1;
        this.totalFrames = this.frames.size();
    }

    public void start() {
        if (!stopped) {
            return;
        }

        if (frames.size() == 0) {
            return;
        }

        stopped = false;
    }

    public void stop() {
        if (frames.size() == 0) {
            return;
        }

        stopped = true;
    }

    public void restart() {
        if (frames.size() == 0) {
            return;
        }

        stopped = false;
        currentFrame = 0;
    }

    public void reset() {
        this.stopped = true;
        this.frameCount = 0;
        this.currentFrame = 0;
    }


    public void update() {
        if (!stopped) {
            frameCount++;

            if (frameCount > frameDelay) {
                frameCount = 0;
                currentFrame += animationDirection;

                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                }
                else if (currentFrame < 0) {
                    currentFrame = totalFrames - 1;
                }
            }
        }

    }

    public AnimationType getType() {
        return type;
    }

    public Image getSprite() {
        return frames.get(currentFrame).getImage();
    }

    public FrameData getCurrentFrame(){ return frames.get(currentFrame);}

    public int getCurrentFramePos(){return currentFrame;}

    public AnimationData getAnimationData(){ return animationData;}
}