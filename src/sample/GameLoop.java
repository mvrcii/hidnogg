package sample;

import javafx.scene.canvas.GraphicsContext;
import sample.controllers.*;
import sample.enums.LevelType;
import sample.world.*;
import sample.interfaces.InputSystem;

import java.util.ArrayList;


public class GameLoop extends Thread implements Runnable {

    private final GraphicsContext gc = Main.canvas.getGraphicsContext2D();
    private final ArrayList<Controller> gameControllers = new ArrayList<>();


    public static WorldObject currentLevel;
    private Config cfg;

    public GameLoop() {
        gameControllers.add(KeyController.getInstance());
        gameControllers.add(DataController.getInstance());
        gameControllers.add(DirectionController.getInstance());
        gameControllers.add(CameraController.getInstance());

        WorldObject level1 = new WorldObject(LevelType.LEVEL_ONE);
        currentLevel = level1;

        gameControllers.add(CollisionController.getInstance());
    }


    private final float interval = 1000.0f / 60;

    public void run() {

        long lastTick = System.currentTimeMillis();

        while (true) {

            // Elapsed time
            long currentTick = System.currentTimeMillis();
            long diffMillis = currentTick - lastTick;
            lastTick = currentTick;


            update(diffMillis);
            clearScreen();
            draw();

            // Adjust timing if necessary
            currentTick = System.currentTimeMillis() - currentTick;
            if (currentTick < interval) {
                try {
                    Thread.sleep((long) (interval - currentTick));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void update(long diffMillis) {
        for (GameObject obj : currentLevel.getGameObjects()) {

            if (obj instanceof InputSystem) {
                ((InputSystem) obj).processInput(diffMillis);
            }

            obj.update(diffMillis);
        }

        for (Controller con : gameControllers) {
            con.update(diffMillis);
        }

    }


    private void draw() {
        for (GameObject obj : currentLevel.getGameObjects()) {
            obj.draw(gc);
        }
    }

    private void clearScreen() {
        gc.clearRect(0, 0, Main.canvas.getWidth(), Main.canvas.getHeight());
    }


}
