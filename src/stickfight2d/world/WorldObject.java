package stickfight2d.world;

import javafx.scene.paint.Color;
import stickfight2d.Main;
import stickfight2d.controllers.KeyController;
import stickfight2d.enums.DirectionType;
import stickfight2d.enums.LevelType;
import stickfight2d.enums.PlayerType;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

public class WorldObject {

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final ArrayList<GameObject> gameObjectsToAdd = new ArrayList<>();
    private final ArrayList<SwordObject> swordObjects;
    private final LevelType levelType;

    private final int groundLevel = (int) (Main.canvas.getHeight()/2 + 100);
    private RectangleObstacle ground;

    private PlayerObject player1, player2;
    private SwordObject sword1, sword2;

    private final FPSObject fpsObject = new FPSObject();

    public WorldObject(LevelType levelType){
        this.levelType = levelType;
        swordObjects = new ArrayList<>();
        fpsObject.setPrintMode(false);
    }

    public void initObjects() {
        player1 = new PlayerObject(500, groundLevel, PlayerType.PLAYER_ONE, DirectionType.RIGHT, Config.keySet1);
        player2 = new PlayerObject(700, groundLevel, PlayerType.PLAYER_TWO, DirectionType.RIGHT, Config.keySet2);

        gameObjects.add(fpsObject);
        ground = new RectangleObstacle(0 , groundLevel, (int) Main.canvas.getWidth() ,(int) Main.canvas.getHeight() - groundLevel, Color.GREY);
        gameObjects.add(ground);

        gameObjects.addAll(getTestMap(1));

        gameObjects.add(player1);
        gameObjects.add(player2);
        gameObjects.addAll(swordObjects);
    }

    private ArrayList<RectangleObstacle> getTestMap(int mapId){
        ArrayList<RectangleObstacle> obstacles = new ArrayList<>();
        switch (mapId) { // Ground-related-parts (stairs) have to be added from top to bottom
            case 1 -> {
                int lv2 = 6, lv2_w = 100, lv2_mid = 350;
                int lv3 = 12, lv3_w = 250;
                int lv4 = 62, lv4_w = 125;
                int lv5 = 112, lv5_h = 15, lv5_w = ground.width / 2 - lv3_w, lv5_offset = 50;
                int p1_w = 15;
                // Left obstacles
                obstacles.add(new RectangleObstacle(ground.x, groundLevel - lv4, lv4_w, lv4, Color.GREY)); // Third stair
                obstacles.add(new RectangleObstacle(ground.x + lv3_w, groundLevel - lv2, lv2_w, lv2, Color.GREY)); // Second stair
                obstacles.add(new RectangleObstacle(ground.x, groundLevel - lv3, lv3_w, lv3, Color.GREY)); // First stair

                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 - lv5_w, groundLevel - lv5, lv5_w - lv5_offset, lv5_h, Color.GREY)); // First upper platform
                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 - lv5_w + p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // First pillar
                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 - lv5_offset - 2 * p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // Second pillar

                // Middle obstacles
                obstacles.add(new RectangleObstacle(ground.x + (ground.width / 2) - (lv2_mid / 2), ground.y - lv2, lv2_mid, lv2, Color.GREY)); // Mid stair

                // Right obstacles
                obstacles.add(new RectangleObstacle(ground.x + ground.width - lv4_w, groundLevel - lv4, lv4_w, lv4, Color.GREY)); // Third stair
                obstacles.add(new RectangleObstacle(ground.x + ground.width - lv2_w - lv3_w, groundLevel - lv2, lv2_w, lv2, Color.GREY)); // Second stair
                obstacles.add(new RectangleObstacle(ground.x + ground.width - lv3_w, groundLevel - lv3, lv3_w, lv3, Color.GREY)); // First stair

                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 + lv5_offset, groundLevel - lv5, lv5_w - lv5_offset, lv5_h, Color.GREY)); // First upper platform
                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 + lv5_offset + p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // First pillar
                obstacles.add(new RectangleObstacle(ground.x + ground.width / 2 + lv5_w - 2 * p1_w, groundLevel - lv5 + lv5_h, p1_w, lv5 - lv5_h - lv2, Color.LIGHTGREY)); // Second pillar
            }
        }
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
                        System.out.println("### SUCCESS ### Sword 1 <--> Player "+p.getPlayerNumber());
                    } else {
                        System.out.println("Sword not on Ground");
                    }
                    break;
                }
            }
            /*
            if(ValueRange.of(playerMiddle-100, playerMiddle+100).isValidIntValue(sword1.getX()+32)){
                if(sword1.isOnGround()){
                    p.setSwordObject(sword1);
                    sword1.setPlayerObject(p);
                    sword1.setOnGround(false);
                    sword1.setFalling(false);
                    //System.out.println("### SUCCESS ### Sword 1 <--> Player "+p.getPlayerNumber());
                }else{
                    //System.out.println("Sword not on Ground");
                }
            }else if(ValueRange.of(playerMiddle-100, playerMiddle+100).isValidIntValue(sword2.getX()+32)){
                if(sword2.isOnGround()){
                    p.setSwordObject(sword2);
                    sword2.setPlayerObject(p);
                    sword2.setOnGround(false);
                    sword2.setFalling(false);
                    //System.out.println("### SUCCESS ### Sword 2 <--> Player "+p.getPlayerNumber());
                }else{
                    //System.out.println("Sword not on Ground");
                }
            }else{
                //System.out.println("Player not in range of a sword");
            }
        }else{
            //System.out.println("Player not on Ground");
        }
        */
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
        return groundLevel;
    }

    public FPSObject getFpsObject() {
        return fpsObject;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public RectangleObstacle getGround() {
        return ground;
    }

    public PlayerObject getPlayer1() {
        return player1;
    }

    public PlayerObject getPlayer2() {
        return player2;
    }

    public SwordObject getSword1() {
        return sword1;
    }

    public SwordObject getSword2() {
        return sword2;
    }

    public void addSword(SwordObject swordObject){
        swordObjects.add(swordObject);
        gameObjectsToAdd.add(swordObject);
    }

    public void refreshGameObjects(){
        gameObjects.addAll(gameObjectsToAdd);
        gameObjectsToAdd.clear();
    }
}
