package com.gmail.yeshjho2.testplugin.eventlisteners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;
import java.util.HashSet;

public class WaterEntityNoSpawnEventListener implements Listener
{
    private static final HashSet<EntityType> NO_SPAWN_TYPES = new HashSet<>(Arrays.asList(
            EntityType.AXOLOTL, EntityType.COD, EntityType.GLOW_SQUID, EntityType.PUFFERFISH, EntityType.SALMON,
            EntityType.SQUID, EntityType.TROPICAL_FISH, EntityType.DOLPHIN
    ));

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        if (NO_SPAWN_TYPES.contains(event.getEntityType()))
        {
            event.setCancelled(true);
        }
    }
}
