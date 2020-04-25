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

}
