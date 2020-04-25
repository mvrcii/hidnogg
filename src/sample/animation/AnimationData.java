package sample.animation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import sample.controllers.HitBoxController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AnimationData {

    private ArrayList<FrameData> frames = new ArrayList<>();

    private static final int TILE_SIZE = 64;
    private static final String SPRITE_SHEET_PATH = "src/spriteSheet.png";

    // Constructor to load many different Sprites from the Sprite Sheet into one animation
    public AnimationData(Point... point) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
            for (Point p : point) {
                BufferedImage sprite = spriteSheet.getSubimage(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                frames.add(new FrameData(convertToFxImage(sprite), HitBoxController.getInstance().getHitBox(sprite)));
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
                BufferedImage sprite = spriteSheet.getSubimage(imageCount * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // (1) calculate hitBox Pixels in HitBoxController
                // (2) If hitBox != null -> continue
                // (3) Else -> break -> All Sprites for Animation found
                //    -->  ArrayList<Point> hitBox = HitBoxController.getInstance().getHitBoxPixels(sprite);
                //    -->  if(hitBox != null){
                //    -->     frames.add(new FrameData(convertToFxImage(sprite), hitBox));
                //    -->  }else{
                //    -->     break;
                //    -->  }
                ArrayList<Point> hitBox = HitBoxController.getInstance().getHitBox(sprite);

                if(hitBox != null){
                    frames.add(new FrameData(convertToFxImage(sprite), hitBox));
                    imageCount++;
                }else{
                    break;
                }
            }
            System.out.println("Animation: ("+imageCount+"/"+spriteSheet.getWidth()/TILE_SIZE+") Sprites loaded in row "+row+".");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("AnimationData: Could not read the Sprite sheet.");
        }
    }





    private boolean checkDirectNeighbours(BufferedImage image, int x, int y){
        if(checkPixelTransparency(image.getRGB(x-1, y))){
            return true;
        }else if(checkPixelTransparency(image.getRGB(x, y-1))){
            return true;
        }else if(checkPixelTransparency(image.getRGB(x+1, y))){
            return true;
        }else if(checkPixelTransparency(image.getRGB(x, y+1))){
            return true;
        }
        return false;
    }

    private boolean checkPixelTransparency(int pixel){
        return ((pixel>>24) == 0x00);
    }

    public ArrayList<Image> getImages(){
        return frames.stream()
                .map(FrameData::getImage)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<FrameData> getFrames() {
        return frames;
    }

    private static Image convertToFxImage(BufferedImage image) {
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

}
