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

import static java.lang.Integer.min;


public class WaterDamageTask extends CustomRunnable
{
    private final double WATER_DAMAGE = Settings.get("WaterDamage", 8);
    private final int WATER_DAMAGE_COOL_TIME = Settings.get("WaterDamageCoolTime", 20);

    private static final HashSet<EntityType> DAMAGE_ENTITY_TYPES = new HashSet<>(Arrays.asList(
            EntityType.PLAYER, EntityType.BAT, EntityType.CAT, EntityType.CHICKEN, EntityType.COW, EntityType.DONKEY,
            EntityType.FOX, EntityType.HORSE, EntityType.MUSHROOM_COW, EntityType.MULE, EntityType.OCELOT,
            EntityType.PARROT, EntityType.PIG, EntityType.PIGLIN, EntityType.POLAR_BEAR, EntityType.RABBIT,
            EntityType.SHEEP, EntityType.SKELETON_HORSE, EntityType.SNOWMAN, EntityType.STRIDER, EntityType.VILLAGER,
            EntityType.WANDERING_TRADER, EntityType.BEE, EntityType.GOAT, EntityType.IRON_GOLEM, EntityType.LLAMA,
            EntityType.PANDA, EntityType.WOLF, EntityType.ZOMBIFIED_PIGLIN, EntityType.ENDERMAN
    ));

    private final JavaPlugin plugin;

    private final HashMap<LivingEntity, Integer> waterDamageCoolTime = new HashMap<>();


    public WaterDamageTask(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        for (World world : plugin.getServer().getWorlds())
        {
            if (world.getEnvironment() == World.Environment.NETHER)
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
                    waterDamageCoolTime.remove(entity);
                    continue;
                }

                if (entity.isInWater())
                {
                    final Integer remainingCoolTime = waterDamageCoolTime.getOrDefault(entity, WATER_DAMAGE_COOL_TIME);
                    waterDamageCoolTime.put(entity, remainingCoolTime - 1);

                    if (remainingCoolTime - 1 <= 0)
                    {
                        waterDamageCoolTime.put(entity, WATER_DAMAGE_COOL_TIME);
                        if (type != EntityType.PLAYER || HazmatSuit.CheckPlayerInventory((Player) entity))
                        {
                            entity.damage(WATER_DAMAGE);
                        }
                    }
                }
                else if (waterDamageCoolTime.containsKey(entity))
                {
                    Integer coolTime = waterDamageCoolTime.get(entity);
                    coolTime = min(coolTime + 1, WATER_DAMAGE_COOL_TIME);
                    waterDamageCoolTime.put(entity, coolTime);
                }
            }
        }
    }
}
