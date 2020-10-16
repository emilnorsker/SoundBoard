package norsker.soundboard;

import javafx.beans.binding.ListBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings
{
    public static boolean playAll = true;
    public static final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
    public static SourceDataLine defaultSpeaker;
    public static SourceDataLine audioCable;
    public static boolean isVACEnable =true;



    public static HashMap<String, String> FileKeyEntries;//map of <filename, keyInputs>




    /**
     *
     * todo load setting from file (json maybe)
     */
    public void loadSettings()
    {

    }


    public static ObservableList<SoundClip> fakeLoad()
    {
        ArrayList<SoundClip> arrayList= new ArrayList<>();

        arrayList.add(new SoundClip("popcorn", "src/main/resources/sounds/munching.wav"));
        arrayList.add(new SoundClip("phump", "src/main/resources/sounds/phump.wav"));


        ObservableList<SoundClip> list= FXCollections.observableList(arrayList);
        return list;
    }

}
