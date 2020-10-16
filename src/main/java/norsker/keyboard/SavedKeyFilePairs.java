package norsker.keyboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class SavedKeyFilePairs
{
    public static Map<String, ArrayList<Integer>> entries;


    public void saveKeyFileEntry(ArrayList<Integer> keys, String filepath)
    {
        //todo save file with keyEntries

    }

    public void loadKeyFileEntriesFromFile() throws FileNotFoundException {
        File entryFile = new File("src/main/resources/entries");
        Scanner sc = new Scanner(entryFile);
        String line;

        while ( (line = sc.nextLine())!= null)
        {
            String[] lineSplit = line.split("=");

            String filepath = lineSplit[0];
            String keysString = lineSplit[1];

            ArrayList<Integer> keys = new ArrayList<>();

            //parse keys
            for (String s:keysString.split(","))
            {
                int i = Integer.parseInt(s);
                keys.add(i);
            }
            entries.put(filepath,keys);
        }
    }
}
