package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import stickfight2d.GameLoop;
import stickfight2d.controllers.CameraController;
import stickfight2d.misc.Config;

public class RectangleObstacle extends GameObject {

    // ----------------------------------------------------------------------------------------------------
    // --- Attributes / Constructor

    protected int width;
    protected int height;
    protected Color color; // Default color
    protected int mapState;

    /*
     * vx, vy 0 on default (immovable object)
     */
    public RectangleObstacle(int x, int y, int width, int height, Color color, int mapState) {
        super(x, y, null); // Need direction?

        this.width = width;
        this.height = height;
        this.color = color;
        this.mapState = mapState;
    }

    // ----------------------------------------------------------------------------------------------------
    // --- Methods

    @Override
    public void update(long diffMillis) {

    }

    @Override
    public void draw(GraphicsContext gc) {
        if (!Config.debug_mode
                || this.mapState != GameLoop.currentLevel.getBackground().getWorldState() && this.mapState >= 0
                || this.mapState < 0 && !(GameLoop.currentLevel.getBackground().getWorldState() == 0 || GameLoop.currentLevel.getBackground().getWorldState() == 4))
            return;

        Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
        gc.setFill(this.color);
        gc.fillRect(drawPoint.getX(), drawPoint.getY(), this.width, this.height);
    }

    // ----------------------------------------------------------------------------------------------------
    // --- Getter

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getMapState() {
        return mapState;
    }

    public Color getColor() {
        return this.color;
    }
}