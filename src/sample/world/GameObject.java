package sample.world;

import javafx.scene.canvas.GraphicsContext;
import sample.enums.DirectionType;

public abstract class GameObject {

    protected int x,y;
    protected double vx, vy;
    protected DirectionType directionType;

    public GameObject(int x, int y, DirectionType directionType){
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.directionType = directionType;
    }

    public abstract void update(long diffMillis);

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

    public DirectionType getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionType directionType) {
        this.directionType = directionType;
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
