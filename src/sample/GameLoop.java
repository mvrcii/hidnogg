package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import sample.animation.AnimationData;
import sample.controllers.*;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.world.*;
import sample.interfaces.InputSystem;

import java.awt.*;
import java.util.ArrayList;


public class GameLoop extends Thread implements Runnable {

    private GraphicsContext gc;

    public static ArrayList<GameObject> gameObjects = new ArrayList<>();
    public static ArrayList<Controller> gameControllers = new ArrayList<>();

    public static final int groundLevel = (int) Main.canvas.getWidth() - 175;

    // KeySet(MOVE_LEFT, MOVE_RIGHT, DOWN, UP, HIT, JUMP)
    private final KeySet keySet1 = new KeySet(KeyCode.A,    KeyCode.D,      KeyCode.S,      KeyCode.W,  KeyCode.F,  KeyCode.G);
    private final KeySet keySet2 = new KeySet(KeyCode.LEFT, KeyCode.RIGHT,  KeyCode.DOWN,   KeyCode.UP, KeyCode.N,  KeyCode.M);

    private final PlayerObject player1 = new PlayerObject(100, groundLevel, PlayerType.PLAYER_ONE, Direction.RIGHT, keySet1);
    private final PlayerObject player2 = new PlayerObject(300, groundLevel, PlayerType.PLAYER_TWO, Direction.RIGHT, keySet2);

    private SwordObject sword1 = new SwordObject(400, 400, Direction.RIGHT, player1);
    private SwordObject sword2 = new SwordObject(400, 400, Direction.RIGHT, player2);

    private RectangleObstacle ground = new RectangleObstacle(0, groundLevel, (int) Main.canvas.getWidth(),(int) Main.canvas.getWidth() - groundLevel, Color.GREEN);

    private final FPSObject fpsObject = new FPSObject();

    public GameLoop() {
        initialize();
    }


    private void initialize() {
        gameControllers.add(KeyController.getInstance());
        gameControllers.add(DataController.getInstance());
        gameControllers.add(DirectionController.getInstance());
        gameControllers.add(CameraController.getInstance());

        gc = Main.canvas.getGraphicsContext2D();

        fpsObject.setPrintMode(false);
        gameObjects.add(fpsObject);
        gameObjects.add(ground);

        gameObjects.add(player1);
        gameObjects.add(player2);

        gameObjects.add(sword1);
        gameObjects.add(sword2);

        player1.setSwordObject(sword1);
        player2.setSwordObject(sword2);

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
/*
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

 */
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
        for (GameObject obj : gameObjects) {

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
        for (GameObject obj : gameObjects) {
            obj.draw(gc);
        }
    }

    private void clearScreen() {
        gc.clearRect(0, 0, Main.canvas.getWidth(), Main.canvas.getHeight());
    }


}
