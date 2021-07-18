package com.gmail.yeshjho2.testplugin.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.plugin.java.JavaPlugin;


public class BoatKillTask extends CustomRunnable
{
    private final JavaPlugin plugin;

    public BoatKillTask(JavaPlugin plugin)
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

            for (Boat boat : world.getEntitiesByClass(Boat.class))
            {
                if (boat.isInWater())
                {
                    boat.remove();
                    continue;
                }

                final Location location = boat.getLocation();
                if (world.getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ()).getType() == Material.WATER)
                {
                    boat.remove();
                }
            }
        }
    }
}
