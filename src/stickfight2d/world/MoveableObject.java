package stickfight2d.world;

import stickfight2d.enums.DirectionType;

public abstract class MoveableObject extends GameObject {

    protected int speed;

    public MoveableObject(int x, int y, DirectionType directionType) {
        super(x, y, directionType);
        this.speed = 3;
    }
}
