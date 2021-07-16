package com.gmail.yeshjho2.testplugin.eventlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.meta.ItemMeta;

import static com.gmail.yeshjho2.testplugin.Constants.HAZMAT_SUIT_CUSTOM_MODEL_DATA;
import static com.gmail.yeshjho2.testplugin.Constants.TURTLE_HOE_CUSTOM_MODEL_DATA;

public class ItemMendEventListener implements Listener
{
    @EventHandler
    public void onItemMend(PlayerItemMendEvent event)
    {
        final ItemMeta itemMeta = event.getItem().getItemMeta();
        assert itemMeta != null;
        final int customModelData = itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0;
        if ((customModelData & HAZMAT_SUIT_CUSTOM_MODEL_DATA) != 0 || (customModelData & TURTLE_HOE_CUSTOM_MODEL_DATA) != 0)
        {
            event.setCancelled(true);
        }
    }
}
