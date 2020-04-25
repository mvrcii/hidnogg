package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.Random;

public class GameLoop extends Thread implements Runnable {

    private Canvas canvas;
    private GraphicsContext gc;

    public GameLoop(Canvas canvas){
        this.canvas = canvas;
        initialize();
    }


    public void run() {


    }


    private void initialize()
    {
        gc = canvas.getGraphicsContext2D();
    }

    private void update(){
    }


    private void draw() {
    }

    private void clearScreen(){
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
