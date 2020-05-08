package sample.world;

import javafx.scene.canvas.GraphicsContext;
import sample.enums.Direction;

public abstract class GameObject {

    protected int x,y;
    protected Direction direction;

    public GameObject(int x, int y, Direction direction){
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public abstract void update();

    public abstract void draw(GraphicsContext gc);

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
