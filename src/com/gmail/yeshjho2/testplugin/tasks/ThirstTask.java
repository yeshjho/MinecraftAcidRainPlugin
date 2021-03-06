package com.gmail.yeshjho2.testplugin.tasks;

import com.gmail.yeshjho2.testplugin.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class ThirstTask extends CustomRunnable
{
    private final int MAX_THIRST = Settings.get("MaxThirst", 20);
    private final int FIRST_MAX_THIRST = Settings.get("FirstMaxThirst", 50);
    private final int THIRST_COOL_TIME = Settings.get("ThirstCoolTime", 600);
    private final int WATER_THIRST_RESTORE_AMOUNT = Settings.get("WaterThirstRestoreAmount", 4);
    private final int SLOW_THIRST_THRESHOLD = Settings.get("SlowThirstThreshold", 5);
    private final int SLOW_AMPLIFIER = Settings.get("ThirstSlowAmplifier", 2);
    private final int NAUSEA_THIRST_THRESHOLD = Settings.get("NauseaThirstThreshold", 3);
    private final int NAUSEA_AMPLIFIER = Settings.get("ThirstNauseaAmplifier", 255);

    private final int NON_OVER_WORLD_MULTIPLIER = Settings.get("NonOverWorldThirstMultiplier", 3);

    private final JavaPlugin plugin;

    private final HashMap<Integer, Integer> thirst = new HashMap<>();
    private final HashMap<Player, Integer> thirstCoolTime = new HashMap<>();
    private final HashMap<Integer, Boolean> isFirstThirst = new HashMap<>();

    public ThirstTask(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    private void putThirst(Player player, Integer v)
    {
        if (v > (isFirstThirst.getOrDefault(player.hashCode(), true) ? FIRST_MAX_THIRST : MAX_THIRST))
        {
            v = MAX_THIRST;
        }
        thirst.put(player.hashCode(), v);
        Objects.requireNonNull(player.getScoreboard().getObjective("Thirst")).getScore(player.getName()).setScore(v);
    }

    private void checkForThresholds(Player player)
    {
        final int hashCode = player.hashCode();
        final int playerThirst = thirst.getOrDefault(hashCode, isFirstThirst.getOrDefault(hashCode, true) ? FIRST_MAX_THIRST : MAX_THIRST);

        if (playerThirst <= MAX_THIRST)
        {
            isFirstThirst.put(player.hashCode(), false);
        }

        if (playerThirst <= SLOW_THIRST_THRESHOLD)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, SLOW_AMPLIFIER, true));
        }
        else
        {
            player.removePotionEffect(PotionEffectType.SLOW);
        }

        if (playerThirst <= NAUSEA_THIRST_THRESHOLD)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, NAUSEA_AMPLIFIER, true));
        }
        else
        {
            player.removePotionEffect(PotionEffectType.CONFUSION);
        }

        if (playerThirst <= 0)
        {
            player.setHealth(0);
            putThirst(player, MAX_THIRST);
            thirstCoolTime.put(player, THIRST_COOL_TIME);
        }
    }

    public void onPlayerDrinkWater(Player player)
    {
        final int hashCode = player.hashCode();
        if (isFirstThirst.getOrDefault(hashCode, true))
        {
            return;
        }

        putThirst(player, thirst.getOrDefault(hashCode, MAX_THIRST) + WATER_THIRST_RESTORE_AMOUNT);
        thirstCoolTime.put(player, THIRST_COOL_TIME);
        checkForThresholds(player);
    }

    @Override
    public void run()
    {
        for (Player player : plugin.getServer().getOnlinePlayers())
        {
            if (player.isDead())
            {
                continue;
            }

            final Integer coolTime = thirstCoolTime.getOrDefault(player, THIRST_COOL_TIME);
            thirstCoolTime.put(player, coolTime - (player.getWorld().getEnvironment() != World.Environment.NORMAL ? NON_OVER_WORLD_MULTIPLIER : 1));

            if (coolTime - 1 <= 0)
            {
                final int hashCode = player.hashCode();
                final int playerThirst = thirst.getOrDefault(hashCode, isFirstThirst.getOrDefault(hashCode, true) ? FIRST_MAX_THIRST : MAX_THIRST);
                putThirst(player, playerThirst - 1);
                thirstCoolTime.put(player, THIRST_COOL_TIME);
            }

            checkForThresholds(player);
        }
    }

    @Override
    public void onEnable()
    {
        try
        {
            JSONObject thirstFile = (JSONObject) new JSONParser().parse(new FileReader("data/thirst.json"));

            JSONObject thirstData = (JSONObject) thirstFile.get("thirst");
            for (Object key : thirstData.keySet())
            {
                thirst.put(Integer.valueOf((String) key), (int) (long) thirstData.get(key));
            }

            JSONObject isFirstData = (JSONObject) thirstFile.get("isFirst");
            for (Object key : isFirstData.keySet())
            {
                isFirstThirst.put(Integer.valueOf((String) key), (boolean) isFirstData.get(key));
            }
        }
        catch (IOException | ParseException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onDisable()
    {
        File thirstFile = new File("data/thirst.json");
        try
        {
            thirstFile.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            return;
        }

        try
        {
            JsonWriter writer = new JsonWriter(new FileWriter(thirstFile, false));
            Gson builder = new GsonBuilder().create();

            writer.beginObject();

            writer.name("thirst");
            builder.toJson(thirst, HashMap.class, writer);

            writer.name("isFirst");
            builder.toJson(isFirstThirst, HashMap.class, writer);

            writer.endObject();
            writer.flush();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
