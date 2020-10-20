package norsker.soundboard;


public class SoundClip
{
    public String name;
    public String filepath;
    public int[] keyBindings;
    public String dummy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int[] getKeyBindings() {
        return keyBindings;
    }

    public void setKeyBindings(int[] keyBindings) {
        this.keyBindings = keyBindings;
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    public SoundClip(String name, String filepath)
    {
        this.filepath = filepath;

        if (!name.isEmpty() && name!=null) {
            this.name=name;
        }
        else
            this.name = filepath.split("/")[filepath.split("/").length-1];

    }


}
