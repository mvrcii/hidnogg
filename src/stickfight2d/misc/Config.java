package stickfight2d.misc;

import javafx.scene.input.KeyCode;

public class Config {



    public final static KeySet keySet1 = new KeySet(KeyCode.A,    KeyCode.D,      KeyCode.S,      KeyCode.W,  KeyCode.F,  KeyCode.G);
    public final static KeySet keySet2 = new KeySet(KeyCode.LEFT, KeyCode.RIGHT,  KeyCode.DOWN,   KeyCode.UP, KeyCode.N,  KeyCode.M);

    public static final double T_HOLDUP = 200;
    public static final double T_CROUCH = 200;
    public static final double T_RESPAWN = 2000;

    public static boolean debug_mode = false;

    public Config(){

    }

}
