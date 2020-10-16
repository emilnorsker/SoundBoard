package norsker.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class CachedFileStream{

    BufferedFile fBufferedFile;
    String fUuid;

    /**
     * A class that interacts with buffered files as if they were a stream. On creation it creates a
     * new UUID that is used by the reading tracker in a BufferedFile.
     * @param aBufferedFile
     */
    public CachedFileStream(BufferedFile aBufferedFile) {
        fUuid = UUID.randomUUID().toString();
        fBufferedFile = aBufferedFile;
    }

    /**
     * Fills the buffer with the next bytes as requested. If the bufferedFile is being read from the disk instead,
     * because it is too big for the cache it will be read straight from the stream given here. An exception will be
     * thrown if the byte buffer given is too small.
     * Return how many bytes were written into the buffer. 0 is return once the end is reached.
     * @param b
     * @param aDiskStream
     * @return
     * @throws IOException
     */
    public int read(byte[] b, InputStream aDiskStream) throws IOException, ArrayIndexOutOfBoundsException {
        byte[] buffer = fBufferedFile.readNextBytes(fUuid, aDiskStream);
        //System.out.println("reading: " + b.length);
        for(int i = 0; i < buffer.length; i++){
            b[i] = buffer[i];
        }
        return buffer.length;
    }

    /**
     * Closes the passed inputstream if it doesn't match the bufferedFile's one. Also closes it if they do match and has finished reading.
     * @param aIs
     * @throws IOException
     */
    public void close(InputStream aIs) throws IOException{
        if(!fBufferedFile.doInputStreamsMatch(aIs)){
            aIs.close();
            System.out.println("CachedAudioStream: Stream closed " + fBufferedFile.getFile().toString());
        }else if(fBufferedFile.isReadFromDisk()){
            aIs.close();
            System.out.println("CachedAudioStream: Disk Stream closed " + fBufferedFile.getFile().toString());		}
        //TODO close only if not being read and doesn't match the stream in bufferedFile
    }
}