package norsker.soundboard;

import com.github.cliftonlabs.json_simple.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings
{
    public boolean playAll = true;
    public final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
    public SourceDataLine defaultSpeaker;
    public SourceDataLine audioCable;
    public boolean isVACEnabled =true; // save
    public ArrayList<SoundClip> soundMappings = new ArrayList<>(); // save
    public String defaultSpeakerName;
    public String VACSpeakerName;
    private JsonObject settings;

    public static Settings instance;

    private Settings()
    {
       // loadFromJson();
        saveSettings();

        if (defaultSpeakerName==null ||defaultSpeakerName.isEmpty())
            defaultSpeakerName = "Primary Sound Driver";
        if (VACSpeakerName==null ||VACSpeakerName.isEmpty() )
            VACSpeakerName = "CABLE Input (VB-Audio Virtual Cable)";

    }

    public static Settings getInstance()
    {
        if (instance==null)
            instance=new Settings();


        return instance;
    }

    public void saveSettings()
    {

        settings = new JsonObject();
        settings.put("soundMappings", soundMappings);
        settings.put("isVacEnabled", isVACEnabled);
        if (defaultSpeakerName==null ||defaultSpeakerName.isEmpty())
            defaultSpeakerName = "Primary Sound Driver";
        if (VACSpeakerName==null ||VACSpeakerName.isEmpty())
            VACSpeakerName = "CABLE Input (VB-Audio Virtual Cable)";


        settings.put("defaultSpeakerName", defaultSpeakerName);
        settings.put("VACSpeakerName", VACSpeakerName);
        System.out.println(settings);
    }
    public void loadSettings()
    {

    }


    public ObservableList<SoundClip> fakeLoad()
    {
        ArrayList<SoundClip> arrayList= new ArrayList<>();

        arrayList.add(new SoundClip("popcorn", "src/main/resources/sounds/munching.wav"));
        arrayList.add(new SoundClip("phump", "src/main/resources/sounds/phump.wav"));


        ObservableList<SoundClip> list= FXCollections.observableList(arrayList);
        return list;
    }

}
