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
        standardDataLineInfo = new DataLine.Info(SourceDataLine.class, Settings.getInstance().format, BUFFERSIZE);
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
        SourceDataLine defaultOutputLine = Settings.getInstance().defaultSpeaker;
        SourceDataLine VACOutputLine = Settings.getInstance().audioCable;
        try
        {
            //default speaker


            Settings.getInstance().defaultSpeaker = (SourceDataLine) defaultMixerOutput.getLine(standardDataLineInfo);
            Settings.getInstance().defaultSpeaker.open(Settings.getInstance().format, INTERNAL_BUFFER_SIZE);
            Settings.getInstance().defaultSpeaker.start();

            //VAC
            if (Settings.getInstance().isVACEnabled && VACMixerOutput!= null)
            {
                System.out.println("setting up vac");
                Settings.getInstance().audioCable = (SourceDataLine) VACMixerOutput.getLine(standardDataLineInfo);
                Settings.getInstance().audioCable.open(Settings.getInstance().format, INTERNAL_BUFFER_SIZE);
                Settings.getInstance().audioCable.start();
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

                if (!format.equals(Settings.getInstance().format))
                    inputStream = AudioSystem.getAudioInputStream(Settings.getInstance().format, inputStream);
            if (inputStream != null)
            {
                byte[] buffer = new byte[2048];
                int bytesRead = 0;

                while (playing && Settings.getInstance().playAll)
                {
                    bytesRead = inputStream.read(buffer, 0, 2048);
                    if (bytesRead>0)
                    {
                        Settings.getInstance().defaultSpeaker.write(buffer, 0, bytesRead);
                        if (Settings.getInstance().isVACEnabled)
                        {
                            Settings.getInstance().audioCable.write(buffer, 0, bytesRead);
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


            Settings.getInstance().defaultSpeaker.close();
            if (Settings.getInstance().audioCable!= null)
                Settings.getInstance().audioCable.close();
            } catch (Exception e){e.printStackTrace();}

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
