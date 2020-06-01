package sample.controllers;

import javafx.geometry.Point2D;
import sample.GameLoop;
import sample.enums.AnimationType;
import sample.enums.DirectionType;
import sample.enums.PlayerType;
import sample.world.GameObject;
import sample.world.PlayerObject;
import sample.world.RectangleObstacle;

import java.util.ArrayList;

public class CollisionController extends Controller {

    // ----------------------------------------------------------------------------------------------------
    // --- Instance & Constructor

    private static CollisionController instance;

    public static CollisionController getInstance() {
        if (instance == null) {
            System.out.println("Collision Controller instantiated");
            instance = new CollisionController();
        }
        return instance;
    }

    // Player Data
    private final ArrayList<PlayerObject> players = new ArrayList<>();
    private static int swordLength = 0; // swordLength for sword-tip-calculation
    private final Point2D[] rectHitBoxP1_P2 = new Point2D[2]; // Contains upper left X,Y and bottom right X,Y of both players
    private final int[] playersWidthHeight = new int[2]; // Contains Width and Height of both players

    // World Data
    private final ArrayList<RectangleObstacle> obstacles = new ArrayList<>();

    // Player states
    private boolean player1_onGround = false;
    private boolean player2_onGround = false;
    private boolean player1_hit_player2 = false;
    private boolean player2_hit_player1 = false;
    private boolean swordsHitting = false;

    private CollisionController() {
        for (GameObject obj : GameLoop.currentLevel.getGameObjects()) { // Collect PlayerObjects
            if (obj instanceof PlayerObject)
                players.add((PlayerObject) obj);

            if (obj instanceof RectangleObstacle) { // Collect RectangleObjects
                obstacles.add((RectangleObstacle) obj);
            }
        }

        fillPlayerRectangleHitBox();
    }

