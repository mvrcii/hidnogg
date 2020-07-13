package stickfight2d.controllers;

import javafx.geometry.Point2D;
import stickfight2d.GameLoop;
import stickfight2d.Main;
import stickfight2d.enums.AnimationType;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.PlayerType;
import stickfight2d.misc.Debugger;
import stickfight2d.world.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollisionController extends Controller {

    // ----------------------------------------------------------------------------------------------------
    // --- Instance & Constructor

    private static CollisionController instance;

    public static CollisionController getInstance() {
        if (instance == null) {
            Debugger.log("Collision Controller instantiated");
            instance = new CollisionController();
        }
        return instance;
    }

    // --- World Data
    // --- --- Obstacle Data
    private final ArrayList<RectangleObstacle> obstacles = new ArrayList<>();
    private final BackgroundObject background = GameLoop.currentLevel.getBackground();

    // --- --- Player Data
    private final ArrayList<PlayerObject> players = new ArrayList<>();

    // --- --- --- Attack related data
    private final HashSet<AnimationType> nonStabAnimations = Stream.of(
            AnimationType.PLAYER_WALK,
            AnimationType.PLAYER_IDLE_HOLD_UP,
            AnimationType.PLAYER_JUMP_START,
            AnimationType.PLAYER_JUMP_PEAK,
            AnimationType.PLAYER_JUMP_END,
            AnimationType.PLAYER_DYING,
            AnimationType.SWORD,
            AnimationType.PLAYER_IDLE_NO_SWORD,
            AnimationType.PLAYER_STAB_NO_SWORD,
            AnimationType.PLAYER_CROUCH,
            AnimationType.PLAYER_WIN).collect(Collectors.toCollection(HashSet::new));
    private static int swordLength = 0; // swordLength for sword-tip-calculation

    // --- --- --- Obstacle-Collision related data
    private final Point2D[] rectHitBoxP1_P2 = new Point2D[2]; // Contains upper left X,Y and bottom right X,Y of both players
    private final int[] playersWidthHeight = new int[2]; // Contains Width and Height of both players
    // ----------------------------------------------------------------------------------------------------

    // --- Player states
    // --- --- Obstacle related states
    private boolean player1_onGround = true;
    private boolean player1_hitsWall_Left = false;
    private boolean player1_hitsWall_Right = false;
    private boolean player1_headBump = false;
    private boolean player2_onGround = true;
    private boolean player2_hitsWall_Left = false;
    private boolean player2_hitsWall_Right = false;
    private boolean player2_headBump = false;
    private final boolean[] inCave = new boolean[2];

    // --- --- Attack related states
    private boolean player1_hit_player2 = false;
    private boolean player2_hit_player1 = false;
    private boolean swordsHitting = false;
    private boolean attackBlocked = false;
    private AnimationType p1_prevState = AnimationType.PLAYER_IDLE_MEDIUM;
    private AnimationType p2_prevState = AnimationType.PLAYER_IDLE_MEDIUM;
    private int disarming = 0;
    // ----------------------------------------------------------------------------------------------------


    private CollisionController() {
        for (GameObject obj : GameLoop.currentLevel.getGameObjects()) { // Collect PlayerObjects
            if (obj instanceof PlayerObject) {
                players.add((PlayerObject) obj);
                ((PlayerObject) obj).currentObstacleStanding = GameLoop.currentLevel.getGround();

            } else if (obj instanceof RectangleObstacle) { // Collect RectangleObjects
                obstacles.add((RectangleObstacle) obj);
            }
        }

        calculatePlayerRectangleHitBox();
    }


    /**
     * Updates information on player states
     */
    @Override
    public void update(long diffMillis) {
        updatePlayerObstacleCollisions(players.get(0));
        updatePlayerObstacleCollisions(players.get(1));

        player1_hit_player2 = collisionSwordAvatar(players.get(0), players.get(1));
        player2_hit_player1 = collisionSwordAvatar(players.get(1), players.get(0));

        swordsHitting = checkCollisionSwordSword();
        disarming = checkDisarm();

        checkWinningCondition();
        checkMapBoundaries();
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // --- Attack collisions

    /**
     * @param player1 Attacking player
     * @param player2 Player who is being attacked
     * @return [true] if player1-sword hits player2, [false] otherwise
     */
    private boolean collisionSwordAvatar(PlayerObject player1, PlayerObject player2) {
        if (nonStabAnimations.contains(player1.getAnimation().getAnimationType()) || player1.getSwordObject() == null) // Prevent horizontal stabbing in specific animations
            return false;

        Point2D swordTip;
        ArrayList<Point2D> hitBox_Player2;
        int offsetHitBox = 0;
        int offset_x = swordLength;

        // Blocking (sword-start point)
        Point2D player2_block;

        // Get relevant hitBox of player2 and swordTip-position of player1
        if (player2.getDirectionType() == DirectionType.RIGHT) { // --> player1 direction must be Direction.LEFT
            hitBox_Player2 = player2.getAnimation().getCurrentFrame().getHitBox();
            offset_x *= (-1);

            Point2D swordStartPoint = player1.getAnimation().getCurrentFrame().getSwordStartPointInverted();
            swordTip = new Point2D(player1.getX() + swordStartPoint.getX() - playersWidthHeight[0] - swordLength, player1.getY() + swordStartPoint.getY());
            player2_block = player2.getAnimation().getCurrentFrame().getSwordStartPoint();

        } else { // --> player1 direction must be Direction.RIGHT
            hitBox_Player2 = player2.getAnimation().getCurrentFrame().getHitBoxInverted();
            offsetHitBox = playersWidthHeight[0] + 2;

            Point2D swordStartPoint = player1.getAnimation().getCurrentFrame().getSwordStartPoint();
            swordTip = new Point2D(player1.getX() + swordStartPoint.getX() + swordLength, player1.getY() + swordStartPoint.getY());
            player2_block = player2.getAnimation().getCurrentFrame().getSwordStartPointInverted();
        }

        /* Blocking
         * Check, if attacked-player is in defensive-animation
         *  >> is the SwordTip at the height of the defensive-sword
         *      >> Check if attack has to be blocked on x-axis depending on direction the player is looking at
         */
        if (player2.getAnimation().getAnimationType() == AnimationType.PLAYER_IDLE_HOLD_UP) {
            if (swordTip.getY() <= player2.getY() + player2_block.getY() && swordTip.getY() >= player2.getY() + player2_block.getY() - swordLength) {

                attackBlocked = (player2.getDirectionType() == DirectionType.RIGHT && swordTip.getX() <= player2.getX() + player2_block.getX() && player1.getX() > player2.getX())
                        || (player2.getDirectionType() == DirectionType.LEFT && swordTip.getX() >= player2.getX() + player2_block.getX() - offsetHitBox && player1.getX() < player2.getX());
            }
        }

        /*
         * Get x-points of the hitBox that are relevant in the sword-tip y-coordinate
         */
        int x_front = Integer.MIN_VALUE;
        int x_back = Integer.MIN_VALUE;

        for (Point2D p : hitBox_Player2) {
            if (swordTip.getY() == player2.getY() + p.getY()) { // X and Y must be ints
                if (x_front == Integer.MIN_VALUE)
                    x_front = (int) p.getX() + offset_x;
                else
                    x_back = (int) p.getX();
            }
        }

        if (x_back == Integer.MIN_VALUE) // No collision possible, sword isn't on the same y position as the player
            return false;

        int firstSign = ((int) swordTip.getX() - (x_front + player2.getX() - offsetHitBox));
        int secondSign = ((int) swordTip.getX() - (x_back + player2.getX() - offsetHitBox));

        return firstSign * secondSign <= 0; // Hit: negative or zero ; Miss: positive
    }


    /**
     * @return [true] if swords of players collide, [false] otherwise
     */
    private boolean checkCollisionSwordSword() {
        if (players.get(0).getSwordObject() == null || players.get(1).getSwordObject() == null // Player has no sword
                || nonStabAnimations.contains(players.get(0).getAnimation().getAnimationType())  // Player 1 in hold-up animation
                || nonStabAnimations.contains(players.get(1).getAnimation().getAnimationType())) // Player 2 in hold-up animation
            return false;

        Point2D swordTip1, swordGrip2, swordTip2;
        int offsetSword = 4;

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


    /**
     * Checks, if a player is being disarmed
     *
     * @return [1] if player1 disarms player2, [2] if player2 disarms player1, [0] otherwise
     */
    private int checkDisarm() {
        if (players.get(0).getSwordObject() == null || players.get(1).getSwordObject() == null)
            return 0;

        AnimationType p1_anim = players.get(0).getAnimation().getAnimationType();
        AnimationType p2_anim = players.get(1).getAnimation().getAnimationType();
        int type = 0;

        if (!(p1_anim != p2_anim || p1_prevState == p2_prevState) && !(p1_prevState == AnimationType.PLAYER_WALK || p2_prevState == AnimationType.PLAYER_WALK)) {
            // Invariant: p1 and p2 are in the same animation, one of both had a different previous animation

            if (p1_anim == p1_prevState && swordsHitting)
                type = 2;

            else if (p2_anim == p2_prevState && swordsHitting)
                type = 1;
        }

        // Update player states
        p1_prevState = p1_anim;
        p2_prevState = p2_anim;
        return type;
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // --- Obstacle collisions:

    /**
     * Updates collision-states of a player
     *
     * @param player player whose collision states are being updated
     */
    private void updatePlayerObstacleCollisions(PlayerObject player) {
        // Flags that have to be set
        boolean onGround = false;
        boolean hitsWallRight = false;
        boolean hitsWallLeft = false;
        boolean headBump = false;
        DirectionType runningDirection = background.getCurrentEnabledRunningDirection();

        // Check Avatar-Obstacle collisions
        for (RectangleObstacle obstacle : obstacles) {
            int currentMapState = background.getWorldState();
            int obstacleMapState = obstacle.getMapState();
            PlayerType playerNumber = player.getPlayerNumber();

            if (obstacleMapState != currentMapState && obstacleMapState >= 0 // Obstacle not in current map state
                    || (!collisionRectRect(player, obstacle, 0, 0, 0, 0)) // Rect-Rect collision
                    || ((obstacleMapState == -2 && playerNumber == PlayerType.PLAYER_ONE && runningDirection == DirectionType.RIGHT)) // Let player1 pass through right boundary
                    || (obstacleMapState == -1 && playerNumber == PlayerType.PLAYER_TWO && runningDirection == DirectionType.LEFT) // Let player2 pass through left boundary
                    || ((obstacleMapState == -3 || obstacleMapState == -4) && (currentMapState != 0 && currentMapState != 4)) // Let anyone pass through cave obstacles in worlds 1,2,3
                    || (obstacleMapState == -4 && playerNumber == PlayerType.PLAYER_ONE) // Let player1 pass through right cave blocker
                    || (obstacleMapState == -3 && playerNumber == PlayerType.PLAYER_TWO) // Let player2 pass through left cave blocker
                    || (obstacleMapState == -3 && currentMapState == 4) // In world 4, let anyone pass through left cave blocker
                    || (obstacleMapState == -4 && currentMapState == 0)) // In world 0, let anyone pass through right cave blocker
                continue;

            // Ground collision
            if (collisionRectRect(player, obstacle, 8, 8, playersWidthHeight[1], 0)) {
                onGround = true;
                player.currentObstacleStanding = obstacle;
            }

            // Head collision in caves only (not in map-start-state)
            if (collisionRectRect(player, obstacle, 10, 10, 0, playersWidthHeight[1]) && (GameLoop.currentLevel.getBackground().getWorldState() != 2))
                headBump = true;

            // Wall collisions
            if (collisionRectRect(player, obstacle, playersWidthHeight[0], 0, 12, 12)) // Rect-Line collision >> Wall-right
                hitsWallRight = true;
            else if (collisionRectRect(player, obstacle, 0, playersWidthHeight[0], 12, 12)) // Rect-Line collision >> Wall-left
                hitsWallLeft = true;

            // Break if relevant obstacles have been found
            if ((hitsWallRight || hitsWallLeft) && onGround) // States have been set, no need to continue
                break;
        }

        // Update states of player
        if (player.getPlayerNumber() == PlayerType.PLAYER_ONE) {
            player1_onGround = onGround;
            player1_hitsWall_Left = hitsWallRight;
            player1_hitsWall_Right = hitsWallLeft;
            player1_headBump = headBump;
        } else {
            player2_onGround = onGround;
            player2_hitsWall_Left = hitsWallRight;
            player2_hitsWall_Right = hitsWallLeft;
            player2_headBump = headBump;
        }
    }


    /**
     * Checks, if a players rectangle hitBox collides with an obstacle
     *
     * @param player   Player whose collision is being checked
     * @param obstacle Obstacle that is supposed to collide with the player
     * @param x1       Offset on the left side of the players rectangle hitBox (added)
     * @param x2       Offset on the right side of the players rectangle hitBox (subtracted)
     * @param y1       Offset on the top of the players hitBox (added)
     * @param y2       Offset on the bottom of the players hitBox (subtracted)
     * @return [true] if there is a collision between {player} and {obstacle}
     */
    private boolean collisionRectRect(PlayerObject player, RectangleObstacle obstacle, int x1, int x2, int y1, int y2) {
        return (player.getX() + x1 <= obstacle.getX() + obstacle.getWidth() // Checks, that rect2 is close enough from the left side
                && player.getX() + playersWidthHeight[0] - x2 >= obstacle.getX() // Checks, that rect2 is close enough from the right side
                && player.getY() + y1 <= obstacle.getY() + obstacle.getHeight() // Checks, --*-- from above
                && player.getY() + playersWidthHeight[1] - y2 >= obstacle.getY()); // Checks, --*-- from below
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // ---  checkMapBoundaries / winningCondition

    /**
     * Updates the map state, if a player reaches the opposite map boundary
     */
    private void checkMapBoundaries() {
        CameraController cam = CameraController.getInstance();
        Point2D player1 = cam.convertWorldToScreen(players.get(0).getX(), players.get(0).getY());
        Point2D player2 = cam.convertWorldToScreen(players.get(1).getX(), players.get(0).getY());
        Point2D map_begin = cam.convertWorldToScreen(0, 0);
        Point2D map_end = cam.convertWorldToScreen((int) Main.canvas.getWidth(), 0);
        DirectionType currentRunningDirection = background.getCurrentEnabledRunningDirection();

        if (player1.getX() + playersWidthHeight[0] / 2.0 > map_end.getX() && currentRunningDirection == DirectionType.RIGHT) // Player1 leaves map boundary on the right side
            background.setWorldState(background.getWorldState() + 1, players.get(0), players.get(1));

        else if (player2.getX() - playersWidthHeight[0] / 2.0 < map_begin.getX() && currentRunningDirection == DirectionType.LEFT) // Player2 leaves map boundary on the left side
            background.setWorldState(background.getWorldState() - 1, players.get(0), players.get(1));
    }


    /**
     * Sets boolean flag for player, that reaches the cave
     */
    private void checkWinningCondition() {
        CameraController cam = CameraController.getInstance();
        Point2D player1 = cam.convertWorldToScreen(players.get(0).getX(), players.get(0).getY());
        Point2D player2 = cam.convertWorldToScreen(players.get(1).getX(), players.get(1).getY());
        Point2D ground = cam.convertWorldToScreen(0, (int) Main.canvas.getHeight() * 4 / 3);

        if (player1.getY() > ground.getY() && player1_onGround && background.getWorldState() == 4) {
            inCave[0] = true;
        } else if (player2.getY() > ground.getY() && player2_onGround && background.getWorldState() == 0) {
            inCave[1] = true;
        }
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // ---  calcRectHitBox

    private void calculatePlayerRectangleHitBox() {
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
    // --- Getter/Setter - PlayerHitBox :: Player Data

    public HashSet<AnimationType> getNonStabAnimations() {
        return nonStabAnimations;
    }

    public static void setSwordLength(int length) { // static to allow call before Instance is constructed
        swordLength = length;
    }

    public int[] getPlayersWidthHeight() {
        return playersWidthHeight;
    }

    public int getSwordLength() {
        return swordLength;
    }

    public Point2D[] getRectHitBoxP1_P2() {
        return rectHitBoxP1_P2;
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // --- Getter/Setter - PlayerStates :: Obstacle / Map related

    public boolean getPlayerOnGround(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? player1_onGround : player2_onGround);
    }

    public boolean getPlayerHeadBump(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? player1_headBump : player2_headBump);
    }

    public boolean getPlayerHitsWallLeft(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? player1_hitsWall_Left : player2_hitsWall_Left);
    }

    public boolean getPlayerHitsWallRight(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? player1_hitsWall_Right : player2_hitsWall_Right);
    }

    public boolean getPlayerHitsWall(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? (player1_hitsWall_Left || player1_hitsWall_Right) : (player2_hitsWall_Left || player2_hitsWall_Right));
    }

    public boolean getWin(PlayerType type) {
        return (type == PlayerType.PLAYER_ONE) ? inCave[0] : inCave[1];
    }


    // ----------------------------------------------------------------------------------------------------
    // ----------------------------------------------------------------------------------------------------
    // --- Getter/Setter - PlayerStates :: Attack related

    public boolean getPlayerHitOtherPlayer(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? player1_hit_player2 : player2_hit_player1);
    }

    public boolean getPlayerHit(PlayerType type) {
        return ((type == PlayerType.PLAYER_ONE) ? player2_hit_player1 : player1_hit_player2);
    }

    public boolean getSwordsHitting() {
        return swordsHitting;
    }

    public boolean isAttackBlocked() {
        return attackBlocked;
    }

    public boolean getPlayerBeingDisarmed(PlayerType type) {
        if (disarming == 0)
            return false;

        return (type == PlayerType.PLAYER_TWO && disarming == 1) || (type == PlayerType.PLAYER_ONE && disarming == 2);
    }

    public PlayerObject getPlayerThatDisarmed(PlayerType type){
        if(type == PlayerType.PLAYER_TWO && disarming == 1)
            return GameLoop.currentLevel.getPlayer2();
        if(type == PlayerType.PLAYER_ONE && disarming == 2)
            return GameLoop.currentLevel.getPlayer1();
        return null;
    }
}