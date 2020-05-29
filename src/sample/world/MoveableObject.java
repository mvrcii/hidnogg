package sample.world;

import sample.enums.Direction;
import sample.world.GameObject;

public abstract class MoveableObject extends GameObject {

    protected int speed;

    public MoveableObject(int x, int y, Direction direction) {
        super(x, y, direction);
        this.speed = 3;
    }
}
