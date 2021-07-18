package com.gmail.yeshjho2.testplugin.tasks;

import com.gmail.yeshjho2.testplugin.Settings;
import com.gmail.yeshjho2.testplugin.items.HazmatSuit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static java.lang.Integer.min;

public class RainTask extends CustomRunnable
{
    private final float RAIN_PROBABILITY = Settings.get("RainProbPerTick", 0.13f / 20f / 60f);
    private final int RAIN_MIN_DURATION = Settings.get("RainMinDuration", 1 * 60 * 20);
    private final int RAIN_MAX_DURATION = Settings.get("RainMaxDuration", 4 * 60 * 20);

    private final double RAIN_DAMAGE = Settings.get("RainDamage", 2);
    private final int RAIN_DAMAGE_COOL_TIME = Settings.get("RainDamageCoolTime", 20);

    private static final HashSet<EntityType> DAMAGE_ENTITY_TYPES = new HashSet<>(Arrays.asList(
            EntityType.PLAYER, EntityType.BAT, EntityType.CAT, EntityType.CHICKEN, EntityType.COW, EntityType.DONKEY,
            EntityType.FOX, EntityType.HORSE, EntityType.MUSHROOM_COW, EntityType.MULE, EntityType.OCELOT,
            EntityType.PARROT, EntityType.PIG, EntityType.PIGLIN, EntityType.POLAR_BEAR, EntityType.RABBIT,
            EntityType.SHEEP, EntityType.SKELETON_HORSE, EntityType.SNOWMAN, EntityType.STRIDER, EntityType.VILLAGER,
            EntityType.WANDERING_TRADER, EntityType.BEE, EntityType.GOAT, EntityType.IRON_GOLEM, EntityType.LLAMA,
            EntityType.PANDA, EntityType.WOLF, EntityType.ZOMBIFIED_PIGLIN, EntityType.ENDERMAN
    ));

    private final JavaPlugin plugin;

    private final HashMap<LivingEntity, Integer> rainDamageCoolTime = new HashMap<>();

    public RainTask(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        final Random random = new Random();
        if (random.nextFloat() < RAIN_PROBABILITY)
        {
            for (World world : plugin.getServer().getWorlds())
            {
                if (world.hasStorm())
                {
                    continue;
                }

                final int rainDuration = random.nextInt(RAIN_MAX_DURATION - RAIN_MIN_DURATION) + RAIN_MIN_DURATION;
                world.setStorm(true);
                world.setWeatherDuration(rainDuration);
                world.setThundering(true);
                world.setThunderDuration(rainDuration);
            }
        }

        for (World world : plugin.getServer().getWorlds())
        {
            if (world.getEnvironment() != World.Environment.NORMAL)
            {
                continue;
            }

            if (!world.hasStorm())
            {
                continue;
            }

            for (LivingEntity entity : world.getLivingEntities())
            {
                final EntityType type = entity.getType();
                if (!DAMAGE_ENTITY_TYPES.contains(type))
                {
                    continue;
                }

                if (entity.isDead())
                {
                    rainDamageCoolTime.remove(entity);
                    continue;
                }

                if (entity.getLocation().getY() + entity.getHeight() >= world.getHighestBlockYAt(entity.getLocation()) + 1)
                {
                    final Integer remainingCoolTime = rainDamageCoolTime.getOrDefault(entity, RAIN_DAMAGE_COOL_TIME);
                    rainDamageCoolTime.put(entity, remainingCoolTime - 1);

                    if (remainingCoolTime - 1 <= 0)
                    {
                        rainDamageCoolTime.put(entity, RAIN_DAMAGE_COOL_TIME);
                        if (type != EntityType.PLAYER || HazmatSuit.CheckPlayerInventory((Player) entity))
                        {
                            entity.damage(RAIN_DAMAGE);
                        }
                    }
                }
                else if (rainDamageCoolTime.containsKey(entity))
                {
                    Integer coolTime = rainDamageCoolTime.get(entity);
                    coolTime = min(coolTime + 1, RAIN_DAMAGE_COOL_TIME);
                    rainDamageCoolTime.put(entity, coolTime);
                }
            }
        }
    }
}
