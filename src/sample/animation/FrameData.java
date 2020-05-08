package sample.animation;


import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FrameData {

    /**
     * Container which holds all the relevant information of a SINGLE frame
     */

    private Image image;
    private BufferedImage bufferedImage;
    private ArrayList<Point2D> hitBox;
    private ArrayList<Point2D> hitBoxInverted;
    private Point2D swordStartPoint, swordEndPoint;
    private int angle = 0;

    public FrameData(BufferedImage bufferedImage){
        this.image = convertToFxImage(bufferedImage);
        this.bufferedImage = bufferedImage;
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


    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.image = convertToFxImage(bufferedImage);
    }

    public Image getImage() {
        return image;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public ArrayList<Point2D> getHitBox() {
        return hitBox;
    }

    public void setHitBox(ArrayList<Point2D> hitBox) {
        this.hitBox = hitBox;
    }

    public ArrayList<Point2D> getHitBoxInverted() {
        return hitBoxInverted;
    }

    public void setHitBoxInverted(ArrayList<Point2D> hitBoxInverted) {
        this.hitBoxInverted = hitBoxInverted;
    }

    public Point2D getSwordStartPoint() {
        return swordStartPoint;
    }

    public void setSwordStartPoint(Point2D swordStartPoint) {
        this.swordStartPoint = swordStartPoint;
    }

    public Point2D getSwordEndPoint() {
        return swordEndPoint;
    }

    public void setSwordEndPoint(Point2D swordEndPoint) {
        this.swordEndPoint = swordEndPoint;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public String toString(){
        return "Image exists: "+!(image==null)+"\n" +
                "BufferedImage exists: "+!(bufferedImage==null)+"\n" +
                "HitBox size: "+hitBox.size()+"\n" +
                "HitBox Inverted size: "+hitBoxInverted.size()+"\n" +
                "Anker Start exists: "+!(swordStartPoint==null)+"\n" +
                "Anker End exists: "+!(swordEndPoint==null)+"\n" +
                "Sprite Angle: "+angle+"\n\n";
    }
}
