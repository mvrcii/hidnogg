package stickfight2d.animation;


import javafx.geometry.Point2D;
import stickfight2d.controllers.CollisionController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AnimationData {

    private boolean imageTransparent = false;
    private ArrayList<FrameData> frames = new ArrayList<>();

    public static final int TILE_SIZE = 64;
    private static final String SPRITE_SHEET_PATH = "src/spritesheet.png";

    private static final int black = new Color(0, 0, 0).getRGB();
    private static final int red = new Color(255, 0, 0).getRGB();
    private static final int green = new Color(0, 255, 0).getRGB();
    private static final int blue = new Color(0, 0, 255).getRGB();
    private static final int blueTest = new Color(47,47,255).getRGB(); // TODO :: solution for now, pixels should be changed
    private static final int blueTest2 = new Color(35,35,234).getRGB();

    private static int previousGreen = 0; // prevent multiple calculations of sword length

    public AnimationData() {

    }

    public AnimationData(int row) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
            int i;
            for (i = 0; (i * TILE_SIZE) < spriteSheet.getWidth(); ) {
                BufferedImage bf = spriteSheet.getSubimage(i * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (!imageTransparent) {
                    FrameData frame = calcFrameData(bf);

                    if (imageTransparent) {
                        break;
                    }
                    frame.setFrameNumber(i);
                    frames.add(frame);
                    i++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AnimationData rotateAnimDataByDegree(int angle) {
        AnimationData newAnimData = new AnimationData();
        ArrayList<FrameData> newFrameList = new ArrayList<>();
        int i = 0;
        for (FrameData oldFrame : frames) {
            BufferedImage bf_ = rotateBfImg(oldFrame.getBufferedImage(), angle, oldFrame.getSwordStartPoint());
            double width_ = bf_.getWidth();

            FrameData newFrame = calcFrameData(bf_);
            newFrame.setAngle(angle);
            newFrame.setFrameNumber(i);
            newFrame.setBufferedImage(rotateBfImg(oldFrame.getBufferedImage(), angle, oldFrame.getSwordStartPoint()));

            // SwordStartPoint Normal and Inverted have to be set here, because there is a chance that the green color
            // value gets lost while rotating the sword
            newFrame.setSwordStartPoint(oldFrame.getSwordStartPoint());
            newFrame.setSwordStartPointInverted(new Point2D(width_ -
                    newFrame.getSwordStartPoint().getX(), newFrame.getSwordStartPoint().getY()));

            // Calculating new SwordEndPoint
            double x = oldFrame.getSwordEndPoint().getX();
            double y = oldFrame.getSwordEndPoint().getY();
            double x_ = x * Math.cos(angle) - y * Math.sin(angle);
            double y_ = x * Math.sin(angle) - y * Math.cos(angle);
            newFrame.setSwordEndPoint(new Point2D(x_, y_));

            newFrameList.add(newFrame);
            i++;
        }
        newAnimData.frames = newFrameList;
        return newAnimData;
    }

    // Helper Method for rotateAnimDataByDegree()
    private BufferedImage rotateBfImg(BufferedImage bf, int angle, Point2D anker) {
        double radian = Math.toRadians(angle);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-radian, anker.getX(), anker.getY());

        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage rotated = new BufferedImage(bf.getWidth(), bf.getHeight(), bf.getType());
        return affineTransformOp.filter(bf, rotated);
    }



    private FrameData calcFrameData(BufferedImage bufferedImage) {
        // Data that has to be calculated
        FrameData frameData = new FrameData(bufferedImage);
        ArrayList<Point2D> hitBox = new ArrayList<>();
        ArrayList<Point2D> hitBoxInverted = new ArrayList<>();

        // For frame picking
        imageTransparent = true;

        for (int row = 0; row < bufferedImage.getHeight(); row++) { // y-coordinates

            boolean foundBlackLeft = false; // only calculate outer left-hitBox pixels
            int lastBlackPixel_x = -1; // for outer right hitBox pixels

            // Left hitBox pixels ++ green and blue pixels
            for (int col = 0; col < bufferedImage.getWidth(); col++) { // x-coordinates
                int currentRGB = bufferedImage.getRGB(col, row);

                if (col == bufferedImage.getWidth() - 1 && lastBlackPixel_x != -1) { // Add outer right hitBox pixel, if it exists
                    hitBox.add(new Point2D(lastBlackPixel_x, row));
                    hitBoxInverted.add(new Point2D(bufferedImage.getWidth() - lastBlackPixel_x, row));
                }

                if ((currentRGB >> 24) == 0x00) // transparent pixel
                    continue;

                imageTransparent = false; // non-transparent image

                if (currentRGB == black) { // player pixel
                    lastBlackPixel_x = col;

                    if (!foundBlackLeft) {
                        foundBlackLeft = true;
                        hitBox.add(new Point2D(col, row));
                        hitBoxInverted.add(new Point2D(bufferedImage.getWidth() - col, row));
                    }

                } else if (currentRGB == green) { // start-mountPoint
                    frameData.setSwordStartPoint(new Point2D(col, row));
                    frameData.setSwordStartPointInverted(new Point2D(bufferedImage.getWidth() - col, row));
                    previousGreen++; // for swordLength calculation

                } else if (currentRGB == blue || currentRGB == blueTest || currentRGB == blueTest2) { // end mountPoint
                    frameData.setSwordEndPoint(new Point2D(col, row));

                } else if (currentRGB == red && previousGreen == 1) {
                    calculateSwordLength(col, row, bufferedImage);
                    previousGreen++;
                }
            }
        }

        frameData.setHitBox(hitBox);
        frameData.setHitBoxInverted(hitBoxInverted);
        return frameData;
    }

    private void calculateSwordLength(int col, int row, BufferedImage bufferedImage) { // only called once in calcFrameData
        int swordLength = 1;
        while (bufferedImage.getRGB(col, row) >> 24 != 0x00) {
            swordLength++;
            col++;
        }
        CollisionController.setSwordLength(swordLength); // static access, instance cannot be created at this stage
    }

    /**
     * Getter Methods
     */
    public ArrayList<FrameData> getFrames() {
        if (frames.size() > 0) {
            return frames;
        } else {
            throw new IllegalArgumentException("There are no frames in the frameData Arraylist.");
        }
    }

}
