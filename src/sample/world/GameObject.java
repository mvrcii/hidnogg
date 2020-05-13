package sample.world;

import javafx.scene.canvas.GraphicsContext;
import sample.enums.Direction;

public abstract class GameObject {

    protected int x,y;
    protected double vx, vy;
    protected Direction direction;

    public GameObject(int x, int y, Direction direction){
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.direction = direction;
    }

    public abstract void update(long diffSeconds);

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

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }
}
