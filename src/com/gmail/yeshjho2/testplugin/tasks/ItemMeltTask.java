package com.gmail.yeshjho2.testplugin.tasks;

import com.gmail.yeshjho2.testplugin.items.HazmatSuit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import static com.gmail.yeshjho2.testplugin.Constants.*;
import static com.gmail.yeshjho2.testplugin.Constants.HAZMAT_SUIT_CUSTOM_MODEL_DATA;

public class ItemMeltTask extends CustomRunnable
{
    private final JavaPlugin plugin;

    public ItemMeltTask(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        for (World world : plugin.getServer().getWorlds())
        {
            if (world.getEnvironment() != World.Environment.NORMAL)
            {
                continue;
            }

            for (Item item : world.getEntitiesByClass(Item.class))
            {
                if (item.isInWater())
                {
                    item.remove();
                    continue;
                }

                if (world.hasStorm())
                {
                    if (item.getLocation().getY() < world.getHighestBlockYAt(item.getLocation()) + 1)
                    {
                        continue;
                    }
                }

                final ItemStack itemStack = item.getItemStack();
                final Material type = itemStack.getType();
                if (ACID_PROOF_MATERIALS.contains(type))
                {
                    continue;
                }

                final ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                final int customModelData = itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0;
                if ((customModelData & ANTI_ACID_CUSTOM_MODEL_DATA) != 0)
                {
                    continue;
                }

                if ((customModelData & HAZMAT_SUIT_CUSTOM_MODEL_DATA) != 0)
                {
                    continue;
                }

                item.remove();
            }
        }
    }
}
