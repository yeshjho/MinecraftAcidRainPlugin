package com.gmail.yeshjho2.testplugin;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Settings
{
    private static final HashMap<String, Object> settings = new HashMap<>();

    public static void load()
    {
        try
        {
            JSONObject dataFile = (JSONObject) new JSONParser().parse(new FileReader("data/settings.json"));

            for (Object key : dataFile.keySet())
            {
                settings.put((String) key, dataFile.get(key));
            }
        }
        catch (IOException | ParseException e)
        {
            System.out.println(e.getMessage());
        }
    }


    public static <T> T get(String key, T defaultValue)
    {
        return (T) settings.getOrDefault(key, defaultValue);
    }
}
