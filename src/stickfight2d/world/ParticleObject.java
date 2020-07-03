package stickfight2d.world;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import stickfight2d.controllers.CameraController;
import stickfight2d.enums.DirectionType;

public class ParticleObject extends GameObject {
    protected int lifetime;
    protected int lifetimeRemaining;
    protected boolean alive = true;
    protected boolean onGround = false;

    public ParticleObject(int x, int y, double vx, double vy, int lifetime) {
        super(x, y, DirectionType.RIGHT);
        this.vx = vx;
        this.vy = vy;
        this.lifetime = lifetime;
        this.lifetimeRemaining = lifetime;
    }

    @Override
    public void update(long diffMillis) {
        lifetimeRemaining -= diffMillis;
        if (lifetimeRemaining < 0) {
            alive = false;
        } else {
            if (onGround) {

            }
                /*
                else if(collisionRectRect(this, GameLoop.currentLevel.getGround()))
                {
                    onGround = true;
                    vy = vy * 0.2;
                    vx = vx * 0.1;
                } */
            else {
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
            gc.setFill(Color.web("0xFF0000", (lifetimeRemaining / (float) lifetime)));
            gc.fillOval((int) drawPoint.getX(), (int) drawPoint.getY(), 4, 4);
        }
    }

    private boolean collisionRectRect(ParticleObject particle, RectangleObstacle obstacle) {
        return (particle.getX() <= obstacle.getX() + obstacle.getWidth()
                && particle.getX() >= obstacle.getX()
                && particle.getY() <= obstacle.getY() + obstacle.getHeight()
                && particle.getY() >= obstacle.getY());
    }

}
