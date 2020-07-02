package stickfight2d.controllers;

import javafx.geometry.Point2D;
import stickfight2d.GameLoop;
import stickfight2d.Main;
import stickfight2d.misc.Debugger;

public class CameraController extends Controller{

    private static CameraController instance;

    //camera position
    private double camX,camY, desiredOffset;

    //players start positions
    Point2D player1StartPosition = null;
    Point2D player2StartPosition = null;

    public static CameraController getInstance() {
        if (instance == null) {
            Debugger.log("Camera Controller instantiated");
            instance = new CameraController();
        }
        return instance;
    }

    private CameraController() {
        camX = 0;
        camY = 0;
    }

    @Override
    public void update(long diffMillis) {

        //get players start positions when available (should be first update)
        //NOTE: this has to be in update method because currentlevel is not instantiated when cameracontroller is created
        if (player1StartPosition == null || player2StartPosition == null) {
            if (GameLoop.currentLevel != null) {
                initStartValues();
            }

            //also return here because positions to follow are not set yet
            return;
        }

        //calculate differences to start positions
        Point2D diffPlayer1 = new Point2D(GameLoop.currentLevel.getPlayer1().getX() - player1StartPosition.getX(), GameLoop.currentLevel.getPlayer1().getY() - player1StartPosition.getY());
        Point2D diffPlayer2 = new Point2D(GameLoop.currentLevel.getPlayer2().getX() - player2StartPosition.getX(), GameLoop.currentLevel.getPlayer2().getY() - player2StartPosition.getY());

        //TODO: add lerp to smooth camera movement
        //calculate center point for camera
        camX = (diffPlayer1.getX() + diffPlayer2.getX()) / 2 + desiredOffset;
        camY = (diffPlayer1.getY() + diffPlayer2.getY()) / 2;

    }

    //this method has to be called in first update loop after startup or in new level
    private void initStartValues() {
        player1StartPosition = new Point2D(GameLoop.currentLevel.getPlayer1().getX(), GameLoop.currentLevel.getPlayer1().getY());
        player2StartPosition = new Point2D(GameLoop.currentLevel.getPlayer2().getX(), GameLoop.currentLevel.getPlayer2().getY());

        //DONT LOOK AT ME IM UGLY
        desiredOffset = (player1StartPosition.getX() - (player1StartPosition.getX() + (Main.canvas.getWidth() - player2StartPosition.getX())) / 2) / 2;
    }

    public Point2D convertWorldToScreen(int x, int y) {
        double newX = x - camX;
        double newY = y - camY; //+ to move camera up if y value is higher
        return new Point2D(newX,newY);
    }

    private double lerp(double a, double b, double f) {
        return (a * (1.0 - f)) + (b * f);
        //return a + f * (b - a);
    }

}
