package norsker.cache;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache {

    private long maxCacheBytes;
    private Long maxFileSize;
    private ConcurrentHashMap<String, SoftReference<BufferedFile>> fMap;

    /**
     * Creates an unreserved memory cache with a max capacity of the given number of megabytes and
     * a file size limit of half the max size. The cache is unreserved as compressed files are of indeterminate size
     * once uncompressed into the memory. Therefore the max cache size may be breached by a significant amount with these file types,
     * plan accordingly.
     * @param maxMegabytes
     */
    public MemoryCache(long maxBytes){
        fMap = new ConcurrentHashMap<String, SoftReference<BufferedFile>>();
        maxCacheBytes = maxBytes;
        maxFileSize = maxCacheBytes / 2;
        System.out.println("Memory Cache started: max cache: " + maxCacheBytes + " max filesize: " + maxFileSize);
    }

    /**
     * Returns the File requested in a BufferedFile object. If the file is
     * @param file
     * @param aBufferStream
     * @param aBufferSize
     * @return
     */
    public BufferedFile getBufferedFile(File file, InputStream aBufferStream, int aBufferSize){
        if(fMap.containsKey(file.toString()) == false){ //file is not in cache
            BufferedFile bf = new BufferedFile(file, aBufferStream, aBufferSize, maxFileSize); //create new object
            SoftReference<BufferedFile> srbf = new SoftReference<BufferedFile>(bf);
            fMap.put(file.toString(), srbf);
            cacheMaintainance(file.toString()); // check and clear cache if full.
        }
        BufferedFile bf = fMap.get(file.toString()).get(); // SoftRefernce for BF existed
        if(bf != null){ //has not been cleared by garbage collection
            return bf;
        }else{ // SoftReference has been cleared by GC
            fMap.remove(file.toString()); //remove it from map so it can be re-entered
            return getBufferedFile(file, aBufferStream, aBufferSize); //recurse the method
        }
    }

    /**
     * While the cache is full it will clear the oldest cached file not used. It will not remove the oldest file
     * if it is the one passed in the argument, it will update it's last call time.
     */
    private synchronized void cacheMaintainance(String aCurrentlyCalled){
        BufferedFile currentCalled = fMap.get(aCurrentlyCalled).get();
        if(currentCalled != null){
            currentCalled.updateCallTime(); //refreshes the call time so its the newest
        }

        cleanNulls();

        long currentSize = 0L; //total cache use
        HashMap<String, Long> timeMap = new HashMap<String, Long>(); //record most recent and oldest calls
        for(SoftReference<BufferedFile> sf : fMap.values()){
            BufferedFile bf = null;
            if((bf = sf.get()) != null){
                long bufferedFileSize = bf.getCurrentBufferedSizeRounded();
                if(bufferedFileSize > maxFileSize){
                    fMap.remove(bf.getFileLocation());
                    System.out.println("file breached size limit for cache");
                }else{
                    currentSize += bufferedFileSize;
                    timeMap.put(bf.getFileLocation(), bf.getLastCallTime());
                }
            }
        }
        System.out.println("cache use: " + currentSize);

        while(currentSize > maxCacheBytes){
            SoftReference<BufferedFile> oldest = fMap.get(findOldestEntry());
            if(oldest.get().getFileLocation().equals(aCurrentlyCalled)){
                return; //don't remove currently being called
            }else{
                if(!oldest.get().isStillReading()){
                    oldest.clear();
                    fMap.remove(oldest);
                    System.out.println("removed oldest entry");
                }else{
                    System.out.println("Still reading file: " + oldest.get().getFileLocation());
                }
            }
        }
    }

    /**
     * Removes the entry that was called the longest time ago. May need to be called several times in succession
     * to fully clear out oldest entries that are breaching the max cache capacity.
     */
    private synchronized String findOldestEntry(){
        String oldest = null; //oldest index
        long oldestTime = 0;
        List<SoftReference<BufferedFile>> values = new ArrayList<SoftReference<BufferedFile>>(fMap.values());
        System.out.println(values.size());
        for(int i = 0; i < values.size(); i++){
            if(i == 0){
                BufferedFile bf = values.get(0).get();
                if(bf != null){
                    oldestTime = bf.getLastCallTime();
                    oldest = bf.getFileLocation();
                }else{
                    fMap.values().remove(values.get(0));
                    cleanNulls();
                    return findOldestEntry();
                }
            }else{
                BufferedFile bf = values.get(i).get();
                if(bf != null){
                    long thisTime = bf.getLastCallTime();
                    if(thisTime < oldestTime){
                        oldestTime = thisTime;
                        oldest = values.get(i).get().getFileLocation();
                    }
                }
            }
        }
        System.out.println(oldest);
        return oldest;
    }

    /**
     * Iterates through the keys to get values (SoftReference) if any are null they are removed from the map.
     */
    private synchronized void cleanNulls(){
        for(String file : fMap.keySet()){
            if((fMap.get(file)) == null){
                fMap.remove(file);
                System.out.println("Memory Cache: Cleaned null reference: " + file);
            }
        }
    }

}