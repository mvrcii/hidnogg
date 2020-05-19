package sample.animation;


import javafx.geometry.Point2D;
import sample.enums.Direction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AnimationData {

    private boolean imageTransparent;
    private ArrayList<FrameData> frames = new ArrayList<>();

    private static final int TILE_SIZE = 64;
    private static final String SPRITE_SHEET_PATH = "src/spritesheet.png";

    private final int black = new Color(0,0,0).getRGB();
    private final int white = new Color(255,255,255).getRGB();
    private final int green = new Color(0,255,0).getRGB();
    private final int blue = new Color(0,0,255).getRGB();
    private final int red = new Color(255,0,0).getRGB();

    public AnimationData(){

    }

    public AnimationData(int row){
        try{
            BufferedImage spriteSheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
            for (int i = 0; (i * TILE_SIZE) < spriteSheet.getWidth(); ) {
                BufferedImage bf = spriteSheet.getSubimage(i * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (!imageTransparent) {
                    FrameData frame = calcFrameData(bf);
                    if(frame.getSwordStartPoint() == null || frame.getSwordEndPoint() == null){
                        break;
                    }
                    frames.add(frame);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public AnimationData rotate(int angle) {
        AnimationData newAnimData = new AnimationData();
        ArrayList<FrameData> newFrameList = new ArrayList<>();
        for (FrameData oldFrame: frames) {
            BufferedImage newBf = rotateBfImg(oldFrame.getBufferedImage(), angle, oldFrame.getSwordStartPoint());
            FrameData newFrame = calcFrameData(newBf);
            newFrame.setAngle(angle);
            newFrame.setBufferedImage(newBf);
            newFrame.setSwordStartPoint(oldFrame.getSwordStartPoint());
            newFrameList.add(newFrame);
        }
        newAnimData.frames = newFrameList;
        return newAnimData;
    }

    private BufferedImage rotateBfImg(BufferedImage bf, int angle, Point2D anker) {
        double radian = Math.toRadians(angle);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-radian, anker.getX(), anker.getY());

        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage rotated = new BufferedImage(bf.getWidth(),bf.getHeight(), bf.getType());
        rotated = affineTransformOp.filter(bf,rotated);

        return rotated;
    }



    private FrameData calcFrameData(BufferedImage bf){
        FrameData frameData = new FrameData(bf);
        ArrayList<Point2D> hitBox = new ArrayList<>();
        ArrayList<Point2D> hitBoxInverted = new ArrayList<>();
        imageTransparent = true;

        for (int y = 0; y < bf.getWidth(); y++) {
            for (int x = 0; x < bf.getHeight(); x++) {
                int pixel = bf.getRGB(x, y);
                if (!isTransparent(pixel)) {
                    imageTransparent = false;
                    //System.out.println(new Point(x,y).toString());
                    if (pixel == red) {
                        if (checkDirectNeighbours(bf, x, y)) {
                            hitBox.add(new Point2D(x, y));
                            hitBoxInverted.add(new Point2D(bf.getWidth() - x, bf.getHeight() - y));
                        }
                    } else if (pixel == green){
                        frameData.setSwordStartPoint(new Point2D(x, y));
                    } else if (pixel == blue) {
                        frameData.setSwordEndPoint(new Point2D(x, y));
                    }
                }
            }
        }
        frameData.setHitBox(hitBox);
        frameData.setHitBoxInverted(hitBoxInverted);
        return frameData;
    }




    /**
     * Help Methods
     */
    private boolean checkDirectNeighbours(BufferedImage image, int x, int y){
        if(x>0){
            if(isTransparent(image.getRGB(x-1, y))) {
                return true;
            }
        }
        if(y>0){
            if(isTransparent(image.getRGB(x, y-1))) {
                return true;
            }
        }
        if(x <= image.getWidth()){
            if(isTransparent(image.getRGB(x+1, y))){
                return true;
            }
        }
        if(y <= image.getHeight()){
            if(isTransparent(image.getRGB(x, y+1))){
                return true;
            }
        }
        return false;
    }

    private boolean isTransparent(int pixel){
        return ((pixel>>24) == 0x00);
    }

    /**
     * Getter Methods
     */
    public ArrayList<FrameData> getFrames() {
        return frames;
    }

}
