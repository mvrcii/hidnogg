package sample.animation;


import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import sample.world.SwordObject;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FrameData {

    /**
     * Container which holds all the relevant information of a SINGLE frame
     */

    private final Image image;
    private final BufferedImage bufferedImage;
    private ArrayList<Point2D> hitBox;
    private ArrayList<Point2D> hitBoxInverted;
    private Point2D swordStartPoint, swordEndPoint;

    public FrameData(BufferedImage bufferedImage){
        this.image = convertToFxImage(bufferedImage);
        this.bufferedImage = bufferedImage;
        this.swordEndPoint = null;
        this.swordStartPoint = null;
    }

    public static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return new ImageView(wr).getImage();
    }

    public Image getImage() {
        return image;
    }

    public ArrayList<Point2D> getHitBox(Object object) {
        /*if(object instanceof SwordObject){
            BufferedImage tmp = new BufferedImage(this.bufferedImage);

        }*/
        return hitBox;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public ArrayList<Point2D> getHitBoxInverted() {
        return hitBoxInverted;
    }

    public Point2D getSwordStartPoint() {
        return swordStartPoint;
    }

    public Point2D getSwordEndPoint() {
        return swordEndPoint;
    }

    public void setHitBox(ArrayList<Point2D> hitBox) {
        this.hitBox = hitBox;
    }

    public void setHitBoxInverted(ArrayList<Point2D> hitBoxInverted) {
        this.hitBoxInverted = hitBoxInverted;
    }

    public void setSwordStartPoint(Point2D swordStartPoint) {
        this.swordStartPoint = swordStartPoint;
    }

    public void setSwordEndPoint(Point2D swordEndPoint) {
        this.swordEndPoint = swordEndPoint;
    }

}
