package com.gmail.yeshjho2.testplugin.eventlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.gmail.yeshjho2.testplugin.Constants.HAZMAT_SUIT_CUSTOM_MODEL_DATA;

public class HazmatSuitBreakEventListener implements Listener
{
    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event)
    {
        final ItemStack item = event.getItem();
        final ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        if (itemMeta.hasCustomModelData() && (itemMeta.getCustomModelData() & HAZMAT_SUIT_CUSTOM_MODEL_DATA) != 0)
        {
            event.setDamage(10000);
        }
    }
}
