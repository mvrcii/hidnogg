package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sample.controllers.AnimationController;
import sample.controllers.KeyController;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.world.GameObject;
import sample.interfaces.InputSystem;
import sample.world.MoveableObject;
import sample.world.PlayerObject;

import java.util.ArrayList;


public class GameLoop extends Thread implements Runnable {


    private double diffSeconds;
    private Canvas canvas;
    private GraphicsContext gc;

    public static ArrayList<GameObject> gameObjects;

    private PlayerObject player1;
    private PlayerObject player2;

    public GameLoop(Canvas canvas) {
        this.canvas = canvas;
        initialize();
    }


    public void run() {

        long lastTick = System.currentTimeMillis();

        while (true) {

            // Elapsed time
            long currentTick = System.currentTimeMillis();
            diffSeconds = (currentTick - lastTick) / 1000.0;
            lastTick = currentTick;


            // Update objects
            update();

            // Clear
            clearScreen();

            // Draw all objects
            draw();

        }
    }




    private void initialize() {
        KeyController.getInstance();
        AnimationController.getInstance();

        gc = canvas.getGraphicsContext2D();
        gameObjects = new ArrayList<GameObject>();

        player1 = new PlayerObject(100,100, PlayerType.PLAYER_ONE, Direction.RIGHT);
        player2 = new PlayerObject(300, 100, PlayerType.PLAYER_TWO, Direction.RIGHT);

        gameObjects.add(player1);
        gameObjects.add(player2);
    }


    private void update() {
        for (GameObject obj : gameObjects) {
            if(obj instanceof MoveableObject){
                if(obj instanceof InputSystem){
                    ((InputSystem) obj).processInput();
                }
                ((MoveableObject) obj).update(diffSeconds);
            }
            obj.update();
        }
        KeyController.getInstance().updateKeyController();
    }


    private void draw() {
        for (GameObject obj : gameObjects) {
            obj.draw(gc);
        }
    }

    private void clearScreen() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
