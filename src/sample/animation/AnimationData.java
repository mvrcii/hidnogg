package sample.animation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AnimationData {

    private ArrayList<FrameData> frames = new ArrayList<>();

    private static final int TILE_SIZE = 64;
    private static final String SPRITE_SHEET_PATH = "C:\\Users\\Marcel\\Documents\\GitHub\\GPRO-Projekt\\src\\spritesheet.png";



    public AnimationData(Point... point) {
        loadDifferentSprites(point);
    }

    public AnimationData(int row){
        loadSpriteRow(row);
    }

    // TODO Noch zu erledigen!!
    private void loadSpriteRow(int row){
        try{
            BufferedImage spriteSheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
            int i = 0;
            while(i < spriteSheet.getWidth()){
                BufferedImage bufferedImage = spriteSheet.getSubimage(i * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                frames.add(new FrameData(convertBufferedImageToImage(bufferedImage), calcHitBoxPixels(bufferedImage)));
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not read spritesheet.");
        }
    }


    private void loadDifferentSprites(Point... point){
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(SPRITE_SHEET_PATH));
            for (Point p : point) {
                BufferedImage sprite = spriteSheet.getSubimage(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                frames.add(new FrameData(convertBufferedImageToImage(sprite), calcOutlineHitBoxPixels(sprite)));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println("Could not read spritesheet.");
        }
    }

    // Converts a BufferedImage to an Image and adds it to the image ArrayList
    private Image convertBufferedImageToImage(BufferedImage image) {
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

    // TODO ANDI - HitBoxController erzeugen und diese beiden Methoden auslagern
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
        //System.out.println("HitBox Pixel amount: "+hitBox.size());
        return hitBox;
    }

    private ArrayList<Point> calcOutlineHitBoxPixels(BufferedImage bufferedImage){
        ArrayList<Point> outlineHitBoxPixels = new ArrayList<>();
            for (int y = 1; y < bufferedImage.getWidth(); y++) {
                for (int x = 1; x < bufferedImage.getHeight(); x++) {
                    int pixel = bufferedImage.getRGB(x, y);
                    if(!transparencyCheck(pixel) && checkDirectNeighbours(bufferedImage, x, y)) {
                        // TODO Hier könnte Performance eingespart werden
                        outlineHitBoxPixels.add(new Point(x,y));
                    }
                }
            }
        return outlineHitBoxPixels;
    }


    private boolean checkDirectNeighbours(BufferedImage image, int x, int y){
        if(transparencyCheck(image.getRGB(x-1, y))){
            return true;
        }else if(transparencyCheck(image.getRGB(x, y-1))){
            return true;
        }else if(transparencyCheck(image.getRGB(x+1, y))){
            return true;
        }else if(transparencyCheck(image.getRGB(x, y+1))){
            return true;
        }
        return false;
    }

    private boolean transparencyCheck(int pixel){
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
}
