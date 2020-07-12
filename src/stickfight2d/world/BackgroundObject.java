package stickfight2d.world;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import stickfight2d.GameLoop;
import stickfight2d.controllers.CameraController;
import stickfight2d.controllers.CollisionController;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.PlayerType;
import stickfight2d.misc.Debugger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BackgroundObject extends GameObject {

    /**
     * map.png
     */
    private BufferedImage defaultImage;

    /**
     * Current world background
     */
    private Image worldSubImage = null;

    /**
     * Map split into 5 world states [0, 4], "2" being the middle
     */
    private boolean worldStateChanged = true;
    private int worldState = 2;

    /**
     * Key :: MapState
     * Value :: Spawn location of player
     */
    private final HashMap<Integer, HashMap<PlayerType, Point2D>> spawnPoints = initializeSpawnMap();

    /**
     * For calculating the current sub image that has to be displayed in the background
     */
    private final int subImageHeight = 810 * 2;
    private final int subImageWidth = 1032;
    private int subImageStartX = worldState * subImageWidth;

    /**
     * For enabling players to run out of opposite bounds if they get a hit on the enemy
     */
    private DirectionType currentEnabledRunningDirection = null;
    private Image arrow_left;
    private Image arrow_right;

    /**
     * Read map image and save first sub image
     *
     * @param x             has to be 0
     * @param y             has to be 0
     * @param directionType has to be null
     */
    public BackgroundObject(int x, int y, DirectionType directionType) {
        super(x, y, directionType);
        try {
            String PATH_MAP = "src/map.png";
            defaultImage = ImageIO.read(new File(PATH_MAP));
            worldSubImage = SwingFXUtils.toFXImage(defaultImage.getSubimage(subImageStartX, 0, subImageWidth, subImageHeight), null);

            String PATH_ARROW_LEFT = "src/arrow_left.png";
            String PATH_ARROW_RIGHT = "src/arrow_right.png";
            arrow_left = SwingFXUtils.toFXImage(ImageIO.read(new File(PATH_ARROW_LEFT)), null);
            arrow_right = SwingFXUtils.toFXImage(ImageIO.read(new File(PATH_ARROW_RIGHT)), null);

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
        spawns_0.put(PlayerType.PLAYER_ONE, new Point2D(580, 500));
        spawns_0.put(PlayerType.PLAYER_TWO, new Point2D(width - 100, 500));
        map.put(0, spawns_0);

        // Spawns Map_1
        HashMap<PlayerType, Point2D> spawns_1 = new HashMap<>();
        spawns_1.put(PlayerType.PLAYER_ONE, symmetricSpawns_P1);
        spawns_1.put(PlayerType.PLAYER_TWO, symmetricSpawns_P2);
        map.put(1, spawns_1);

        // Spawns Map_2
        HashMap<PlayerType, Point2D> spawns_2 = new HashMap<>();
        spawns_2.put(PlayerType.PLAYER_ONE, new Point2D(275, 500));
        spawns_2.put(PlayerType.PLAYER_TWO, new Point2D(width - 275, 500));
        map.put(2, spawns_2);

        // Spawns Map_3
        HashMap<PlayerType, Point2D> spawns_3 = new HashMap<>();
        spawns_3.put(PlayerType.PLAYER_ONE, symmetricSpawns_P1);
        spawns_3.put(PlayerType.PLAYER_TWO, symmetricSpawns_P2);
        map.put(3, spawns_3);

        // Spawns Map_4
        HashMap<PlayerType, Point2D> spawns_4 = new HashMap<>();
        spawns_4.put(PlayerType.PLAYER_ONE, new Point2D(100, 500));
        spawns_4.put(PlayerType.PLAYER_TWO, new Point2D(400, 500));
        map.put(4, spawns_4);

        return map;
    }

    @Override
    public void update(long diffMillis) {
        if (worldStateChanged) {
            currentEnabledRunningDirection = null;

        } else {
            CollisionController collisionController = CollisionController.getInstance();

            if (collisionController.getPlayerHitOtherPlayer(PlayerType.PLAYER_ONE))
                currentEnabledRunningDirection = DirectionType.RIGHT;

            else if (collisionController.getPlayerHitOtherPlayer(PlayerType.PLAYER_TWO))
                currentEnabledRunningDirection = DirectionType.LEFT;

        }

        worldStateChanged = false;
    }

    @Override
    public void draw(GraphicsContext gc) {
        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(0, 0);
        gc.drawImage(worldSubImage, drawPoint.getX(), drawPoint.getY());

        if (currentEnabledRunningDirection == DirectionType.RIGHT && worldState != 0)
            gc.drawImage(arrow_right, 676, 156);

        else if (currentEnabledRunningDirection == DirectionType.LEFT && worldState != 4)
            gc.drawImage(arrow_left, 356, 156);
    }

    /**
     * Updates player spawns when a new map is being loaded onto the screen
     *
     * @param worldState new worldState, when new map has to be loaded
     * @param p1         player1
     * @param p2         player2
     */
    public void setWorldState(int worldState, PlayerObject p1, PlayerObject p2) {
        this.worldState = worldState;
        this.subImageStartX = worldState * subImageWidth;
        worldSubImage = SwingFXUtils.toFXImage(defaultImage.getSubimage(subImageStartX, 0, subImageWidth, subImageHeight), null);
        worldStateChanged = true;

        GameLoop.currentLevel.clearSwordsOnGround();

        p1.checkSwordInNewScreen();
        p2.checkSwordInNewScreen();

        Point2D pointP1 = spawnPoints.get(this.worldState).get(p1.getPlayerNumber());
        Point2D pointP2 = spawnPoints.get(this.worldState).get(p2.getPlayerNumber());

        if(!p1.isAlive())
            p1.setDeadAndMapChanged(true);
        if(!p2.isAlive())
            p2.setDeadAndMapChanged(true);

        p1.setXY((int) pointP1.getX(), (int) pointP1.getY());
        p2.setXY((int) pointP2.getX(), (int) pointP2.getY());
    }

    public int getWorldState() {
        return worldState;
    }

    /**
     * @param type PlayerType
     * @return Location where the given player should be spawning relative to the current map state
     */
    public Point2D getCurrentSpawnPoint(PlayerType type) {
        return spawnPoints.get(worldState).get(type);
    }

    public DirectionType getCurrentEnabledRunningDirection() {
        return currentEnabledRunningDirection;
    }

    public boolean isWorldStateChanged(){
        return worldStateChanged;
    }
}