package stickfight2d.controllers;


import stickfight2d.GameLoop;
import stickfight2d.enums.DirectionType;
import stickfight2d.misc.Debugger;
import stickfight2d.world.GameObject;
import stickfight2d.world.PlayerObject;

import java.util.ArrayList;

public class DirectionController implements Controller {

    private ArrayList<GameObject> gameObjects;
    private ArrayList<PlayerObject> playerObjects = new ArrayList<>();

    private boolean manDirConP1 = false, manDirConP2 = false;

    private static DirectionController instance;

    public static DirectionController getInstance() {
        if (instance == null) {
            Debugger.log("Direction Controller instantiated");
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
                if(p1.getDirectionType()!= DirectionType.LEFT || p2.getDirectionType()!= DirectionType.RIGHT){
                    if(!manDirConP1)
                        p1.setDirectionType(DirectionType.LEFT);
                    if(!manDirConP2)
                        p2.setDirectionType(DirectionType.RIGHT);
                }
            }else{
                if(p1.getDirectionType()!= DirectionType.RIGHT || p2.getDirectionType()!= DirectionType.LEFT){
                    if(!manDirConP1)
                        p1.setDirectionType(DirectionType.RIGHT);
                    if(!manDirConP2)
                        p2.setDirectionType(DirectionType.LEFT);
                }
            }

        }else if(playerObjects.size()==0){
            getPlayers();
        }
        else {
            throw new IllegalArgumentException("More than 2 players! Direction not implemented yet");
        }
    }


    private void setPlayerDirection(PlayerObject playerObject, DirectionType directionType){
        playerObject.setDirectionType(directionType);
    }

    // Activates the manual control of a specific player direction and therefor disables the automatic control
    public void setManualControl(PlayerObject playerObject, boolean bool) {
        switch (playerObject.getPlayerNumber()){
            case PLAYER_ONE -> manDirConP1 = bool;
            case PLAYER_TWO -> manDirConP2 = bool;
        }
    }

}
