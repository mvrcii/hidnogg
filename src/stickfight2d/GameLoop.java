package stickfight2d;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;
import stickfight2d.controllers.*;
import stickfight2d.enums.SoundType;
import stickfight2d.interfaces.InputSystem;
import stickfight2d.misc.Config;
import stickfight2d.world.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GameLoop extends Thread implements Runnable {

    private final GraphicsContext gc = Main.canvas.getGraphicsContext2D();
    public static final ArrayList<Controller> gameControllers = new ArrayList<>();

    public static WorldObject currentLevel;

    private static Text counterText;
    private static boolean counterOn = false;
    private static int counterState = 0;

    private static double diffTimeMs = 0;

    public static Music currentMusic;

    public GameLoop() {
        TinySound.init();
        gameControllers.add(KeyController.getInstance());
        gameControllers.add(AnimationFactory.getInstance());
        gameControllers.add(DirectionController.getInstance());
        gameControllers.add(CameraController.getInstance());
        gameControllers.add(SoundController.getInstance());

        currentLevel = new WorldObject();
        currentLevel.initObjects();

        gameControllers.add(CollisionController.getInstance());

        gameControllers.add(MenuController.getInstance());

        currentMusic = SoundController.getInstance().getMusic(SoundType.MUSIC_THEME_INGAME); // Music theme
    }


    private static final float interval = 1000.0f / 60;

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
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
            Main.copyCanvas();

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

        //determineCurrentObjects();

        if (counterOn) {
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
            Platform.runLater(() -> obj.draw(gc));
        }
    }

    private void clearScreen() {
        gc.clearRect(0, 0, Main.canvas.getWidth(), Main.canvas.getHeight());
    }


    private void determineCurrentObjects() {

        Map<Class<? extends GameObject>, List<GameObject>> counted = currentLevel.getGameObjects().stream().collect(Collectors.groupingBy(gameObject -> {
            if (gameObject instanceof RectangleObstacle) return RectangleObstacle.class;
            if (gameObject instanceof PlayerObject) return PlayerObject.class;
            if (gameObject instanceof SwordObject) return SwordObject.class;
            if (gameObject instanceof ParticleEmitter) return ParticleEmitter.class;
            if (gameObject instanceof BackgroundObject) return BackgroundObject.class;
            return GameObject.class;
        }));

        int n = 0;
        if (counted.get(ParticleEmitter.class) != null) {
            n = counted.get(ParticleEmitter.class).size();
        }

        System.out.println(MessageFormat.format("\nGameObjects {0}:" +
                        "\n{1} RectangleObstacle" +
                        "\n{2} PlayerObject" +
                        "\n{3} SwordObject" +
                        "\n{4} ParticleEmitter" +
                        "\n{5} BackgroundObject",
                counted.values().stream().mapToInt(List::size).sum(),
                counted.get(RectangleObstacle.class).size(),
                counted.get(PlayerObject.class).size(),
                counted.get(SwordObject.class).size(),
                n,
                counted.get(BackgroundObject.class).size()));
    }

    private void updateCounter() {
        Platform.runLater(() -> {
            if (counterOn) {
                if (diffTimeMs / 1000 >= 2 && counterState == 0) {
                    counterState = 1;
                    counterText.setX(Main.getPrimaryStage().getWidth() / 2 - 90);
                    counterText.setFont(Font.loadFont(Config.FONT_PATH, 80));
                    counterText.setText("3");
                } else if (diffTimeMs / 1000 >= 3 && counterState == 1) {
                    counterState = 2;
                    counterText.setText("2");
                } else if (diffTimeMs / 1000 >= 4 && counterState == 2) {
                    counterState = 3;
                    counterText.setText("1");
                } else if (diffTimeMs / 1000 >= 5 && counterState == 3) {
                    counterState = 4;
                    counterText.setText("GO");
                } else if (diffTimeMs / 1000 >= 6 && counterState == 4) {
                    counterState = 5;
                    counterText.setText("");
                    KeyController.getInstance().setKeyPressBlockedP1(false);
                    KeyController.getInstance().setKeyPressBlockedP2(false);
                    counterOn = false;
                }
            }
        });
    }

    public static void startCounter() {
        counterOn = true;
        counterState = 0;
        diffTimeMs = 0;

        Platform.runLater(() -> {
            VBox counterBox = new VBox();
            counterBox.layoutXProperty().bind(Main.getPrimaryStage().widthProperty().divide(2).subtract(counterBox.widthProperty().divide(2)));
            counterBox.layoutYProperty().bind(Main.getPrimaryStage().heightProperty().divide(2).subtract(counterBox.heightProperty().divide(2)));
            counterText = new Text("Get ready!");
            counterText.setTextAlignment(TextAlignment.CENTER);
            counterText.setFill(Config.FONT_COLOR);
            counterText.setFont(Font.loadFont(Config.FONT_PATH, 50));
            counterBox.getChildren().add(counterText);
            Main.getRoot().getChildren().add(counterBox);
        });

        KeyController.getInstance().setKeyPressBlockedP1(true);
        KeyController.getInstance().setKeyPressBlockedP2(true);
    }

}
