package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;

public class TagParser {
    Model model;

    public final static int DEFAULT_COLOR = 0;

    public static void parse(
        String filename,
        InputStream inputStream, 
        HashMap<String, ArrayList<String>> keys,
        HashMap<String, ArrayList<int[]>> colors,
        HashMap<String, ArrayList<Integer>> priorities
    ) {
        parse(filename, inputStream, keys, colors, priorities, null);
    }

    public static void parse(
        String filename,
        InputStream inputStream, 
        HashMap<String, ArrayList<String>> keys,
        HashMap<String, ArrayList<int[]>> colors,
        HashMap<String, ArrayList<Integer>> priorities,
        HashMap<String, Integer> defaultSpeeds
    ) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            //Current line in .txt file
            String line;
            String key = "";
            
            //While there are more lines in the file
            while ((line = reader.readLine()) != null && !line.contains("//")) {
                String[] sections = line.split(" ");
                int length = sections.length;
                if(length == 0) continue;

                //If there is only a key, add this key to new entries in HashMaps
                if(length <= 3) {
                    key = sections[0];
                    keys.put(key, new ArrayList<>());
                    colors.put(key, new ArrayList<>());
                    priorities.put(key, new ArrayList<>());
                }
                
                //Otherwise, add found values of key to HashMaps
                else {
                    String value = sections[4];

                    if(filename.contains("relations.txt")) value = value.replace("_", " ");
                    int red = Integer.parseInt(sections[5]);
                    int green = Integer.parseInt(sections[6]);
                    int blue = Integer.parseInt(sections[7]);
                    int[] color = {red, green, blue};
                    int priority = Integer.parseInt(sections[8]);
                    keys.get(key).add(value);
                    colors.get(key).add(color);
                    priorities.get(key).add(priority);

                    if(defaultSpeeds != null && length >= 10) {
                        defaultSpeeds.put(value, Integer.parseInt(sections[9]));
                    }
                }
            }
            reader.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
