package com.gmail.yeshjho2.testplugin.eventlisteners;

import com.gmail.yeshjho2.testplugin.tasks.ThirstTask;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import static com.gmail.yeshjho2.testplugin.Constants.PURIFIED_WATER_CUSTOM_MODEL_DATA;

public class WaterDrinkEventListener implements Listener
{
    private final ThirstTask thirstTask;

    public WaterDrinkEventListener(ThirstTask thirstTask)
    {
        this.thirstTask = thirstTask;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event)
    {
        ItemStack item = event.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        if (itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == PURIFIED_WATER_CUSTOM_MODEL_DATA)
        {
            thirstTask.onPlayerDrinkWater(event.getPlayer());
        }
        else if (item.getType() == Material.POTION && ((PotionMeta) itemMeta).getBasePotionData().getType() == PotionType.WATER)
        {
            event.getPlayer().setHealth(0);
        }
    }
}
