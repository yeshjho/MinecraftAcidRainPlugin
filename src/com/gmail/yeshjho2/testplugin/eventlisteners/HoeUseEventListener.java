package com.gmail.yeshjho2.testplugin.eventlisteners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.gmail.yeshjho2.testplugin.Constants.TURTLE_HOE_CUSTOM_MODEL_DATA;

public class HoeUseEventListener implements Listener
{
    @EventHandler
    public void onPlayerUseHoe(PlayerInteractEvent event)
    {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
        {
            return;
        }

        final ItemStack item = event.getItem();
        if (item == null)
        {
            return;
        }

        if (item.getType() == Material.WOODEN_HOE)
        {
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            final int customModelData = itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0;
            if ((customModelData & TURTLE_HOE_CUSTOM_MODEL_DATA) == 0)
            {
                event.setCancelled(true);
            }
        }
    }
}
