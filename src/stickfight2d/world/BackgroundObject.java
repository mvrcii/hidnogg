package stickfight2d.world;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import stickfight2d.Main;
import stickfight2d.controllers.CameraController;
import stickfight2d.enums.DirectionType;
import stickfight2d.misc.Debugger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BackgroundObject extends GameObject {

    private BufferedImage defaultImage;
    private Image worldSubImage = null;
    private final String PATH_MAP = "src/map.png";

    private int worldState = 2;

    private int subImageHeight = 810;
    private int subImageWidth = 1032;
    private int subImageStartX = worldState * subImageWidth;

    public BackgroundObject(int x, int y, DirectionType directionType) { // x,y, dirType has to be 0, 0, null
        super(x, y, directionType);
        try {
            defaultImage = ImageIO.read(new File("src/map.png"));
            worldSubImage = SwingFXUtils.toFXImage(defaultImage.getSubimage(subImageStartX, 0, subImageWidth, subImageHeight), null);
        } catch (IOException e) {
            e.printStackTrace();
            Debugger.log("BackgroundObject construction - Background image could not be initialized.\n");
        }
    }

    @Override
    public void update(long diffMillis) {
    }

    @Override
    public void draw(GraphicsContext gc) {
        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(this.x, this.y);

        gc.drawImage(worldSubImage, drawPoint.getX(), drawPoint.getY());
    }

    public void setWorldState(int worldState) {
        this.worldState = worldState;
        this.subImageStartX = worldState * subImageWidth;
        worldSubImage = SwingFXUtils.toFXImage(defaultImage.getSubimage(subImageStartX, 0, subImageWidth, subImageHeight), null);
    }

    public int getWorldState() {
        return worldState;
    }
}
