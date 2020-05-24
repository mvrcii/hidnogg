package sample.controllers;

import javafx.geometry.Point2D;
import sample.GameLoop;
import sample.enums.CollisionType;
import sample.enums.Direction;
import sample.world.GameObject;
import sample.world.PlayerObject;
import sample.world.RectangleObstacle;

import java.util.ArrayList;

public class CollisionController {

    // ----------------------------------------------------------------------------------------------------
    // --- Instance & Constructor

    private static CollisionController instance;

    public static CollisionController getInstance() {
        if (instance == null) {
            instance = new CollisionController();
        }
        return instance;
    }

    // Player and Obstacle GameObjects
    private final ArrayList<PlayerObject> players = new ArrayList<>();
    private final ArrayList<RectangleObstacle> obstacles = new ArrayList<>();

    // Rectangle-HitBoxes for obstacle collisions
    private final Point2D[] rectHitBoxP1_P2 = new Point2D[4]; // Contains upper left X,Y and bottom right X,Y of both players
    private final int[] playersWidthHeight = new int[4]; // Contains Width and Height of both players

    private CollisionController() {
        for (GameObject obj : GameLoop.gameObjects) { // Collect PlayerObjects
            if (obj instanceof PlayerObject)
                players.add((PlayerObject) obj);

            if (obj instanceof RectangleObstacle) // Collect RectangleObjects
                obstacles.add((RectangleObstacle) obj);
        }

        fillPlayerRectangleHitBox();
    }

    // ----------------------------------------------------------------------------------------------------
    // --- Methods:

    /**
     * Returns all currently confirmed collision types as "HitType"s
     */
    public ArrayList<HitType> getAttackCollisionTypes() {
        ArrayList<HitType> detectedHitTypes = new ArrayList<>();

        // TODO: Return point of collision, null for now (? - must be x,y position of sword tip --- players.get(0).getSwordObject().getSwordSpike() )
        // Check Sword-Avatar collisions
        if (collisionSwordAvatar(players.get(0), players.get(1)))
            detectedHitTypes.add(new HitType(null, players.get(0), players.get(1), CollisionType.PLAYER1_HIT_PLAYER2));

        if (collisionSwordAvatar(players.get(1), players.get(0)))
            detectedHitTypes.add(new HitType(null, players.get(1), players.get(0), CollisionType.PLAYER2_HIT_PLAYER1));

        return detectedHitTypes;
    }


    /**
     * returns true if the swords hit each other (are on the same level) // TODO :: Should be called in the KeyControl since it depends on previous sword positions (?)
     */
    private boolean checkCollisionSwordSword() {
        Point2D swordTip_player1 = new Point2D(0,0); // TODO --- Need sword tip (x, y) position --> player1.getSwordObject().getSwordSpike()
        Point2D swordTip_player2 = new Point2D(0,0); // TODO --- Need sword tip (x, y) position --> player2.getSwordObject().getSwordSpike()

        // Offset of 2 pixels in each direction, assuming that the sword is 2-5 pixels wide
        return (swordTip_player1.getY() - 2 <= swordTip_player2.getY() && swordTip_player2.getY() <= swordTip_player1.getY() + 2);
    }


    /**
     * Returns all the Obstacle-Objects that collide with the player 1
     */
    public ArrayList<RectangleObstacle> getObstacleCollisionsPlayer1() {
        ArrayList<RectangleObstacle> retO_player1 = new ArrayList<>(); // Obstacles player1 collides with atm

        // Check Avatar-Obstacle collisions
        for (RectangleObstacle obstacle : obstacles) {
            if (collisionAvatarObstacle(players.get(0), obstacle, 1))
                retO_player1.add(obstacle);
        }

        return retO_player1;
    }


    /**
     * Returns all the Obstacle-Objects that collide with the player 2
     */
    public ArrayList<RectangleObstacle> getObstacleCollisionsPlayer2() {
        ArrayList<RectangleObstacle> ret0_player2 = new ArrayList<>(); // Obstacles player2 collides with atm

        // Check Avatar-Obstacle collisions
        for (RectangleObstacle obstacle : obstacles) {
            if (collisionAvatarObstacle(players.get(1), obstacle, 2))
                ret0_player2.add(obstacle);
        }

        return ret0_player2;
    }


    // ------------------------------------------------------------------------------------------------------------------------
    // --- Helper methods

