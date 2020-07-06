package stickfight2d.world;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import stickfight2d.controllers.CameraController;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.PlayerType;
import stickfight2d.misc.Debugger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BackgroundObject extends GameObject {

    private BufferedImage defaultImage;
    private Image worldSubImage = null;

    private int worldState = 2;

    private final int subImageHeight = 810 * 2;
    private final int subImageWidth = 1032;
    private int subImageStartX = worldState * subImageWidth;

    // Key :: MapState <> Value :: Spawn location of player
    private final HashMap<Integer, HashMap<PlayerType, Point2D>> spawnPoints = initializeSpawnMap();

    public BackgroundObject(int x, int y, DirectionType directionType) { // x,y, dirType has to be 0, 0, null
        super(x, y, directionType);
        try {
            String PATH_MAP = "src/map.png";
            defaultImage = ImageIO.read(new File(PATH_MAP));
            worldSubImage = SwingFXUtils.toFXImage(defaultImage.getSubimage(subImageStartX, 0, subImageWidth, subImageHeight), null);
        } catch (IOException e) {
            e.printStackTrace();
            Debugger.log("BackgroundObject construction - Background image could not be initialized.\n");
        }
    }

    private static HashMap<Integer, HashMap<PlayerType, Point2D>> initializeSpawnMap() {
        HashMap<Integer, HashMap<PlayerType, Point2D>> map = new HashMap<>();
        int width = 1032;

        Point2D symmetricSpawns_P1 = new Point2D(150, 500);
        Point2D symmetricSpawns_P2 = new Point2D(width - 150, 500);

        // Spawns Map_0
        HashMap<PlayerType, Point2D> spawns_0 = new HashMap<>();
        spawns_0.put(PlayerType.PLAYER_ONE, new Point2D(580,500));
        spawns_0.put(PlayerType.PLAYER_TWO, new Point2D(width - 100,500));
        map.put(0, spawns_0);

        // Spawns Map_2
        HashMap<PlayerType, Point2D> spawns_1 = new HashMap<>();
        spawns_1.put(PlayerType.PLAYER_ONE, symmetricSpawns_P1);
        spawns_1.put(PlayerType.PLAYER_TWO, symmetricSpawns_P2);
        map.put(1, spawns_1);

        // Spawns Map_3
        HashMap<PlayerType, Point2D> spawns_2 = new HashMap<>();
        spawns_2.put(PlayerType.PLAYER_ONE, new Point2D(150, 500));
        spawns_2.put(PlayerType.PLAYER_TWO, new Point2D(850, 500));
        map.put(2, spawns_2);

        // Spawns Map_4
        HashMap<PlayerType, Point2D> spawns_3 = new HashMap<>();
        spawns_3.put(PlayerType.PLAYER_ONE, symmetricSpawns_P1);
        spawns_3.put(PlayerType.PLAYER_TWO, symmetricSpawns_P2);
        map.put(3, spawns_3);

        // Spawns Map_5
        HashMap<PlayerType, Point2D> spawns_4 = new HashMap<>();
        spawns_4.put(PlayerType.PLAYER_ONE, new Point2D(100,500));
        spawns_4.put(PlayerType.PLAYER_TWO, new Point2D(400,500));
        map.put(4, spawns_4);

        return map;
    }

    @Override
    public void update(long diffMillis) {
    }

    @Override
    public void draw(GraphicsContext gc) {
        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(this.x, this.y);

        gc.drawImage(worldSubImage, drawPoint.getX(), drawPoint.getY());
    }

    public void setWorldState(int worldState, PlayerObject p1, PlayerObject p2) {
        this.worldState = worldState;
        this.subImageStartX = worldState * subImageWidth;
        worldSubImage = SwingFXUtils.toFXImage(defaultImage.getSubimage(subImageStartX, 0, subImageWidth, subImageHeight), null);

        Point2D pointP1 = spawnPoints.get(this.worldState).get(p1.getPlayerNumber());
        Point2D pointP2 = spawnPoints.get(this.worldState).get(p2.getPlayerNumber());

        p1.setXY((int) pointP1.getX(), (int) pointP1.getY());
        p2.setXY((int) pointP2.getX(), (int) pointP2.getY());
    }

    public int getWorldState() {
        return worldState;
    }
}
