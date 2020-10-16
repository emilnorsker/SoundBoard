package norsker.soundboard;


import norsker.Utility;

import javax.sound.sampled.*;
import java.io.File;

public class SoundBoard
{
    private static final int INTERNAL_BUFFER_SIZE = 8192;
    public static final int BUFFERSIZE = 2048;
    public final DataLine.Info standardDataLineInfo;
    Mixer defaultMixerOutput;
    Mixer VACMixerOutput;

    private static SoundBoard instance;

    private SoundBoard()
    {
        standardDataLineInfo = new DataLine.Info(SourceDataLine.class, Settings.format, BUFFERSIZE);
    }

    public static SoundBoard getInstance()
    {
        if (instance==null)
        {
            instance = new SoundBoard();
            return instance;
        }
        else return instance;
    }



    public void setMixerOutput(String name, boolean secondary)
    {
        String[] mixerNames = Utility.getMixerNames(standardDataLineInfo);

        for (String mixerName: mixerNames)
        {
            for(Mixer.Info mixerInfo : AudioSystem.getMixerInfo()){
                if(mixerName.equals(mixerInfo.getName()))
                {
                    if (!secondary)
                     defaultMixerOutput = AudioSystem.getMixer(mixerInfo);
                    else
                        VACMixerOutput = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }

    }

    /**
     * Play clip to the selected outputs. it will always play to default speaker.
     *
     * @param filepath path to the file you want to play
     * @throws LineUnavailableException
     */
    public void play(String filepath) throws LineUnavailableException
    {

        //setup datalines for speaker and vac
        SourceDataLine defaultOutputLine = Settings.defaultSpeaker;
        SourceDataLine VACOutputLine = Settings.audioCable;
        try
        {
            //default speaker


            Settings.defaultSpeaker = (SourceDataLine) defaultMixerOutput.getLine(standardDataLineInfo);
            Settings.defaultSpeaker.open(Settings.format, INTERNAL_BUFFER_SIZE);
            Settings.defaultSpeaker.start();

            //VAC
            if (Settings.isVACEnable && VACMixerOutput!= null)
            {
                System.out.println("setting up vac");
                Settings.audioCable = (SourceDataLine) VACMixerOutput.getLine(standardDataLineInfo);
                Settings.audioCable.open(Settings.format, INTERNAL_BUFFER_SIZE);
                Settings.audioCable.start();
            }
        } catch (Exception e) { e.printStackTrace();}

        File file = new File(filepath);
        SoundPlayer sound = new SoundPlayer(file);
        sound.start();
    }


    /**
     * this Thread worker handles the playing to default speaker, and if present the virtual audio cable.
     */
    public static class SoundPlayer extends Thread
    {

        File file;



        public SoundPlayer(File file)
        {
            this.file = file;
        }

        @Override
        public void run()
        {
            this.playSound(this.file);
        }

        public void playSound(File file)
        {
            AudioInputStream inputStream = null;
            AudioFormat format = null;
            boolean playing = true;

            try
            {
                inputStream = AudioSystem.getAudioInputStream(file);
                format = inputStream.getFormat();

                if (!format.equals(Settings.format))
                    inputStream = AudioSystem.getAudioInputStream(Settings.format, inputStream);
            if (inputStream != null)
            {
                byte[] buffer = new byte[2048];
                int bytesRead = 0;

                while (playing && Settings.playAll)
                {
                    bytesRead = inputStream.read(buffer, 0, 2048);
                    if (bytesRead>0)
                    {
                        Settings.defaultSpeaker.write(buffer, 0, bytesRead);
                        if (Settings.isVACEnable)
                        {
                            Settings.audioCable.write(buffer, 0, bytesRead);
                        }
                    }
                    if (bytesRead < 2048)
                    {
                        playing = false;
                    }
                }
            }

            if (inputStream!=null)
                inputStream.close();


            Settings.defaultSpeaker.close();
            if (Settings.audioCable!= null)
                Settings.audioCable.close();
            } catch (Exception e){e.printStackTrace();}

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
