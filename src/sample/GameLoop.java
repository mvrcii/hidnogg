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

        Random rnd = new Random();
        double ups = 60;
        double fps = 60;

        long initialTime = System.nanoTime();
        final double timeU = 1000000000 / ups;
        final double timeF = 1000000000 / fps;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();

        while (true) {

            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;

            if (deltaU >= 1) {

                //getInput();
                update();
                ticks++;
                deltaU--;
            }


            if (deltaF >= 1) {
                long b = System.currentTimeMillis();
                clearScreen();


                draw();
                long a = System.currentTimeMillis();

                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                frames = 0;
                ticks = 0;
                timer += 1000;
            }
        }
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
