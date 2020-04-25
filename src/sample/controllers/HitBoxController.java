package sample.controllers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class HitBoxController {

    private ArrayList<Point> hitBox;
    private ArrayList<Point> invertedHitBox;

    // SINGLETON
    private static HitBoxController instance;

    public static HitBoxController getInstance()
    {
        if(instance == null)
        {
            instance = new HitBoxController();
        }
        return instance;
    }


    private HitBoxController(){
        // TODO
    }

    /*
    // Adds a Point(x,y) for each hitBox pixel to the hitBox ArrayList
    private ArrayList<Point> calcHitBoxPixels(BufferedImage image) {
        ArrayList<Point> hitBox = new ArrayList<>();
        for (int y = 0; y < image.getWidth(); y++) {
            for (int x = 0; x < image.getHeight(); x++) {
                int pixel = image.getRGB(x, y);
                boolean transparent = ((pixel >> 24) == 0x00);
                if (!transparent) {
                    hitBox.add(new Point(x, y));
                }
            }
        }
        return hitBox;
    }

    private ArrayList<Point> calcOutlineHitBoxPixels(BufferedImage bufferedImage){
        ArrayList<Point> outlineHitBoxPixels = new ArrayList<>();
        for (int y = 1; y < bufferedImage.getWidth(); y++) {
            for (int x = 1; x < bufferedImage.getHeight(); x++) {
                int pixel = bufferedImage.getRGB(x, y);
                if(!checkPixelTransparency(pixel) && checkDirectNeighbours(bufferedImage, x, y)) {
                    outlineHitBoxPixels.add(new Point(x,y));
                }
            }
        }
        return outlineHitBoxPixels;
    }
    */

    public ArrayList<Point> getHitBox(BufferedImage sprite){
        // TODO: calcHitBox();
        // TODO: Must return NULL if every pixel is transparent!
        return new ArrayList<Point>();
    }

    public ArrayList<Point> getInvertedHitBox(){
        // TODO: calcInvertedHitBox();
        return new ArrayList<Point>();
    }

}
