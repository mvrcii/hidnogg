package sample.world;

import javafx.scene.canvas.GraphicsContext;
import sample.enums.Direction;


public class FPSObject extends GameObject {


    private boolean printMode;
    private long time = 0;
    private int updatesPerSecond = 0;


    public FPSObject(){
        super(0,0, Direction.RIGHT);
        printMode = true;
    }

    @Override
    public void update(long diffMillis) {
        time += diffMillis;
        updatesPerSecond++;
        if(time > 1000){
            time -= 1000;
            if(printMode){
                printFPS();
            }
            updatesPerSecond = 0;
        }
    }

    @Override
    public void draw(GraphicsContext gc) {

    }

    public void setPrintMode(boolean b){
        printMode = b;
    }


    public void printFPS(){
        System.out.println("Frames/second: "+updatesPerSecond);
    }
}