    /**
     * Updates information on player states
     */
    @Override
    public void update(long diffMillis) {
        player1_onGround = getPlayerObstacleCollisions(players.get(0)).size() > 0;
        player2_onGround = getPlayerObstacleCollisions(players.get(1)).size() > 0;

        player1_hit_player2 = collisionSwordAvatar(players.get(0), players.get(1));
        player2_hit_player1 = collisionSwordAvatar(players.get(1), players.get(0));

        swordsHitting = checkCollisionSwordSword();
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // --- Attack collisions

    /**
     * Returns true, if there is a collision between the sword of player1 and the character of player2
     */
    private boolean collisionSwordAvatar(PlayerObject player1, PlayerObject player2) { // TODO :: Eventually update hitBoxCalc & swordTip
        if(player1.getAnimation().getAnimationType() == AnimationType.PLAYER_WALK || player1.getAnimation().getAnimationType() == AnimationType.PLAYER_IDLE_HOLD_UP) // Prevent stabbing while sword is held up
            return false;

        Point2D swordTip;
        ArrayList<Point2D> hitBox_Player2;
        int offsetHitBox = 0; // TODO :: has to be updated, if playerRotation-implementation is changed

        // Get relevant hitBox of player2 and swordTip-position of player1
        if (player2.getDirectionType() == DirectionType.RIGHT) { // --> player1 direction must be Direction.LEFT
            hitBox_Player2 = player2.getAnimation().getCurrentFrame().getHitBox();

            Point2D swordStartPoint = player1.getAnimation().getCurrentFrame().getSwordStartPointInverted();
            swordTip = new Point2D(player1.getX() + swordStartPoint.getX() - playersWidthHeight[0] - swordLength, player1.getY() + swordStartPoint.getY());

        } else { // --> player1 direction must be Direction.RIGHT
            hitBox_Player2 = player2.getAnimation().getCurrentFrame().getHitBoxInverted();
            offsetHitBox = playersWidthHeight[0] + 2;

            Point2D swordStartPoint = player1.getAnimation().getCurrentFrame().getSwordStartPoint();
            swordTip = new Point2D(player1.getX() + swordStartPoint.getX() + swordLength, player1.getY() + swordStartPoint.getY());
        }

        // Get points of interest for hitBox detection
        Point2D[] points = new Point2D[2];
        int idx = 0;
        for (Point2D p : hitBox_Player2) {
            if (swordTip.getY() == player2.getY() + p.getY()) { // X and Y must be ints
                points[idx++] = p;
                if (idx > 1)
                    break;
            }
        }

        if (idx == 0) { // No collision possible, sword isn't on the same y position as the player
            return false;
        }

        int firstSign = ((int) swordTip.getX() - ((int) points[0].getX() + player2.getX() - offsetHitBox));
        int secondSign = ((int) swordTip.getX() - ((int) points[1].getX() + player2.getX() - offsetHitBox));

        return firstSign * secondSign <= 0; // Hit: negative or zero ; Miss: positive
    }


    /**
     * returns true if the swords hit each other (are on the same level) // TODO :: Should be called in the KeyControl since it depends on previous sword positions (?)
     */
    private boolean checkCollisionSwordSword() {
        Point2D swordTip1, swordGrip2, swordTip2;
        int offsetSword = 4; // TODO :: has to be updated, if playerRotation-implementation is changed

        if (players.get(0).getDirectionType() == DirectionType.RIGHT) { // --> p2 LEFT
            Point2D swordStartPoint = players.get(0).getAnimation().getCurrentFrame().getSwordStartPoint();
            swordTip1 = new Point2D(players.get(0).getX() + swordStartPoint.getX() + swordLength, players.get(0).getY() + swordStartPoint.getY());

            Point2D swordStartPoint2 = players.get(1).getAnimation().getCurrentFrame().getSwordStartPointInverted();
            swordTip2 = new Point2D(players.get(1).getX() + swordStartPoint2.getX() - 2 * swordLength + offsetSword, players.get(1).getY() + swordStartPoint2.getY());
            swordGrip2 = new Point2D(players.get(1).getX() + swordStartPoint2.getX() - swordLength + offsetSword, players.get(1).getY() + swordStartPoint2.getY());

        } else { // --> p2 RIGHT
            Point2D swordStartPoint = players.get(1).getAnimation().getCurrentFrame().getSwordStartPoint();
            swordTip1 = new Point2D(players.get(1).getX() + swordStartPoint.getX() + swordLength, players.get(1).getY() + swordStartPoint.getY());

            Point2D swordStartPoint2 = players.get(0).getAnimation().getCurrentFrame().getSwordStartPointInverted();
            swordTip2 = new Point2D(players.get(0).getX() + swordStartPoint2.getX() - 2 * swordLength + offsetSword, players.get(0).getY() + swordStartPoint2.getY());
            swordGrip2 = new Point2D(players.get(0).getX() + swordStartPoint2.getX() - swordLength + offsetSword, players.get(0).getY() + swordStartPoint2.getY());
        }

        // Offset of 2 pixels in each direction, assuming that the sword is 2-5 pixels wide
        boolean onSameY = (swordTip1.getY() - 2 <= swordTip2.getY() && swordTip2.getY() <= swordTip1.getY() + 2);
        boolean onSameXInterval = (swordTip1.getX() - swordTip2.getX()) * (swordTip1.getX() - swordGrip2.getX()) <= 0;

        return onSameY && onSameXInterval;
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // --- Obstacle collisions:

    /**
     * Returns all the Obstacle-Objects that collide with the player
     */
    private ArrayList<RectangleObstacle> getPlayerObstacleCollisions(PlayerObject player) {
        ArrayList<RectangleObstacle> retO_player = new ArrayList<>(); // Obstacles player collides with atm

        // Check Avatar-Obstacle collisions
        for (RectangleObstacle obstacle : obstacles) {
            if (collisionAvatarObstacle(player, obstacle))
                retO_player.add(obstacle);
        }

        return retO_player;
    }


    /**
     * Returns true, if there is a collision between the player and the given obstacle
     */
    private boolean collisionAvatarObstacle(PlayerObject player, RectangleObstacle obstacle) {

        int playerWidth = playersWidthHeight[0];
        int playerHeight = playersWidthHeight[1];

        return (player.getX() <= obstacle.getX() + obstacle.getWidth() // Checks, that rect2 is close enough from the left side
                && player.getX() + playerWidth >= obstacle.getX() // Checks, that rect2 is close enough from the right side
                && player.getY() <= obstacle.getY() + obstacle.getHeight() // Checks, --*-- from above
                && player.getY() + playerHeight >= obstacle.getY()); // Checks, --*-- from below
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // ---  calcRectHitBox

    private void fillPlayerRectangleHitBox() {
        int x_min = Integer.MAX_VALUE, x_max = Integer.MIN_VALUE, y_min = Integer.MAX_VALUE, y_max = Integer.MIN_VALUE;
        ArrayList<Point2D> hitBoxPoints;

        hitBoxPoints = players.get(0).getAnimation().getCurrentFrame().getHitBox();
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

        rectHitBoxP1_P2[0] = new Point2D(x_min, y_min);
        rectHitBoxP1_P2[1] = new Point2D(x_max, y_max);

        // Player 1
        playersWidthHeight[0] = (int) rectHitBoxP1_P2[1].getX() - (int) rectHitBoxP1_P2[0].getY();
        playersWidthHeight[1] = (int) rectHitBoxP1_P2[1].getY() - (int) rectHitBoxP1_P2[0].getY();
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // --- Getter

    public static void setSwordLength(int length) { // static to allow call before Instance is constructed
        swordLength = length;
    }

    public int[] getPlayersWidthHeight() {
        return playersWidthHeight;
    }

    public boolean getPlayerOnGround(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? player1_onGround : player2_onGround);
    }

    public boolean getPlayer1HitPlayer2() {
        return player1_hit_player2;
    }

    public boolean getPlayer2HitPlayer1() {
        return player2_hit_player1;
    }

    public boolean getSwordsHitting() {
        return swordsHitting;
    }

    public int getSwordLength(){
        return swordLength;
    }

    public Point2D[] getRectHitBoxP1_P2(){
        return rectHitBoxP1_P2;
    }
}