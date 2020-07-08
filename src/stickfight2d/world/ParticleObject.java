package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import stickfight2d.GameLoop;
import stickfight2d.controllers.CameraController;
import stickfight2d.enums.DirectionType;

public class ParticleObject extends GameObject {
    protected int lifetime;
    protected int lifetimeRemaining;
    protected boolean alive = true;
    protected boolean onGround = false;
    protected String color;
    protected int size;

    public ParticleObject(int x, int y, double vx, double vy, int lifetime,String color,int size) {
        super(x, y, DirectionType.RIGHT);
        this.vx = vx;
        this.vy = vy;
        this.lifetime = lifetime;
        this.lifetimeRemaining = lifetime;
        this.color = color;
        this.size = size;
    }

    @Override
    public void update(long diffMillis) {
        lifetimeRemaining -= diffMillis;
        if (lifetimeRemaining < 0) {
            alive = false;
        } else {
            if (onGround) {
                vy = vy * 0.999;
                vx = vx * 0.999;
            } else if (collisionRectRect(this, GameLoop.currentLevel.getGround())) {
                onGround = true;
                vy = vy * 0.999;
                vx = vx * 0.999;
            } else {
                vy -= (diffMillis / 10.0);    //gravity
            }

            y -= vy * diffMillis / 100.0;
            x += vx * diffMillis / 100.0;
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (alive) {
            Point2D drawPoint = CameraController.getInstance().convertWorldToScreen(x, y);
            gc.setFill(Color.web(color, (lifetimeRemaining / (float) lifetime)));
            gc.fillOval((int) drawPoint.getX(), (int) drawPoint.getY(), size, size);
        }
    }

    private boolean collisionRectRect(ParticleObject particle, RectangleObstacle obstacle) {

        return (particle.getX() <= obstacle.getX() + obstacle.getWidth()
                && particle.getX() >= obstacle.getX()
                && particle.getY() <= obstacle.getY() + obstacle.getHeight()
                && particle.getY() >= obstacle.getY());
    }
}
