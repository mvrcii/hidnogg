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

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setOnCloseRequest(e->{
            Platform.exit();
            System.exit(0);
        });

        Group root = new Group();
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(false);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        canvas = new Canvas(bounds.getWidth()/2, bounds.getHeight()/2);

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


}
