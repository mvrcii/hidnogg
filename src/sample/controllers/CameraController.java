package sample.controllers;

import javafx.geometry.Point2D;
import sample.Main;

public class CameraController extends Controller{

    private static CameraController instance;

    //camera position
    private int camX,camY;

    public static CameraController getInstance() {
        if (instance == null) {
            System.out.println("Camera Controller instantiated");
            instance = new CameraController();
        }
        return instance;
    }

    private CameraController() {
        //TODO: get references to player objects
        camX = 0;
        camY = 0;
    }

    @Override
    public void update(long diffMillis) {
        //TODO: calculate camera position based on player positions
        //TODO: implement offset that is calculated based on camera position for side scrolling
        //     -> TODO: draw methods for other objects have to take offset into account
    }

    public Point2D convertWorldToScreen(int x, int y) {
        double newX = x - camX;
        double newY = y + camY; //+ to move camera up if y value is higher
        return new Point2D(newX,newY);
    }

    public Point2D convertScreenToWorld(Point2D point) {
        //TODO: implement
        return new Point2D(0,0);
    }

}
