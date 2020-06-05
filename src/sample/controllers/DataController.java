package sample.controllers;


import sample.animation.Animation;
import sample.animation.AnimationData;
import sample.enums.AnimationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    public final <S, T> List<S> getKeysForValue(final HashMap<S, T> hashMap, final T value) {
        return hashMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private DataController()
    {
        // SWORD
        basicAnimationData.put(AnimationType.SWORD, new AnimationData(0));
        System.out.println("[BASIC ANIMATION]:"+basicAnimationData.get(AnimationType.SWORD).getFrames().get(0).getHitBox().size());
        for (int i = 1; i <= 360; i+=1) {
            swordAngleData.put(i, basicAnimationData.get(AnimationType.SWORD).rotateAnimDataByDegree(i));

            System.out.println("["+i+"] "+swordAngleData.get(i).getFrames().get(0).getHitBox().size());
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
        basicAnimationData.put(AnimationType.PLAYER_DIEING, new AnimationData(12));

    }

    @Override
    public void update(long diffMillis) {

    }


    public Animation getSwordAnimAngle(AnimationType animationType){
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

    public Animation getSwordAnimAngle(int angle){
        if(angle == 0 || angle == 360){
            return getSwordAnimAngle(AnimationType.SWORD);
        }else if(angle < 0){
            return new Animation(AnimationType.SWORD, swordAngleData.get(360+angle));
        }else{
            return new Animation(AnimationType.SWORD, swordAngleData.get(angle));
        }
    }
}
