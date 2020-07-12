package stickfight2d.world;

import javafx.scene.paint.Color;
import stickfight2d.Main;
import stickfight2d.controllers.KeyController;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.PlayerType;
import stickfight2d.misc.Config;
import stickfight2d.misc.Debugger;
import stickfight2d.misc.FPSObject;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class WorldObject {

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final ArrayList<GameObject> gameObjectsToAdd = new ArrayList<>();
    private final ArrayList<GameObject> gameObjectsToRemove = new ArrayList<>();
    private final Queue<SwordObject> swordObjects;

    private final ArrayList<RectangleObstacle> grounds = new ArrayList<>();
    private BackgroundObject background;

    private PlayerObject player1, player2;

    private final FPSObject fpsObject = new FPSObject();


    public WorldObject(){
        swordObjects = new LinkedList<>();
        fpsObject.setPrintMode(true);
    }

    public void initObjects() {
        player1 = new PlayerObject(275, 500, PlayerType.PLAYER_ONE, DirectionType.RIGHT, Config.keySet1);
        player2 = new PlayerObject((int) Main.canvas.getWidth() - 275, 500, PlayerType.PLAYER_TWO, DirectionType.LEFT, Config.keySet2);

        gameObjects.add(fpsObject);

        background = new BackgroundObject(0,0,null);
        gameObjects.add(background);

        gameObjects.addAll(getMapObstacles());

        gameObjects.add(player1);
        gameObjects.add(player2);
        gameObjects.addAll(swordObjects);
    }


    private ArrayList<RectangleObstacle> getMapObstacles() {
        ArrayList<RectangleObstacle> obstacles = new ArrayList<>();
        int mapState = 0;
        obstacles.add(new RectangleObstacle(-80, 0, 80, 810, Color.RED, -1)); // Player1 block boundary
        obstacles.add(new RectangleObstacle(340, 584, 14, 160, Color.RED, -3)); // Player1 block cave

        obstacles.add(new RectangleObstacle(1032, 0, 80, 810, Color.BLUE, -2)); // Player2 block boundary
        obstacles.add(new RectangleObstacle(692 - 14, 584, 14, 160, Color.BLUE, -4)); // Player2 block cave

        // Map 00
        obstacles.add(new RectangleObstacle(130, 600, 74, 155, Color.GREEN, mapState)); // Left cave wall
        obstacles.add(new RectangleObstacle(380, 532, 114, 40, Color.GREEN, mapState)); // Upper cave wall
        obstacles.add(new RectangleObstacle(495, 532, 82, 22, Color.GREEN, mapState)); // Upper cave wall entrance
        obstacles.add(new RectangleObstacle(320, 768, 24, 135, Color.GREEN, mapState)); // Cave stair 01 (from left to right)
        obstacles.add(new RectangleObstacle(343, 744, 71, 810 - 744, Color.GREEN, mapState)); // Cave stair 02
        obstacles.add(new RectangleObstacle(413, 720, 73, 810 - 720, Color.GREEN, mapState)); // Cave stair 03
        obstacles.add(new RectangleObstacle(484, 696, 73, 810 - 696, Color.GREEN, mapState)); // Cave stair 04
        obstacles.add(new RectangleObstacle(506, 671, 72, 810 - 671, Color.GREEN, mapState)); // Cave stair 05
        obstacles.add(new RectangleObstacle(117, 808, 31, 101, Color.GREEN, mapState)); // Cave tunnel left wall
        obstacles.add(new RectangleObstacle(497, 1044, 72, 810 * 2 - 671, Color.GREEN, mapState)); // Right cave wall
        obstacles.add(new RectangleObstacle(-44, 1044, 50, 810 * 2 - 671, Color.GREEN, mapState)); // Left cave wall

        obstacles.add(new RectangleObstacle(5,1323,495,810 * 2 - 1328, Color.GREEN, mapState)); // cave ground
        obstacles.add(new RectangleObstacle(577, 648, 456, 810 - 648, Color.GREEN, mapState)); // ground
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;

        // Map 01
        obstacles.add(new RectangleObstacle(370, 624, 24, 30, Color.GREEN, mapState)); // Stair base 01 (from bottom to top)
        obstacles.add(new RectangleObstacle(391, 575, 47, 80, Color.GREEN, mapState)); // Stair base 03
        obstacles.add(new RectangleObstacle(437, 599, 47, 62, Color.GREEN, mapState)); // Stair base 02

        obstacles.add(new RectangleObstacle(0, 648, (int) Main.canvas.getWidth(), 162, Color.GREEN, mapState)); // ground
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;

        // Map 02
        obstacles.add(new RectangleObstacle(121, 624, 90, 30, Color.GREEN, mapState)); // Stair 01 left (from bottom to top)
        obstacles.add(new RectangleObstacle(144, 601, 43, 30, Color.GREEN, mapState)); // Stair 02 left
        obstacles.add(new RectangleObstacle(457, 571, 118, 15, Color.GREEN, mapState)); // Floating Island
        obstacles.add(new RectangleObstacle(822, 625, 90, 30, Color.GREEN, mapState)); // Stair 01 right (from bottom to top)
        obstacles.add(new RectangleObstacle(845, 601, 43, 30, Color.GREEN, mapState)); // Stair 02 right

        obstacles.add(new RectangleObstacle(0, 648, (int) Main.canvas.getWidth(), 162, Color.GREEN, mapState)); // ground
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;

        // Map 03
        obstacles.add(new RectangleObstacle(551, 601, 47, 62, Color.GREEN, mapState)); // Stair base 02 (from bottom to top)
        obstacles.add(new RectangleObstacle(596, 576, 47, 80, Color.GREEN, mapState)); // Stair base 03
        obstacles.add(new RectangleObstacle(642, 624, 24, 30, Color.GREEN, mapState)); // Stair base 01

        obstacles.add(new RectangleObstacle(0, 648, (int) Main.canvas.getWidth(), 162, Color.GREEN, mapState)); // ground
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;
        // Map 04
        obstacles.add(new RectangleObstacle(902 - 74, 600, 74, 155, Color.GREEN, mapState)); // Left cave wall
        obstacles.add(new RectangleObstacle(652 - 114, 532, 114, 40, Color.GREEN, mapState)); // Upper cave wall
        obstacles.add(new RectangleObstacle(537 - 82, 532, 82, 22, Color.GREEN, mapState)); // Upper cave wall entrance
        obstacles.add(new RectangleObstacle(712 - 24, 768, 24, 135, Color.GREEN, mapState)); // Cave stair 01 (from right to left)
        obstacles.add(new RectangleObstacle(689 - 71, 744, 71, 810 - 744, Color.GREEN, mapState)); // Cave stair 02
        obstacles.add(new RectangleObstacle(619 - 73, 720, 73, 810 - 720, Color.GREEN, mapState)); // Cave stair 03
        obstacles.add(new RectangleObstacle(548 - 73, 696, 73, 810 - 696, Color.GREEN, mapState)); // Cave stair 04
        obstacles.add(new RectangleObstacle(526 - 72, 671, 72, 810 - 671, Color.GREEN, mapState)); // Cave stair 05
        obstacles.add(new RectangleObstacle(915 - 31, 808, 31, 101, Color.GREEN, mapState)); // Cave tunnel left wall
        obstacles.add(new RectangleObstacle(535 - 72, 1044, 72, 810 * 2 - 671, Color.GREEN, mapState)); // Right cave wall
        obstacles.add(new RectangleObstacle(1076 - 50, 1044, 50, 810 * 2 - 671, Color.GREEN, mapState)); // Left cave wall

        obstacles.add(new RectangleObstacle(1027 - 495,1323,495,810 * 2 - 1328, Color.GREEN, mapState)); // cave ground

        obstacles.add(new RectangleObstacle(0, 648, 455, 810 - 648, Color.GREEN, mapState)); // ground
        grounds.add(obstacles.get(obstacles.size() - 1));

        return obstacles;
    }

    public void takeSwordFromGround(PlayerObject p){
        int playerMiddle = p.x + 32;

        if(p.isOnGround()) {
            for (SwordObject sword : swordObjects) {

                if (ValueRange.of(playerMiddle - 100, playerMiddle + 100).isValidIntValue(sword.getX() + 32)) {
                    if (sword.isOnGround()) {
                        p.setSwordObject(sword);
                        sword.setPlayerObject(p);
                        sword.setOnGround(false);
                        sword.setFalling(false);
                        p.resetAnimationToIdle();
                        Debugger.log("### SUCCESS ### Sword 1 <--> Player "+p.getPlayerNumber());
                    } else {
                        Debugger.log("Sword not on Ground");
                    }
                    break;
                }
            }
        }
    }

    public void clearSwordsOnGround(){
        for (SwordObject sword : swordObjects) {
            if(sword.isOnGround()){
                removeGameObject(sword);
            }
        }
    }

    public void respawnPlayer(PlayerObject p) {

        switch (p.getPlayerNumber()) {
            case PLAYER_ONE -> {
                p.x = (int) background.getCurrentSpawnPoint(PlayerType.PLAYER_ONE).getX();
                KeyController.getInstance().setKeyPressBlockedP1(false);
            }
            case PLAYER_TWO -> {
                p.x = (int) background.getCurrentSpawnPoint(PlayerType.PLAYER_TWO).getX();
                KeyController.getInstance().setKeyPressBlockedP2(false);
            }
        }
        p.y = getGroundLevel();
        p.reset();
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public int getGroundLevel() {
        return getGround().getY();
    }

    public BackgroundObject getBackground(){
        return background;
    }

    public FPSObject getFpsObject() {
        return fpsObject;
    }

    public RectangleObstacle getGround() {
        return grounds.get(background.getWorldState());
    }

    public PlayerObject getPlayer1() {
        return player1;
    }

    public PlayerObject getPlayer2() {
        return player2;
    }

    public void addSword(SwordObject swordObject){

        // If the amount of swords in the world is greater than 4
        // First remove one sword without a player reference
        // And then add the new one
        if(swordObjects.size() >= 4){
            for (SwordObject sword : swordObjects) {
                if(sword.getPlayerObject() == null){
                    removeGameObject(sword);
                    swordObjects.remove(sword);
                    break;
                }
            }
        }

        swordObjects.add(swordObject);
        addGameObject(swordObject);
    }

    public void addGameObject(GameObject gameObject){
        gameObjectsToAdd.add(gameObject);
    }

    public void removeGameObject(GameObject gameObject){
        gameObjectsToRemove.add(gameObject);
    }

    public void refreshGameObjects(){
        gameObjects.addAll(gameObjectsToAdd);
        gameObjects.removeAll(gameObjectsToRemove);
        gameObjectsToRemove.clear();
        gameObjectsToAdd.clear();
    }
}
