package sample.world;

import javafx.scene.canvas.GraphicsContext;
import sample.animation.Animation;
import sample.controllers.AnimationController;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.interfaces.InputSystem;

import static sample.enums.AnimationType.PLAYER_IDLE_LOW;

public class PlayerObject extends MoveableObject implements  InputSystem{

    private PlayerType playerNumber;
    private float speed;

    // Current Player Animation
    private Animation animation = AnimationController.getInstance().getAnimation(PLAYER_IDLE_LOW);;

    public PlayerObject(int x, int y, PlayerType playerNumber, Direction direction){
        super(x,y,direction);
        this.playerNumber = playerNumber;
        animation.start();
    }


    @Override
    public void processInput() {
    }

    @Override
    public void update(double diffSeconds) {
    }

    @Override
    public void update() {
        animation.update();
    }

    @Override
    public void draw(GraphicsContext gc) {
      gc.drawImage(animation.getSprite(), x, y);
    }
}
