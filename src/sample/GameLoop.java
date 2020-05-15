package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sample.controllers.*;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.world.*;
import sample.interfaces.InputSystem;

import java.util.ArrayList;


public class GameLoop extends Thread implements Runnable {


    private Canvas canvas;
    private GraphicsContext gc;

    public static ArrayList<GameObject> gameObjects = new ArrayList<>();
    public static ArrayList<Controller> gameControllers = new ArrayList<>();


    private PlayerObject player1;
    private PlayerObject player2;

    private SwordObject sword1;
    private SwordObject sword2;

    public GameLoop() {
        this.canvas = Main.canvas;
        initialize();
    }


    private void initialize() {
        gameControllers.add(KeyController.getInstance());
        gameControllers.add(DataController.getInstance());
        gameControllers.add(DirectionController.getInstance());

        gc = canvas.getGraphicsContext2D();

        FPSObject fpsObject = new FPSObject();
        fpsObject.setPrintMode(false);
        gameObjects.add(fpsObject);

        player1 = new PlayerObject(100,100, PlayerType.PLAYER_ONE, Direction.RIGHT);
        gameObjects.add(player1);

        player2 = new PlayerObject(300, 100, PlayerType.PLAYER_TWO, Direction.RIGHT);
        gameObjects.add(player2);

        sword1 = new SwordObject(400,400, Direction.RIGHT, player1);
        player1.setSwordObject(sword1);
        gameObjects.add(sword1);

        sword2 = new SwordObject(400,400, Direction.RIGHT, player2);
        player2.setSwordObject(sword2);
        gameObjects.add(sword2);
    }

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

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private void update(long diffMillis)
    {
        for (GameObject obj : gameObjects)
        {

             if(obj instanceof InputSystem)
             {
                 ((InputSystem) obj).processInput(diffMillis);
             }

            obj.update(diffMillis);
        }

        for(Controller con : gameControllers){
            con.update(diffMillis);
        }

    }


    private void draw() {
        for (GameObject obj : gameObjects) {
            obj.draw(gc);
        }
    }

    private void clearScreen()
    {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


}
