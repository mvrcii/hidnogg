package stickfight2d;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    public static Canvas canvas;
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

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        //canvas = new Canvas(bounds.getWidth()/2, bounds.getHeight()/2);
        canvas = new Canvas(SUB_MAP_WIDTH, SUB_MAP_HEIGHT);

        canvas.requestFocus();
        canvas.setFocusTraversable(true);
        canvas.setScaleX(1.5);
        canvas.setScaleY(1.5);

        root.getChildren().add(canvas);

        primaryStage.show();

        new GameLoop().start();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public static Group getRoot() {
        return root;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
