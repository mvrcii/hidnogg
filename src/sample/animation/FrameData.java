package sample.animation;


import javafx.scene.image.Image;

import java.awt.*;
import java.util.ArrayList;

public class FrameData {

    private Image image;
    private ArrayList<Point> hitBox;

    public FrameData(Image image, ArrayList<Point> hitBox){
        this.image = image;
        this.hitBox = hitBox;
        System.out.println("Created Frame with "+hitBox.size()+" Pixels");
    }

    public Image getImage() {
        return image;
    }


    public ArrayList<Point> getHitBox() {
        return hitBox;
    }
}
