package stickfight2d.enums;

import java.io.File;

public enum SoundType {

    GAME_WON_THEME("game_won.wav"),                 // All rights to "https://www.youtube.com/watch?v=LDU_Txk06tM"
    INGAME_THEME_01("theme_01.wav"),                // All rights to "https://www.youtube.com/watch?v=kWZpn0pd6Dc"
    MAIN_MENU_THEME_01("main_menu_theme.wav"),      // All rights to "https://www.youtube.com/watch?v=OpPsUcYSl38"

    HIT_SWORD_SWORD("sword_hit_another_sword.wav");

    private final File file;

    SoundType(String fileName){
        this.file = new File("src/sound/"+fileName);
    }

    public File getFile(){
        return file;
    }
}
