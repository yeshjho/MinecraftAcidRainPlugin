package com.gmail.yeshjho2.testplugin.tasks;

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
import java.util.Map;
import java.util.Objects;

public class ThirstTask extends CustomRunnable
{
    private static final int MAX_THIRST = 20;
    private static final int THIRST_COOL_TIME = 600;
    private static final int WATER_THIRST_RESTORE_AMOUNT = 4;

    private static final int NON_OVER_WORLD_MULTIPLIER = 3;

    private final JavaPlugin plugin;

    private final HashMap<Integer, Integer> thirst = new HashMap<>();
    private final HashMap<Player, Integer> thirstCoolTime = new HashMap<>();

    public ThirstTask(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    private void putThirst(Player player, Integer v)
    {
        if (v > MAX_THIRST)
        {
            v = MAX_THIRST;
        }
        thirst.put(player.hashCode(), v);
        Objects.requireNonNull(player.getScoreboard().getObjective("Thirst")).getScore(player.getName()).setScore(v);
    }

    private void checkForThresholds(Player player)
    {
        final Integer playerThirst = thirst.getOrDefault(player.hashCode(), MAX_THIRST);

        if (playerThirst <= 5)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2, true));
        }
        else
        {
            player.removePotionEffect(PotionEffectType.SLOW);
        }

        if (playerThirst <= 3)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 255, true));
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
        putThirst(player, thirst.getOrDefault(player.hashCode(), MAX_THIRST) + WATER_THIRST_RESTORE_AMOUNT);
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
                final Integer playerThirst = thirst.getOrDefault(player.hashCode(), MAX_THIRST);
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

            for (Object key : thirstFile.keySet())
            {
                thirst.put(Integer.valueOf((String) key), (int) (long) thirstFile.get(key));
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

            writer.beginObject();
            for (Map.Entry<Integer, Integer> pair : thirst.entrySet())
            {
                writer.name(pair.getKey().toString()).value(pair.getValue());
            }
            writer.endObject();
            writer.flush();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
