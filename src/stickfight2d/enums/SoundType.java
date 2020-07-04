package stickfight2d.enums;

import java.io.File;

public enum SoundType {

    THEME_01("theme_01.wav");

    private final File file;

    SoundType(String fileName){
        this.file = new File("src/sound/"+fileName);
    }

    public File getFile(){
        return file;
    }
}
