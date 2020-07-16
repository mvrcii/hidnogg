package stickfight2d.enums;

import java.io.File;

public enum SoundType {

    MUSIC_GAME_WON("music_game_won.wav"),                   // All rights to "https://www.youtube.com/watch?v=LDU_Txk06tM"
    MUSIC_THEME_INGAME("music_theme_ingame.wav"),           // All rights to "https://www.youtube.com/watch?v=kWZpn0pd6Dc"
    MUSIC_MAIN_MENU("music_main_menu.wav"),                 // All rights to "https://www.youtube.com/watch?v=OpPsUcYSl38"

    SOUND_SWORD_HIT_SWORD("sound_sword_hit_sword.wav"),

    SOUND_SWORD_SWING_FAST_1("sound_sword_swing_fast_hit_body_slash_1.wav"),
    SOUND_SWORD_SWING_FAST_2("sound_sword_swing_fast_hit_body_slash_2.wav"),

    SOUND_HIT_BODY_FIST_VOCAL_1("noisecreations_NC_SFX_FistPunch-Vocal_02.wav"),
    SOUND_HIT_BODY_FIST_VOCAL_2("noisecreations_NC_SFX_FistPunch-Vocal_03.wav"),

    SOUND_HIT_BODY_1("sound_sword_swing_fast_hit_body_splash_1.wav"),
    SOUND_HIT_BODY_2("sound_sword_swing_fast_hit_body_splash_2.wav");

    private final File file;

    SoundType(String fileName){
        this.file = new File("src/sound/"+fileName);
    }

    public File getFile(){
        return file;
    }
}