    /**
     * Returns true, if there is a collision between the sword of player1 and the character of player2
     */
    private boolean collisionSwordAvatar(PlayerObject player1, PlayerObject player2) {
        Point2D[] points = new Point2D[2];

        ArrayList<Point2D> hitBoxPlayer2 = (player2.getDirection() == Direction.RIGHT) ?
                player2.getAnimation().getCurrentFrame().getHitBox() : player2.getAnimation().getCurrentFrame().getHitBoxInverted(); // TODO :: Make sure this is correct

        int idx = 0;
        for (Point2D p : hitBoxPlayer2) {
            if (player2.getY() + p.getY() == player1.getY()) { // X and Y must be ints
                points[idx++] = p;
                if (idx > 1)
                    break;
            }
        }

        if (idx == 0) { // No collision, sword isn't on the same y position as the player
            return false;
        }

        Point2D swordTip = new Point2D(0, 0); // TODO --- Need sword tip (x, y) position --> player1.getSwordObject().getSwordSpike()
        int sign = ((int) swordTip.getX() - (int) points[0].getX() - player2.getX()) * ((int) swordTip.getX() - (int) points[1].getX() - player2.getX());

        return sign <= 0; // Hit: negative or zero ; Miss: positive
    }


    /**
     * Returns true, if there is a collision between the player and the given obstacle
     */
    private boolean collisionAvatarObstacle(PlayerObject player, RectangleObstacle obstacle, int playerType) {

        int startIndex = (playerType == 1) ? 0 : 2;
        int playerWidth = playersWidthHeight[startIndex];
        int playerHeight = playersWidthHeight[startIndex + 1];

        return (player.getX() <= obstacle.getX() + obstacle.getWidth() // Checks, that rect2 is close enough from the left side
                && player.getX() + playerWidth >= obstacle.getX() // Checks, that rect2 is close enough from the right side
                && player.getY() <= obstacle.getY() + obstacle.getHeight() // Checks, --*-- from above
                && player.getY() + playerHeight >= obstacle.getY()); // Checks, --*-- from below
    }


    // ----------------------------------------------------------------------------------------------------
    // --- Inner Class HitTypes

    /**
     * Types of attack HitTypes
     */
    public static class HitType {
        public Point2D collisionPoint;
        public GameObject obj1;
        public GameObject obj2;
        public CollisionType typeEnum;

        HitType(Point2D collisionPoint, GameObject obj1, GameObject obj2, CollisionType typeEnum) {
            this.collisionPoint = collisionPoint;
            this.obj1 = obj1;
            this.obj2 = obj2;
            this.typeEnum = typeEnum;
        }
    }


    // ----------------------------------------------------------------------------------------------------
    // --- Inner Class HitTypes

    private void fillPlayerRectangleHitBox() {
        int x_min = Integer.MAX_VALUE, x_max = Integer.MIN_VALUE, y_min = Integer.MAX_VALUE, y_max = Integer.MIN_VALUE;
        ArrayList<Point2D> hitBoxPoints;

        for (int playerIndex = 0; playerIndex < 2; playerIndex++) {
            hitBoxPoints = players.get(playerIndex).getAnimation().getCurrentFrame().getHitBox();

            for (Point2D currentPoint : hitBoxPoints) {

                if (currentPoint.getX() < x_min)
                    x_min = (int) currentPoint.getX();
                else if (currentPoint.getX() > x_max)
                    x_max = (int) currentPoint.getX();

                if (currentPoint.getY() < y_min)
                    y_min = (int) currentPoint.getY();
                else if (currentPoint.getY() > y_max)
                    y_max = (int) currentPoint.getY();
            }

            if (playerIndex == 0) {
                rectHitBoxP1_P2[0] = new Point2D(x_min, y_min);
                rectHitBoxP1_P2[1] = new Point2D(x_max, y_max);
            } else {
                rectHitBoxP1_P2[2] = new Point2D(x_min, y_min);
                rectHitBoxP1_P2[3] = new Point2D(x_max, y_max);
            }
        }

        // Player 1
        playersWidthHeight[0] = (int) rectHitBoxP1_P2[1].getX() - (int) rectHitBoxP1_P2[0].getY();
        playersWidthHeight[1] = (int) rectHitBoxP1_P2[1].getY() - (int) rectHitBoxP1_P2[0].getY();

        // Player 2
        playersWidthHeight[2] = (int) rectHitBoxP1_P2[3].getX() - (int) rectHitBoxP1_P2[2].getY();
        playersWidthHeight[3] = (int) rectHitBoxP1_P2[3].getY() - (int) rectHitBoxP1_P2[2].getY();
    }
}