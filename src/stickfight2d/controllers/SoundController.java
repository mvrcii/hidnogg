package stickfight2d.controllers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import stickfight2d.misc.Debugger;
import java.io.File;
import java.net.URI;

public class SoundController extends Controller {

    //TODO: add sound list with different sounds
    //TODO: add music


    private static SoundController instance;

    public static SoundController getInstance() {
        if (instance == null) {
            Debugger.log("Sound Controller instantiated");
            instance = new SoundController();
        }
        return instance;
    }

    @Override
    public void update(long diffMillis) {

    }

    public void playTestSound() {

        try {

            File file = new File("src/sound/knife.mp3");
            String path = file.getAbsolutePath();
            URI uri = file.toURI();
            Media media = new Media(uri.toString());
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.1);
            player.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
