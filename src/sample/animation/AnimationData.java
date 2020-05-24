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
            for (int i = 0; (i * TILE_SIZE) < spriteSheet.getWidth(); ) {
                BufferedImage bf = spriteSheet.getSubimage(i * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (!imageTransparent) {
                    FrameData frame = calcFrameData(bf);

                    if (frame.getSwordStartPoint() == null || frame.getSwordEndPoint() == null) {

                        //System.out.println("No sword start/end point found, row: "+row);
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

            // Left hitBox pixels ++ green and blue pixels
            for (int col = 0; col < bufferedImage.getWidth(); col++) {
                int currentRGB = bufferedImage.getRGB(col, row);
                int lastBlackPixel_x = 0;

                if (isTransparent(currentRGB))
                    continue;

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

                if (col == bufferedImage.getWidth() - 1) {
                    hitBox.add(new Point2D(lastBlackPixel_x, row));
                    hitBoxInverted.add(new Point2D(bufferedImage.getWidth() - lastBlackPixel_x, row));
                }
            }
        }

        frameData.setHitBox(hitBox);
        frameData.setHitBoxInverted(hitBoxInverted);
        return frameData;

        /* Previous definition

        FrameData frameData = new FrameData(bf);
        ArrayList<Point2D> hitBox = new ArrayList<>();
        ArrayList<Point2D> hitBoxInverted = new ArrayList<>();
        imageTransparent = true;

        for (int y = 0; y < bf.getWidth(); y++) {
            for (int x = 0; x < bf.getHeight(); x++) {
                int pixel = bf.getRGB(x, y);
                if (!isTransparent(pixel)) {
                    imageTransparent = false;

                    if (pixel == red) {
                        if (checkDirectNeighbours(bf, x, y)) {
                            hitBox.add(new Point2D(x, y));
                            hitBoxInverted.add(new Point2D(bf.getWidth() - x, bf.getHeight() - y));
                        }
                    } else if (pixel == green){
                        frameData.setSwordStartPoint(new Point2D(x, y));
                        frameData.setSwordStartPointInverted(new Point2D(bf.getWidth() - x, y));
                    } else if (pixel == blue) {
                        frameData.setSwordEndPoint(new Point2D(x, y));
                    }
                }
            }
        }
        frameData.setHitBox(hitBox);
        frameData.setHitBoxInverted(hitBoxInverted);
        return frameData; */
    }


    /**
     * Help Methods
     */
    private boolean checkDirectNeighbours(BufferedImage image, int x, int y) {
        if (x > 0) {
            return isTransparent(image.getRGB(x - 1, y));
        }
        if (y > 0) {
            return isTransparent(image.getRGB(x, y - 1));
        }
        if (x <= image.getWidth()) {
            return isTransparent(image.getRGB(x + 1, y));
        }
        if (y <= image.getHeight()) {
            return isTransparent(image.getRGB(x, y + 1));
        }
        return false;
    }

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
