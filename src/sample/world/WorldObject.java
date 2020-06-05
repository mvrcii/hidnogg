package sample.world;

import javafx.scene.paint.Color;
import sample.Main;
import sample.enums.DirectionType;
import sample.enums.LevelType;
import sample.enums.PlayerType;

import java.time.temporal.ValueRange;
import java.util.ArrayList;

public class WorldObject {

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final LevelType levelType;

    private final int groundLevel = (int) (Main.canvas.getHeight()/2);
    private RectangleObstacle ground;

    private PlayerObject player1, player2;
    private SwordObject sword1, sword2;

    private final FPSObject fpsObject = new FPSObject();

    public WorldObject(LevelType levelType){
        this.levelType = levelType;

        initObjects();
        fpsObject.setPrintMode(false);
    }

    private void initObjects() {
        player1 = new PlayerObject(500, groundLevel, PlayerType.PLAYER_ONE, DirectionType.RIGHT, Config.keySet1);
        player2 = new PlayerObject(700, groundLevel, PlayerType.PLAYER_TWO, DirectionType.RIGHT, Config.keySet2);

        sword1 = new SwordObject(400, 400, DirectionType.RIGHT, player1);
        sword2 = new SwordObject(400, 400, DirectionType.RIGHT, player2);

        ground = new RectangleObstacle(0, groundLevel, (int) Main.canvas.getWidth(),20, Color.GREEN);

        gameObjects.add(fpsObject);
        gameObjects.add(ground);

        gameObjects.add(player1);
        gameObjects.add(player2);
        gameObjects.add(sword1);
        gameObjects.add(sword2);

        player1.setSwordObject(sword1);
        player2.setSwordObject(sword2);
    }

    public void checkForSword(PlayerObject p){
        int playerMiddle = p.x + 32;

        if(p.isOnGround()){
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
}
