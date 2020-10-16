package norsker;

import javax.sound.sampled.*;
import java.util.ArrayList;


public class Utility
{
    public static final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100f, 16, 2, 4, 44100f, false);
    public static final int BUFFERSIZE = 2048;
    public static SourceDataLine[] sourceDataLines;
    public static Mixer[] outputs;


    public static String[] getMixerNames(DataLine.Info lineInfo)
    {
        ArrayList<String> mixerNames = new ArrayList<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

        for (Mixer.Info info: mixerInfos)
        {
            Mixer mixer = AudioSystem.getMixer(info);
            if (mixer.isLineSupported(lineInfo))
                mixerNames.add(info.getName());
        }
        String[] result = new String[mixerNames.size()];
        return mixerNames.toArray(result);
    }


}