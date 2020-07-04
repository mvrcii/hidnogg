package stickfight2d.world;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kuusisto.tinysound.Music;
import org.w3c.dom.css.Rect;
import stickfight2d.GameLoop;
import stickfight2d.Main;
import stickfight2d.controllers.KeyController;
import stickfight2d.controllers.SoundController;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.LevelType;
import stickfight2d.enums.PlayerType;
import stickfight2d.enums.SoundType;
import stickfight2d.misc.Config;
import stickfight2d.misc.Debugger;
import stickfight2d.misc.FPSObject;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class WorldObject {

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final ArrayList<GameObject> gameObjectsToAdd = new ArrayList<>();
    private final ArrayList<GameObject> gameObjectsToRemove = new ArrayList<>();
    private final Queue<SwordObject> swordObjects;
    private final LevelType levelType;

    private final int groundLevel = (int) (Main.canvas.getHeight()/2 + 100);
    private final ArrayList<RectangleObstacle> grounds = new ArrayList<>();
    private BackgroundObject background;

    private PlayerObject player1, player2;

    private final FPSObject fpsObject = new FPSObject();


    public WorldObject(LevelType levelType){
        this.levelType = levelType;
        swordObjects = new LinkedList<>();
        fpsObject.setPrintMode(true);
    }

    public void initObjects() {
        player1 = new PlayerObject(500, groundLevel, PlayerType.PLAYER_ONE, DirectionType.RIGHT, Config.keySet1);
        player2 = new PlayerObject(700, groundLevel, PlayerType.PLAYER_TWO, DirectionType.RIGHT, Config.keySet2);

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

        // Map 00
        obstacles.add(new RectangleObstacle(130, 600, 74, 155, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(380, 532, 114, 40, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(495, 532, 82, 22, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(320, 768, 24, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(343, 744, 71, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(413, 720, 73, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(484, 696, 73, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(506, 671, 71, 30, Color.GREEN, mapState));

        obstacles.add(new RectangleObstacle(577, 648, 456, 30, Color.GREEN, mapState));
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;

        // Map 01
        obstacles.add(new RectangleObstacle(370, 624, 24, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(391, 575, 47, 80, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(437, 599, 47, 62, Color.GREEN, mapState));

        obstacles.add(new RectangleObstacle(0, 648, (int) Main.canvas.getWidth(), 20, Color.GREEN, mapState));
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;

        // Map 02
        obstacles.add(new RectangleObstacle(121, 624, 90, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(144, 601, 43, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(460, 571, 118, 12, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(822, 625, 90, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(845, 601, 43, 30, Color.GREEN, mapState));

        obstacles.add(new RectangleObstacle(0, 648, (int) Main.canvas.getWidth(), 20, Color.GREEN, mapState));
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;

        // Map 03
        obstacles.add(new RectangleObstacle(551, 601, 47, 62, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(596, 576, 47, 80, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(642, 624, 24, 30, Color.GREEN, mapState));

        obstacles.add(new RectangleObstacle(0, 648, (int) Main.canvas.getWidth(), 20, Color.GREEN, mapState));
        grounds.add(obstacles.get(obstacles.size() - 1));

        mapState++;
        // Map 04
        obstacles.add(new RectangleObstacle(902 - 74, 600, 74, 155, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(652 - 114, 532, 114, 40, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(537 - 82, 532, 82, 22, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(712 - 24, 768, 24, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(689 - 71, 744, 71, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(619 - 73, 720, 73, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(548 - 73, 696, 73, 30, Color.GREEN, mapState));
        obstacles.add(new RectangleObstacle(526 - 71, 671, 71, 30, Color.GREEN, mapState));

        obstacles.add(new RectangleObstacle(0, 648, 455, 30, Color.GREEN, mapState));
        grounds.add(obstacles.get(obstacles.size() - 1));

        return obstacles;
    }

//    private ArrayList<RectangleObstacle> getTestMap(int mapId){
//        ArrayList<RectangleObstacle> obstacles = new ArrayList<>();
//        switch (mapId) { // Ground-related-parts (stairs) have to be added from top to bottom
//            case 1 -> {
//                int lv2 = 6, lv2_w = 100, lv2_mid = 350;
//                int lv3 = 12, lv3_w = 250;
//                int lv4 = 62, lv4_w = 125;
//                int lv5 = 112, lv5_h = 15, lv5_w = ground.width / 2 - lv3_w, lv5_offset = 50;
//                int p1_w = 15;
//                // Left obstacles
//                obstacles.add(new RectangleObstacle(ground.x, groundLevel - lv4, lv4_w, lv4, Color.GREY)); // Third stair
//                obstacles.add(new RectangleObstacle(ground.x + lv3_w, groundLevel - lv2, lv2_w, lv2, Color.GREY)); // Second stair
//                obstacles.add(new RectangleObstacle(ground.x, groundLevel - lv3, lv3_w, lv3, Color.GREY)); // First stair
//
//                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 - lv5_w, groundLevel - lv5, lv5_w - lv5_offset, lv5_h, Color.GREY)); // First upper platform
//                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 - lv5_w + p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // First pillar
//                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 - lv5_offset - 2 * p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // Second pillar
//
//                // Middle obstacles
//                obstacles.add(new RectangleObstacle(ground.x + (ground.width / 2) - (lv2_mid / 2), ground.y - lv2, lv2_mid, lv2, Color.GREY)); // Mid stair
//
//                // Right obstacles
//                obstacles.add(new RectangleObstacle(ground.x + ground.width - lv4_w, groundLevel - lv4, lv4_w, lv4, Color.GREY)); // Third stair
//                obstacles.add(new RectangleObstacle(ground.x + ground.width - lv2_w - lv3_w, groundLevel - lv2, lv2_w, lv2, Color.GREY)); // Second stair
//                obstacles.add(new RectangleObstacle(ground.x + ground.width - lv3_w, groundLevel - lv3, lv3_w, lv3, Color.GREY)); // First stair
//
//                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 + lv5_offset, groundLevel - lv5, lv5_w - lv5_offset, lv5_h, Color.GREY)); // First upper platform
//                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 + lv5_offset + p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // First pillar
//                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 + lv5_w - 2 * p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // Second pillar
//            }
//        }
//        return obstacles;
//    }

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

    public void respawnPlayer(PlayerObject p){

        switch (p.getPlayerNumber()){
            case PLAYER_ONE -> {
                p.x = (int) (Main.canvas.getWidth()*0.20);
                KeyController.getInstance().setKeyPressBlockedP1(false);
            }
            case PLAYER_TWO -> {
                p.x = (int) (Main.canvas.getWidth()*0.80);
                KeyController.getInstance().setKeyPressBlockedP2(false);
            }
        }
        p.y = groundLevel;
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
        if(swordObjects.size() == 4){
            removeGameObject(swordObjects.remove());
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
