package sample.animation;


import javafx.geometry.Point2D;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AnimationData {

    private boolean imageTransparent;
    private final ArrayList<FrameData> frames = new ArrayList<>();

    private static final int TILE_SIZE = 64;
    private static final String SPRITE_SHEET_PATH = "src/spritesheet.png";

    // Constructor to load many different Sprites from the Sprite Sheet into one animation
    public AnimationData(Point... point) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
            for (Point p : point) {
                BufferedImage bf = spriteSheet.getSubimage(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                frames.add(calcFrameData(bf));
                System.out.println("Frame finished");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("AnimationData: Could not read the Sprite sheet.");
        }
    }

    // Constructor to load a complete Sprite Sheet row into one animation
    public AnimationData(int row){
        try{
            BufferedImage spriteSheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
            int imageCount = 0;
            while((imageCount * TILE_SIZE) < spriteSheet.getWidth()){
                BufferedImage bf = spriteSheet.getSubimage(imageCount * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if(!imageTransparent){
                    frames.add(calcFrameData(bf));
                    System.out.println("Frame finished");
                    imageCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("AnimationData: Could not read the Sprite sheet.");
        }
    }


    private FrameData calcFrameData(BufferedImage bf){
        FrameData frameData = new FrameData(bf);                    // Initialize the frameData with the bufferedImage
        imageTransparent = true;
        ArrayList<Point2D> hitBox = new ArrayList<>();              // ArrayList which gets filled with hitBox pixels
        ArrayList<Point2D> hitBoxInverted = new ArrayList<>();

        System.out.println("Start Frame Reading");

        int black = new Color(0,0,0).getRGB();
        int white = new Color(255,255,255).getRGB();
        int green = new Color(0,255,0).getRGB();
        int blue = new Color(0,0,255).getRGB();

        for (int y = 0; y < bf.getWidth(); y++) {
            for (int x = 0; x < bf.getHeight(); x++) {
                int pixel = bf.getRGB(x, y);                     // int Color value for a specific pixel

                if(!isTransparent(pixel))                       // Check Transparency First
                {
                    imageTransparent = false;

                    if(pixel == black)                           // Check Black Color
                    {
                        if(checkDirectNeighbours(bf, x, y))         // Check Outline Pixel
                        {
                            hitBox.add(new Point2D(x,y));           // Add to HitBox
                            hitBoxInverted.add(new Point2D(bf.getWidth()-x, bf.getHeight()-y));
                        }
                    }
                    else if(pixel == green)     // Check Green Color
                    {
                        System.out.println("Added Green Pixel ("+x+","+y+")");
                        frameData.setSwordStartPoint(new Point2D(x,y));
                    }
                    else if(pixel == blue){      // Check Blue Color
                        System.out.println("Added Blue Pixel ("+x+","+y+")");
                        frameData.setSwordEndPoint(new Point2D(x,y));
                    }
                }
            }
        }
        frameData.setHitBox(hitBox);
        frameData.setHitBoxInverted(hitBoxInverted);
        return frameData;
    }

    /* NOT UPDATED
    private ArrayList<Point2D> calcHitBoxPixels(FrameData frameData) {
        ArrayList<Point2D> hitBox = new ArrayList<>();
        for (int y = 0; y < frameData.getBufferedImage().getWidth(); y++) {
            for (int x = 0; x < frameData.getBufferedImage().getHeight(); x++) {
                int pixel = frameData.getBufferedImage().getRGB(x, y);
                boolean transparent = ((pixel >> 24) == 0x00);
                if (!transparent) {
                    hitBox.add(new Point2D(x, y));
                }
            }
        }
        return hitBox;
    }
    */


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
        if(isTransparent(image.getRGB(x+1, y))){
            return true;
        }
        if(isTransparent(image.getRGB(x, y+1))){
            return true;
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

    public static int getTileSize() {
        return TILE_SIZE;
    }
}
