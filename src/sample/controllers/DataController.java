package sample.controllers;


import sample.animation.Animation;
import sample.animation.AnimationData;
import sample.enums.AnimationType;

import java.util.HashMap;

public class DataController extends Controller {

    private static DataController instance;
    private final HashMap<AnimationType, AnimationData> basicAnimationData = new HashMap<>();
    private final HashMap<Integer, AnimationData> swordAngleData = new HashMap<>();

    public static DataController getInstance()
    {
        if(instance == null)
        {
            System.out.println("Data Controller instantiated");
            instance = new DataController();
        }
        return instance;
    }


    private DataController()
    {
        basicAnimationData.put(AnimationType.PLAYER_IDLE_LOW, new AnimationData(0));
        basicAnimationData.put(AnimationType.PLAYER_IDLE_MEDIUM, new AnimationData(1));
        basicAnimationData.put(AnimationType.PLAYER_IDLE_HIGH, new AnimationData(3));
        basicAnimationData.put(AnimationType.PLAYER_IDLE_HOLD_UP, new AnimationData(4));

        basicAnimationData.put(AnimationType.SWORD, new AnimationData(2));

        for (int i = 1; i <= 360; i+=1) {
            swordAngleData.put(i, basicAnimationData.get(AnimationType.SWORD).rotate(i));
        }

        // TESTING
        //ArrayList<FrameData> f = basicAnimationData.get(AnimationType.PLAYER_IDLE_HOLD_UP).getFrames();
        //System.out.println(f.size());
        // TESTING
    }

    @Override
    public void update(long diffMillis) {

    }


    public Animation getAnimation(AnimationType animationType){
        return new Animation(animationType, basicAnimationData.get(animationType));
    }

    public Animation getAnimation(AnimationType animationType, int angle){
        return new Animation(animationType, swordAngleData.get(angle));
    }
}
