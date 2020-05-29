package sample.controllers;


import sample.GameLoop;
import sample.enums.Direction;
import sample.world.GameObject;
import sample.world.PlayerObject;

import java.util.ArrayList;

public class DirectionController extends Controller{

    private ArrayList<GameObject> gameObjects;
    private ArrayList<PlayerObject> playerObjects = new ArrayList<>();

    private static DirectionController instance;

    public static DirectionController getInstance() {
        if (instance == null) {
            System.out.println("Direction Controller instantiated");
            instance = new DirectionController();
        }
        return instance;
    }

    /**
     * Takes care that the players look at each other at all times
     */

    private DirectionController(){
    }

    @Override
    public void update(long diffMillis) {
        updatePlayerDirection();
    }


    private void getPlayers(){
        this.gameObjects = GameLoop.currentLevel.getGameObjects();
        for (GameObject obj : gameObjects) {
            if(obj instanceof PlayerObject){
                playerObjects.add((PlayerObject) obj);
            }
        }

    }

    private void updatePlayerDirection(){
        if(playerObjects.size()==2){
            PlayerObject p1 = playerObjects.get(0);
            PlayerObject p2 = playerObjects.get(1);
            if (p1.getX() > p2.getX()){
                if(p1.getDirection()!=Direction.LEFT || p2.getDirection()!=Direction.RIGHT){
                    p1.setDirection(Direction.LEFT);
                    p2.setDirection(Direction.RIGHT);
                }
            }else{
                if(p1.getDirection()!=Direction.RIGHT || p2.getDirection()!=Direction.LEFT){
                    p1.setDirection(Direction.RIGHT);
                    p2.setDirection(Direction.LEFT);
                }
            }
            //System.out.println(p1.getPlayerNumber()+"="+p1.getDirection()+" | SWORD="+p1.getSwordObject().getDirection());
            //System.out.println(p2.getPlayerNumber()+"="+p2.getDirection()+" | SWORD="+p2.getSwordObject().getDirection());

        }else if(playerObjects.size()==0){
            getPlayers();
        }
        else {
            throw new IllegalArgumentException("More than 2 players! Direction not implemented yet");
        }
    }

}
