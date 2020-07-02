package stickfight2d;

import javafx.scene.canvas.GraphicsContext;
import stickfight2d.controllers.*;
import stickfight2d.enums.LevelType;
import stickfight2d.misc.Config;
import stickfight2d.misc.Debugger;
import stickfight2d.world.*;
import stickfight2d.interfaces.InputSystem;

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

        currentLevel = new WorldObject(LevelType.LEVEL_ONE);
        currentLevel.initObjects();

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

        currentLevel.refreshGameObjects();

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
