package sample.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import sample.animation.Animation;
import sample.controllers.AnimationController;
import sample.controllers.KeyController;
import sample.enums.Direction;
import sample.enums.PlayerType;
import sample.interfaces.InputSystem;

import static sample.enums.AnimationType.*;
import static sample.enums.AnimationType.PLAYER_IDLE_MEDIUM;

public class PlayerObject extends MoveableObject implements  InputSystem{

    private PlayerType playerNumber;

    // Current Player Animation
    private Animation animation = AnimationController.getInstance().getAnimation(PLAYER_IDLE_LOW);;

    public PlayerObject(int x, int y, PlayerType playerNumber, Direction direction){
        super(x,y,direction);
        this.playerNumber = playerNumber;
        animation.start();
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

    @Override
    public void processInput() {
        KeyController keyController = KeyController.getInstance();
        AnimationController animationController = AnimationController.getInstance();

        if (playerNumber == PlayerType.PLAYER_ONE)
        {
            if (keyController.isKeyPressed(KeyCode.D))
            {
                System.out.println("Pressed D");
                direction = Direction.RIGHT;
                x += speed;
            }
            if (keyController.isKeyPressed(KeyCode.A))
            {
                System.out.println("Pressed A");
                direction = Direction.LEFT;
                x -= speed;
            }
            if (keyController.isKeyPressed(KeyCode.S))
            {
                System.out.println("Pressed S");
                if (animation.getType() == PLAYER_IDLE_MEDIUM)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_LOW);
                    animation.start();
                }
                else if (animation.getType() == PLAYER_IDLE_HIGH)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                    animation.start();
                }
            }

            if (keyController.isKeyPressed(KeyCode.W))
            {
                System.out.println("Pressed W");
                if (animation.getType() == PLAYER_IDLE_LOW)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                    animation.start();
                } else if (animation.getType() == PLAYER_IDLE_MEDIUM)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_HIGH);
                    animation.start();
                }

            }
        } else if (playerNumber == PlayerType.PLAYER_TWO)
        {
            if (keyController.isKeyPressed(KeyCode.RIGHT))
            {
                direction = Direction.RIGHT;
                x += speed;
            }
            if (keyController.isKeyPressed(KeyCode.LEFT))
            {
                direction = Direction.LEFT;
                x -= speed;
            }
            if (keyController.isKeyPressed(KeyCode.UP))
            {
                System.out.println("Pressed UP");
                if (animation.getType() == PLAYER_IDLE_LOW)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                    animation.start();
                } else if (animation.getType() == PLAYER_IDLE_MEDIUM)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_HIGH);
                    animation.start();
                }
            }
            if (keyController.isKeyPressed(KeyCode.DOWN))
            {
                System.out.println("Pressed DOWN");
                if (animation.getType() == PLAYER_IDLE_MEDIUM)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_LOW);
                    animation.start();
                }
                else if (animation.getType() == PLAYER_IDLE_HIGH)
                {
                    animation = animationController.getAnimation(PLAYER_IDLE_MEDIUM);
                    animation.start();
                }
            }

        }
    }
}
