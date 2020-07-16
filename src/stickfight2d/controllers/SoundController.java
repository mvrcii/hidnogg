package stickfight2d.controllers;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import stickfight2d.enums.SoundType;
import stickfight2d.misc.Debugger;

import java.util.HashMap;

import static stickfight2d.enums.SoundType.*;

public class SoundController implements Controller {

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
        musicData.put(MUSIC_THEME_INGAME, TinySound.loadMusic(MUSIC_THEME_INGAME.getFile()));
        musicData.put(MUSIC_MAIN_MENU, TinySound.loadMusic(MUSIC_MAIN_MENU.getFile()));
        musicData.put(MUSIC_GAME_WON, TinySound.loadMusic(MUSIC_GAME_WON.getFile()));

        // Sound
        soundData.put(SOUND_SWORD_HIT_SWORD, TinySound.loadSound(SOUND_SWORD_HIT_SWORD.getFile()));
        soundData.put(SOUND_HIT_BODY_1, TinySound.loadSound(SOUND_HIT_BODY_1.getFile()));
        soundData.put(SOUND_HIT_BODY_2, TinySound.loadSound(SOUND_HIT_BODY_2.getFile()));
        soundData.put(SOUND_HIT_BODY_FIST_VOCAL_1, TinySound.loadSound(SOUND_HIT_BODY_FIST_VOCAL_1.getFile()));
        soundData.put(SOUND_HIT_BODY_FIST_VOCAL_2, TinySound.loadSound(SOUND_HIT_BODY_FIST_VOCAL_2.getFile()));
        soundData.put(SOUND_SWORD_SWING_FAST_1, TinySound.loadSound(SOUND_SWORD_SWING_FAST_1.getFile()));
        soundData.put(SOUND_SWORD_SWING_FAST_2, TinySound.loadSound(SOUND_SWORD_SWING_FAST_2.getFile()));
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
