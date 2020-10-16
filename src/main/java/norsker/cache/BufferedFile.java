package norsker.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


/**
 * Based on https://sourceforge.net/p/expsoundboard/code by  expenosa
 */

public class BufferedFile extends TreadedByteBuffer{

    private final long fFileSize;
    private HashMap<String, Long> fReadingTracker;
    private File fFile;
    private InputStream fBufferedInput = null;
    private long fLastCallMs;
    private boolean fStillReading;
    protected Long fMaxFileSize;
    private boolean fReadFromDisk = false; //Breaches max file size so it must be read from disk

    /**
     * Class that represents a file cached into Memory. Caching happens on the fly rather than initialisation.
     * Each thread wanting to read the bytes must pass a unique ID (use UUID) that is kept on a register to track reading progress.
     * IF the file size breaches the Max file size parameter the file is instead not cached but read directly from the drive.
     * @param file
     * @param aBufferedInputStream
     * @param aBufferSize
     * @param aMaxFileSize
     */
    public BufferedFile(File file, InputStream aBufferedInputStream, int aBufferSize, Long aMaxFileSize){
        super(aBufferSize);
        fFile = file;
        fFileSize = file.length();
        fReadingTracker = new HashMap<String, Long>();
        fBufferedInput = aBufferedInputStream;
        fLastCallMs = System.currentTimeMillis();
        fStillReading = true;
        fMaxFileSize = aMaxFileSize;
        if(fFile.length() > fMaxFileSize){ // exceeds file limit
            fReadFromDisk = true;
        }
    }

    /**
     * Retrieves the next array of bytes corresponding with the reading tracker issued to the UUID string.
     * The specified array size corresponds with the Buffer Size passed on creation.
     * This method also forwards the reading if the file has still not been fully read.
     * If the file is being read from the drive (because of it's size) it will be read straight from the input stream passed here.
     * @param uuid
     * @param aDiskStream
     * @return
     */
    public byte[] readNextBytes(String uuid, InputStream aDiskStream){
        updateCallTime();

        InputStream input = fBufferedInput;
        if(fReadFromDisk){
            input = aDiskStream;
        }

        if(fReadingTracker.containsKey(uuid) == false){ //create new entry
            fReadingTracker.put(uuid, 0L);
        }
        long totalRead = fReadingTracker.get(uuid);

        int bytesRead = 0;
        byte[] buffer = new byte[fBufferSize];

        try {
            bytesRead = input.read(buffer); //reading file
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(bytesRead <= 0){ //end of file
            fStillReading = false;
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            if(!fReadFromDisk){
                concat(buffer, bytesRead); //adds data to the cache
            }
        }

        fReadingTracker.put(uuid, totalRead + bytesRead); //updates reading tracker

        if(fReadFromDisk){
            if(bytesRead != fBufferSize){
                if(bytesRead == -1){
                    return new byte[0];
                }else{
                    byte[] returnBytes = new byte[bytesRead];
                    for(int i = 0; i < bytesRead; i++){
                        returnBytes[i] = buffer[i];
                    }
                    return returnBytes;
                }
            }else{
                return buffer;
            }
        }else{ // read from cache
            byte[] returnBytes = getNext(uuid);// always gets data from cache
            return returnBytes;
        }
    }

    /**
     * Returns the .length in bytes of the file givfen at creation.
     * @return
     */
    public long getFileSize(){
        return fFileSize;
    }

    /**
     * Returns in milliseconds the last time readNextBytes() was called (via. updateCallTime()).
     * @return
     */
    public long getLastCallTime(){
        return fLastCallMs;
    }

    /**
     * Sets the last call time to the time this method is called.
     */
    public void updateCallTime(){
        fLastCallMs = System.currentTimeMillis();
    }

    /**
     * returns true if the file is still being read from the disk.
     * @return
     */
    public synchronized boolean isStillReading(){
        if(fLastCallMs + 200 > System.currentTimeMillis()){
            return fStillReading;
        }else{ // reading was stopped by user. close stream.
            fStillReading = false;
            try {
                fBufferedInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fStillReading;
    }

    /**
     * Returns true if the file is being read straight from disk instead of cache.
     * @return
     */
    public boolean isReadFromDisk(){
        return fReadFromDisk;
    }

    /**
     * return true if the inputstream given at creation matches the one passed.
     * @param aIs
     * @return
     */
    public boolean doInputStreamsMatch(InputStream aIs){
        if(aIs == fBufferedInput){
            return true;
        }
        return false;
    }

    public String getFileLocation(){
        return fFile.toString();
    }

    /**
     * Returns a copy of the original file
     * @return
     */
    public File getFile(){
        return new File(fFile.getAbsolutePath());
    }
}
