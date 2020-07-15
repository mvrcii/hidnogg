package stickfight2d;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import stickfight2d.controllers.MenuController;

public class Main extends Application {

    public static Canvas canvas, copy;
    private static Group root;
    private static Stage primaryStage;

    private final int SUB_MAP_WIDTH = 1032;
    private final int SUB_MAP_HEIGHT = 810;

    @Override
    public void start(Stage primaryStage) {

        Main.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(e->{
            Platform.exit();
            System.exit(0);
        });


        root = new Group();
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(false);


        canvas = new Canvas(SUB_MAP_WIDTH, SUB_MAP_HEIGHT);
        copy = new Canvas(SUB_MAP_WIDTH, SUB_MAP_HEIGHT);


        canvas.requestFocus();
        canvas.setFocusTraversable(true);
        canvas.setScaleX(1);
        canvas.setScaleY(1);

        copy.setScaleX(1.5);
        copy.setScaleY(1.5);

        root.getChildren().add(canvas);
        root.getChildren().add(copy);

        primaryStage.show();

        new GameLoop().start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void copyCanvas() {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Platform.runLater(() -> {
            WritableImage image = canvas.snapshot(params, null);
            copy.getGraphicsContext2D().drawImage(image, 0, 0);
        });
    }

    public static Group getRoot() {
        return root;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
