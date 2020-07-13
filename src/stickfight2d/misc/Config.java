package stickfight2d.misc;

import javafx.scene.input.KeyCode;
import stickfight2d.animation.Animation;
import stickfight2d.controllers.AnimationFactory;
import stickfight2d.enums.AnimationType;

public class Config {


    // PLAYER
    public final static KeySet keySet1 = new KeySet(KeyCode.A,    KeyCode.D,      KeyCode.S,      KeyCode.W,  KeyCode.F,  KeyCode.G);
    public final static KeySet keySet2 = new KeySet(KeyCode.LEFT, KeyCode.RIGHT,  KeyCode.DOWN,   KeyCode.UP, KeyCode.N,  KeyCode.M);

    public static AnimationType INITIAL_ANIMATION_TYPE = AnimationType.PLAYER_IDLE_MEDIUM;
    public static final double T_HOLDUP = 200;
    public static final double T_CROUCH = 200;
    public static final double T_RESPAWN = 2000;

    public static final int KNOCKBACK_VALUE = 6;
    public static final int DROPKICK_VX = 30;
    public static final int JUMP_VY = 20;
    public static boolean swordCollisionParticles = true;

    // MAIN MENU
    public static final String CREDITS = "Team:\n" +
            "Andreas Schermann\tHitBoxes, Collision detection,\n" +
            "\t\t\t\t\tMap obstacles/boundaries \n" +
            "Erwin Kenner\t\t\tGeneral Artwork \n" +
            "Marcel Roth\t\t\tManagement, Animation, Programming \n" +
            "Marcus Waibel\t\t\tCameraController, Sounds\n" +
            "Pascal Schaeffer\t\tMenus, Blood particles, Jump physics\n" +
            "\n" +
            "The Idea:\n" +
            "They might be 5 inches thick, but they are 17 inches of stupid. After peaceful negotiation breaks down, violence erupts. \n" +
            "Stickfight pits two idiots against each other in physics-based combat. Defeat your enemy in order to prove that you are always right, no matter what. \n" +
            "Stick Fight is a physics-based local fighting game where you battle it out as the iconic stick figures from the golden age of the internet. Fight it out against your friends!\n" +
            "\n" +
            "Game Description:\n" +
            "-\tChallenge your friends to a one versus one budget fight\n" +
            "-\tStab your friends without getting arrested\n" +
            "-\tLocal multiplayer, shared screen\n" +
            "-\tPhysics based combat system\n" +
            "-\tPixel accurate hitboxes\n" +
            "-\tFast paced melee combat with exciting highs and lows and close calls\n";

    // MISC
    public static boolean debug_mode = false;
    public static boolean fps_print_mode = true;
    public static double volume = 0.05;

    public Config(){

    }

}
