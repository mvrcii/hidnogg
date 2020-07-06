package stickfight2d;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;
import stickfight2d.controllers.*;
import stickfight2d.enums.SoundType;
import stickfight2d.interfaces.InputSystem;
import stickfight2d.world.GameObject;
import stickfight2d.world.WorldObject;

import java.util.ArrayList;


public class GameLoop extends Thread implements Runnable {

    private final GraphicsContext gc = Main.canvas.getGraphicsContext2D();
    private final ArrayList<Controller> gameControllers = new ArrayList<>();

    public static WorldObject currentLevel;

    private static Text counterText;
    private static boolean counterOn = false;
    private int counterState = 0;

    private double diffTimeMs = 0;

    public static Music currentMusic;

    public GameLoop() {
        TinySound.init();
        gameControllers.add(KeyController.getInstance());
        gameControllers.add(DataController.getInstance());
        gameControllers.add(DirectionController.getInstance());
        gameControllers.add(CameraController.getInstance());
        gameControllers.add(SoundController.getInstance());

        currentLevel = new WorldObject();
        currentLevel.initObjects();

        gameControllers.add(CollisionController.getInstance());
        gameControllers.add(MenuController.getInstance());

        currentMusic = SoundController.getInstance().getMusic(SoundType.THEME_01); // Music theme
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
        if(counterOn){
            diffTimeMs += diffMillis;
        }

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
        updateCounter();
    }


    private void draw() {
        for (GameObject obj : currentLevel.getGameObjects()) {
            obj.draw(gc);
        }
    }

    private void clearScreen() {
        gc.clearRect(0, 0, Main.canvas.getWidth(), Main.canvas.getHeight());
    }

    private void updateCounter(){
        if(counterOn){
            if(diffTimeMs/1000 >= 3 && counterState == 0){
                counterState = 1;
                counterText.setX(Main.getPrimaryStage().getWidth()/2-90);
                counterText.setFont(Font.font("Verdana", 80));
                counterText.setText("3");
            }else if(diffTimeMs/1000 >= 4 && counterState == 1){
                counterState = 2;
                counterText.setText("2");
            }else if(diffTimeMs/1000 >= 5 && counterState == 2){
                counterState = 3;
                counterText.setText("1");
            }else if(diffTimeMs/1000 >= 6 && counterState == 3){
                counterState = 4;
                counterText.setText("GO");
            }else if(diffTimeMs/1000 >= 7 && counterState == 4){
                counterState = 5;
                counterText.setText("");
                KeyController.getInstance().setKeyPressBlockedP1(false);
                KeyController.getInstance().setKeyPressBlockedP2(false);
                counterOn = false;
            }
        }
    }

    public static void startCounter() {
        Stage stage = Main.getPrimaryStage();
        counterOn = true;
        counterText = new Text("Get ready!");
        counterText.setTextAlignment(TextAlignment.CENTER);
        counterText.setX(stage.getWidth()/2-140);
        counterText.setY(stage.getHeight()/2);
        counterText.setFill(Color.LIGHTGREEN);
        counterText.setFont(Font.font("Verdana", 50));
        Main.getRoot().getChildren().add(counterText);

        KeyController.getInstance().setKeyPressBlockedP1(true);
        KeyController.getInstance().setKeyPressBlockedP2(true);
    }

}
