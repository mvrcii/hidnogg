package stickfight2d.misc;

public class Debugger {


    public static void log(Object o){
        if(Config.debug_mode){
            System.out.println(o.toString());
        }
    }
}
