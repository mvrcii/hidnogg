package stickfight2d.controllers;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import stickfight2d.enums.SoundType;
import stickfight2d.misc.Debugger;

import java.util.HashMap;

import static stickfight2d.enums.SoundType.MAIN_MENU_THEME_01;
import static stickfight2d.enums.SoundType.THEME_01;

public class SoundController extends Controller {

    //TODO: add sound list with different sounds
    //TODO: add music
    private final HashMap<SoundType, Music> musicData = new HashMap<>();
    private final HashMap<SoundType, Sound> soundData = new HashMap<>();

    private static SoundController instance;

    public static SoundController getInstance() {
        if (instance == null) {
            Debugger.log("Sound Controller instantiated");
            instance = new SoundController();
        }
        return instance;
    }

    private SoundController(){
        // Music
        musicData.put(THEME_01, TinySound.loadMusic(THEME_01.getFile()));
        musicData.put(MAIN_MENU_THEME_01, TinySound.loadMusic(MAIN_MENU_THEME_01.getFile()));

        // Sound
    }

    @Override
    public void update(long diffMillis) {

    }

    public Music getMusic(SoundType soundType){
        return musicData.get(soundType);
    }

    public Sound getSound(SoundType soundType){
        return soundData.get(soundType);
    }

}
