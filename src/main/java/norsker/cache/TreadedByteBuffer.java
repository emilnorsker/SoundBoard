package norsker.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Based on https://sourceforge.net/p/expsoundboard/code by  expenosa
 */

public abstract class TreadedByteBuffer{

    private ArrayList<byte[]> fBytes;
    private HashMap<String, Integer> fIndexTracker;
    protected int fBufferSize;


    public TreadedByteBuffer(int aBufferSize){
        fBufferSize = aBufferSize;
        fIndexTracker = new HashMap<String, Integer>();
        fBytes = new ArrayList<byte[]>();
    }

    public void concat(byte[] aByteArray, int aBytesRead){
        if(aBytesRead == fBufferSize){
            fBytes.add(aByteArray);
            //System.out.println("byte array list size = " + fBytes.size() + " " + getCurrentBufferedSizeRounded());
        }else{
            byte[] bytes = Arrays.copyOfRange(aByteArray, 0, aBytesRead);
            fBytes.add(bytes);
            //System.out.println("reached end of file with concat");
        }
    }

    protected byte[] getNext(String uuid){
        if(fIndexTracker.containsKey(uuid) == false){
            fIndexTracker.put(uuid, 0);
        }

        int index = fIndexTracker.get(uuid);
        //System.out.println("getNext() getting index" + index);
        byte[] returnArray = null;
        if(index < fBytes.size()){
            returnArray = fBytes.get(index);
        }
        fIndexTracker.put(uuid, index+1);

        if(returnArray == null || returnArray.length != fBufferSize) // reached end
            fIndexTracker.remove(uuid);

        if(returnArray == null){
            return new byte[0];
        }
        return returnArray;
    }

    public long getCurrentBufferedSizeRounded(){
        int arraySize = fBytes.size();
        long bytesBuffered = (long) arraySize * (long) fBufferSize;
        return bytesBuffered;
    }

    public int getBufferSize(){
        return fBufferSize;
    }

    public void clear(){
        fBytes.clear();
    }

}