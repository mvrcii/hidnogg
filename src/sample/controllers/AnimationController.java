package sample.controllers;


import sample.animation.Animation;
import sample.animation.AnimationData;
import sample.enums.AnimationType;


import java.awt.*;
import java.util.HashMap;

public class AnimationController {

    private static AnimationController instance;
    private final HashMap<AnimationType, AnimationData> hashMap = new HashMap<>();

    public static AnimationController getInstance()
    {
        if(instance == null)
        {
            instance = new AnimationController();
        }
        return instance;
    }


    private AnimationController()
    {
        hashMap.put(AnimationType.PLAYER_IDLE_LOW, new AnimationData(new Point(0,0), new Point(1,0), new Point(1,0)));
        hashMap.put(AnimationType.PLAYER_IDLE_MEDIUM, new AnimationData(new Point(1,1)));
        hashMap.put(AnimationType.PLAYER_IDLE_HIGH, new AnimationData(new Point(0,2)));
        //hashMap.put(AnimationType.PLAYER_IDLE_LOW, new AnimationData(0));
    }


    public Animation getAnimation(AnimationType animationType){
        return new Animation(animationType, hashMap.get(animationType));
    }


}
