package com.gmail.yeshjho2.testplugin.tasks;

import com.gmail.yeshjho2.testplugin.items.HazmatSuit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import static com.gmail.yeshjho2.testplugin.Constants.HAZMAT_SUIT_CUSTOM_MODEL_DATA;

public class HazmatSuitMeltTask extends CustomRunnable
{
    private final JavaPlugin plugin;

    public HazmatSuitMeltTask(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    private void tryRemove(Item item, ItemStack itemStack, ItemMeta itemMeta, int customModelData)
    {
        final Damageable damageable = (Damageable) itemMeta;
        final int newDamage = damageable.getDamage() + HazmatSuit.DAMAGE_PER_TIER.get(customModelData ^ HAZMAT_SUIT_CUSTOM_MODEL_DATA);
        if (newDamage < itemStack.getType().getMaxDurability())
        {
            damageable.setDamage(newDamage);
            itemStack.setItemMeta(itemMeta);
            return;
        }

        item.remove();
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
                final ItemStack itemStack = item.getItemStack();
                final ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                final int customModelData = itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0;

                if (item.isInWater())
                {
                    tryRemove(item, itemStack, itemMeta, customModelData);
                    continue;
                }

                if ((customModelData & HAZMAT_SUIT_CUSTOM_MODEL_DATA) == 0)
                {
                    continue;
                }

                if (world.hasStorm())
                {
                    if (item.getLocation().getY() < world.getHighestBlockYAt(item.getLocation()) + 1)
                    {
                        continue;
                    }
                }

                tryRemove(item, itemStack, itemMeta, customModelData);
            }
        }
    }
}
