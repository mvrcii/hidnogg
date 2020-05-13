package sample.animation;

import javafx.scene.image.Image;
import sample.enums.AnimationType;
import java.util.ArrayList;


public class Animation {

    private final AnimationType animationType;
    private final ArrayList<FrameData> frames;




    private int currentFrame;               // animations current frame
    private final int totalFrames;                // total amount of frames for your animation


    private double animationTimer;

    public Animation(AnimationType type, AnimationData animationData) {
        this.animationType = type;
        this.frames = animationData.getFrames();

        this.currentFrame = 0;
        this.totalFrames = this.frames.size();

        this.animationTimer = 0;
    }

    public void reset()
    {
        currentFrame = 0;
    }


    public void update(long diffMillis)
    {
        System.out.println(diffMillis);

        animationTimer += diffMillis;


        while (animationTimer >= animationType.getDuration())
        {
            animationTimer -= (double) animationType.getDuration();

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





    public AnimationType getType() {
        return animationType;
    }

    public Image getSprite() {
        return frames.get(currentFrame).getImage();
    }

    public FrameData getCurrentFrame(){ return frames.get(currentFrame);}

}