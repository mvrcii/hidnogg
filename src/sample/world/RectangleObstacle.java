package sample.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sample.animation.FrameData;
import sample.enums.Direction;

public class RectangleObstacle extends GameObject {

    // ----------------------------------------------------------------------------------------------------
    // --- Attributes / Constructor

    protected int width;
    protected int height;
    protected Color color; // Default color
    protected boolean isGround;

    /*
     * vx, vy 0 on default (immovable object)
     */
    public RectangleObstacle(int x, int y, int width, int height, Color color) {
        super(x, y, null); // Need direction?

        this.width = width;
        this.height = height;
        this.color = color;
    }

    // ----------------------------------------------------------------------------------------------------
    // --- Methods

    @Override
    public void update(long diffMillis) {

    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(this.color);
        gc.fillRect(this.x, this.y + 1, this.width, this.height);
    }

    // ----------------------------------------------------------------------------------------------------
    // --- Getter

    public int getWidth(){
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Color getColor() {
        return this.color;
    }
}
