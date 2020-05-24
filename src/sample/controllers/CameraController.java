package sample.controllers;

public class CameraController extends Controller{

    private static CameraController instance;

    //camera position
    private int x,y;

    public static CameraController getInstance() {
        if (instance == null) {
            System.out.println("Camera Controller instantiated");
            instance = new CameraController();
        }
        return instance;
    }

    private CameraController() {
        //TODO: get references to player objects
        x = 0;
        y = 0;
    }

    @Override
    public void update(long diffMillis) {
        //TODO: calculate camera position based on player positions
        //TODO: implement offset that is calculated based on camera position for side scrolling
        //     -> TODO: draw methods for other objects have to take offset into account
    }

}
