package norsker.soundboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private JSONObject settings;

    private static Settings instance;

    private Settings()
    {
        if (defaultSpeakerName==null ||defaultSpeakerName.isEmpty())
            defaultSpeakerName = "Primary Sound Driver";
        if (VACSpeakerName==null ||VACSpeakerName.isEmpty() )
            VACSpeakerName = "CABLE Input (VB-Audio Virtual Cable)";
        loadSettings();
    }

    public static Settings getInstance()
    {
        if (instance==null)
            instance = new Settings();



        return instance;
    }

    public void saveSettings()
    {

        settings = new JSONObject();
        if (defaultSpeakerName==null ||defaultSpeakerName.isEmpty())
            defaultSpeakerName = "Primary Sound Driver";
        if (VACSpeakerName==null ||VACSpeakerName.isEmpty())
            VACSpeakerName = "CABLE Input (VB-Audio Virtual Cable)";
        JSONArray soundArray = new JSONArray();
        for (SoundClip clip: soundMappings)
        {
            JSONObject obj = new JSONObject();
            obj.put("name", clip.name);
            obj.put("filepath", clip.filepath);
            obj.put("keyBindings", clip.keyBindings);
            soundArray.add(obj);
        }

        settings.put("soundMappings", soundArray);
        settings.put("isVACEnabled", isVACEnabled);
        settings.put("defaultSpeakerName", defaultSpeakerName);
        settings.put("VACSpeakerName", VACSpeakerName);


        try (FileWriter file = new FileWriter("src/main/resources/settings.json")) {

            file.write(settings.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadSettings()
    {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader("src/main/resources/settings.json"))
        {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            System.out.println( "object = "+ jsonObject);
            JSONArray array= (JSONArray) jsonObject.get("soundMappings");
            array.forEach(clip -> parseSoundMapping((JSONObject) clip));
            isVACEnabled=(boolean)     jsonObject.get("isVACEnabled");
            defaultSpeakerName=(String)jsonObject.get("defaultSpeakerName");
            VACSpeakerName=(String)    jsonObject.get("VACSpeakerName");
        }catch (Exception e){e.printStackTrace();}

    }

    private void parseSoundMapping(JSONObject object)
    {
        JSONObject clipObject = (JSONObject) object.get("soundMappings");

        SoundClip clip = new SoundClip((String)object.get("name"), (String)object.get("filepath"));
        clip.setKeyBindings((int[])object.get("keyBindings"));

        soundMappings.add(clip);
    }



    public ObservableList<SoundClip> load()
    {
        return FXCollections.observableList(soundMappings);
    }

}
