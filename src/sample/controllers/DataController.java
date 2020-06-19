package sample.controllers;


import sample.animation.Animation;
import sample.animation.AnimationData;
import sample.animation.FrameData;
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
        // SWORD
        basicAnimationData.put(AnimationType.SWORD, new AnimationData(0));
        for (int i = 1; i < 360; i++) {
            swordAngleData.put(i, basicAnimationData.get(AnimationType.SWORD).rotateAnimDataByDegree(i));

            //System.out.println("["+i+"] "+swordAngleData.get(i).getFrames().get(0).getHitBox().size());

            for (FrameData f : swordAngleData.get(i).getFrames()) {
                if(f.getSwordEndPoint() == null){
                    System.out.println(f.getAngle()+" -> "+f.getFrameNumber());
                }
            }
            //System.out.println(swordAngleData.get(i).getFrames().toString());


        }





        // JUMP
        basicAnimationData.put(AnimationType.PLAYER_JUMP_START, new AnimationData(2));
        basicAnimationData.put(AnimationType.PLAYER_JUMP_END, new AnimationData(3));
        basicAnimationData.put(AnimationType.PLAYER_JUMP_PEAK, new AnimationData(4));

        // PLAYER
        basicAnimationData.put(AnimationType.PLAYER_WALK, new AnimationData(1));
        basicAnimationData.put(AnimationType.PLAYER_IDLE_LOW, new AnimationData(5));
        basicAnimationData.put(AnimationType.PLAYER_IDLE_MEDIUM, new AnimationData(6));
        basicAnimationData.put(AnimationType.PLAYER_IDLE_HIGH, new AnimationData(7));
        basicAnimationData.put(AnimationType.PLAYER_IDLE_HOLD_UP, new AnimationData(8));
        basicAnimationData.put(AnimationType.PLAYER_STAB_LOW, new AnimationData(9));
        basicAnimationData.put(AnimationType.PLAYER_STAB_MEDIUM, new AnimationData(10));
        basicAnimationData.put(AnimationType.PLAYER_STAB_HIGH, new AnimationData(11));
        basicAnimationData.put(AnimationType.PLAYER_DYING, new AnimationData(12));

        basicAnimationData.put(AnimationType.PLAYER_STEP_LOW, new AnimationData(10));
        basicAnimationData.put(AnimationType.PLAYER_STEP_MEDIUM, new AnimationData(11));
        basicAnimationData.put(AnimationType.PLAYER_STEP_HIGH, new AnimationData(12));

    }

    @Override
    public void update(long diffMillis) {

    }


    public Animation getAnimation(AnimationType animationType){
        return new Animation(animationType, basicAnimationData.get(animationType));
    }

    // Get the corresponding stab animation to the last idle position
    public Animation getStabAnim(AnimationType animationType){
        AnimationType swordAnimType;
        switch (animationType){
            case PLAYER_IDLE_LOW -> swordAnimType = AnimationType.PLAYER_STAB_LOW;
            case PLAYER_IDLE_MEDIUM -> swordAnimType = AnimationType.PLAYER_STAB_MEDIUM;
            case PLAYER_IDLE_HIGH -> swordAnimType = AnimationType.PLAYER_STAB_HIGH;
            default -> throw new IllegalArgumentException("Wrong usage of method getStabAnim(). Has to be called with an idle animation type!");
        }
        return new Animation(swordAnimType, basicAnimationData.get(swordAnimType));
    }

    // Get the corresponding stab animation to the last idle position
    public Animation getStepAnim(AnimationType lastIdleAnimType){
        AnimationType animType;
        switch (lastIdleAnimType){
            case PLAYER_IDLE_LOW -> animType = AnimationType.PLAYER_STEP_LOW;
            case PLAYER_IDLE_MEDIUM -> animType = AnimationType.PLAYER_STEP_MEDIUM;
            case PLAYER_IDLE_HIGH -> animType = AnimationType.PLAYER_STEP_HIGH;
            default -> throw new IllegalArgumentException("Wrong usage of method getStepAnim(). Has to be called with an idle animation type!");
        }
        return new Animation(animType, basicAnimationData.get(animType));
    }

    public Animation getSwordAnimAngle(int angle){
        if(angle == 0 || angle == 360){
            return getAnimation(AnimationType.SWORD);
        }else if(angle < 0){
            return new Animation(AnimationType.SWORD, swordAngleData.get(360+angle));
        }else{
            return new Animation(AnimationType.SWORD, swordAngleData.get(angle));
        }
    }
}
