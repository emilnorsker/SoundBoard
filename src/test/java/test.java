import norsker.soundboard.SoundBoard;
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
            Thread.sleep(5000);
            //sb.play("src/main/resources/sounds/Whistle.mp3");
            //Thread.sleep(5000);
        } catch (LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }



    }
}
