package stickfight2d.misc;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import stickfight2d.Main;
import stickfight2d.enums.DirectionType;
import stickfight2d.world.GameObject;


public class FPSObject extends GameObject {

    private final Text displayText = new Text();
    private boolean printMode;
    private long time = 0;
    private int updatesPerSecond = 0;


    public FPSObject(){
        super(0,0, DirectionType.RIGHT);
        printMode = Config.fps_print_mode;


            Stage stage = Main.getPrimaryStage();
            displayText.setText("60 FPS");
            displayText.setX(stage.getWidth()-90);
            displayText.setY(25);
            displayText.setFill(Color.AQUAMARINE);
            displayText.setFont(Font.font("Verdana", 18));
            Main.getRoot().getChildren().add(displayText);

    }

    @Override
    public void update(long diffMillis) {
        time += diffMillis;
        updatesPerSecond++;
        if(time > 1000){
            time -= 1000;
            displayText.setText(updatesPerSecond+" FPS");
            updatesPerSecond = 0;
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        displayText.setVisible(printMode);
    }

    public void setPrintMode(boolean b){
        printMode = b;
    }


    public void printFPS(){
        System.out.println("Frames/second: "+updatesPerSecond);
    }
}
