import norsker.soundboard.Settings;
import norsker.soundboard.SoundBoard;
import norsker.soundboard.SoundClip;
import org.junit.Test;

import javax.sound.sampled.LineUnavailableException;

public class test
{
    @Test
    public void test()
    {
        SoundBoard sb = SoundBoard.getInstance();

        sb.setMixerOutput("Primary Sound Driver", false);
        sb.setMixerOutput("CABLE Input (VB-Audio Virtual Cable)", true);

        try {
            sb.play("src/main/resources/sounds/munching.wav");
         } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void Json()
    {
        Settings settings = Settings.getInstance();
        settings.soundMappings.add(new SoundClip("hello", "sounds/phump.wav"));
        settings.soundMappings.add(new SoundClip("bye", "sounds/phump.wav"));
        settings.saveSettings();
    }
}
