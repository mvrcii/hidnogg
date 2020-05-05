package sample.controllers;


import sample.animation.Animation;
import sample.animation.AnimationData;
import sample.enums.AnimationType;


import java.awt.*;
import java.util.HashMap;

public class DataController {

    private static DataController instance;
    private final HashMap<AnimationType, AnimationData> hashMap = new HashMap<>();

    public static DataController getInstance()
    {
        if(instance == null)
        {
            instance = new DataController();
        }
        return instance;
    }


    private DataController()
    {
        //hashMap.put(AnimationType.PLAYER_IDLE_LOW, new AnimationData(new Point(0,0), new Point(1,0), new Point(1,0)));
        hashMap.put(AnimationType.PLAYER_IDLE_LOW, new AnimationData(0));
        hashMap.put(AnimationType.PLAYER_IDLE_MEDIUM, new AnimationData(new Point(0,1)));
        hashMap.put(AnimationType.PLAYER_IDLE_HIGH, new AnimationData(new Point(1,1)));

        System.out.println("Sword####");
        hashMap.put(AnimationType.SWORD, new AnimationData(new Point(2,1)));

    }


    public Animation getAnimation(AnimationType animationType){
        return new Animation(animationType, hashMap.get(animationType));
    }
}
