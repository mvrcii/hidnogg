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

    private boolean imageTransparent = false;
    private ArrayList<FrameData> frames = new ArrayList<>();

    private static final int TILE_SIZE = 64;
    private static final String SPRITE_SHEET_PATH = "src/test.png";

    private final int black = new Color(0, 0, 0).getRGB();
    private final int white = new Color(255, 255, 255).getRGB();
    private final int green = new Color(0, 255, 0).getRGB();
    private final int blue = new Color(0, 0, 255).getRGB();
    private final int red = new Color(255, 0, 0).getRGB();

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

                    frames.add(frame);
                    i++;
                }
            }
            System.out.println("Row "+row+" with "+i+" Sprites fully loaded.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AnimationData rotateAnimDataByDegree(int angle) {
        AnimationData newAnimData = new AnimationData();
        ArrayList<FrameData> newFrameList = new ArrayList<>();
        for (FrameData oldFrame : frames) {
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

    // Helper Method for rotateAnimDataByDegree()
    private BufferedImage rotateBfImg(BufferedImage bf, int angle, Point2D anker) {
        double radian = Math.toRadians(angle);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(-radian, anker.getX(), anker.getY());

        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage rotated = new BufferedImage(bf.getWidth(), bf.getHeight(), bf.getType());
        rotated = affineTransformOp.filter(bf, rotated);

        return rotated;
    }

    private FrameData calcFrameData(BufferedImage bufferedImage) {
        FrameData frameData = new FrameData(bufferedImage);
        ArrayList<Point2D> hitBox = new ArrayList<>();
        ArrayList<Point2D> hitBoxInverted = new ArrayList<>();
        imageTransparent = true;

        for (int row = 0; row < bufferedImage.getHeight(); row++) { // TODO :: Deal with red pixels

            boolean foundBlackLeft = false;
            int lastBlackPixel_x = -1;

            // Left hitBox pixels ++ green and blue pixels
            for (int col = 0; col < bufferedImage.getWidth(); col++) {
                int currentRGB = bufferedImage.getRGB(col, row);

                if (col == bufferedImage.getWidth() - 1 && lastBlackPixel_x != -1) {
                    hitBox.add(new Point2D(lastBlackPixel_x, row));
                    hitBoxInverted.add(new Point2D(bufferedImage.getWidth() - lastBlackPixel_x, row));
                }

                if ((currentRGB >> 24) == 0x00) {
                    continue;
                }

                imageTransparent = false;

                if (currentRGB == black) {
                    lastBlackPixel_x = col;

                    if(!foundBlackLeft) {
                        foundBlackLeft = true;
                        hitBox.add(new Point2D(col, row));
                        hitBoxInverted.add(new Point2D(bufferedImage.getWidth() - col, row));
                    }

                } else if (currentRGB == green) {
                    frameData.setSwordStartPoint(new Point2D(col, row));
                    frameData.setSwordStartPointInverted(new Point2D(bufferedImage.getWidth() - col, row));

                } else if (currentRGB == blue) {
                    frameData.setSwordEndPoint(new Point2D(col, row));

                } else if (currentRGB == red) { // TODO :: Calculate sword tip by getting the red pixel with the highest / lowest x-coordinate
                    continue;

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
    private boolean isTransparent(int pixel) {
        return ((pixel >> 24) == 0x00);
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
