package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

public class Main extends Application {

    public static Canvas canvas;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("2D Stickfight");
        Group root = new Group();
        canvas = new Canvas(500, 500);

        canvas.requestFocus();
        canvas.setFocusTraversable(true);

        primaryStage.setOnCloseRequest(e->{
            Platform.exit();
            System.exit(0);
        });

        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        new GameLoop().start();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
