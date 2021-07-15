package com.gmail.yeshjho2.testplugin.items;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.gmail.yeshjho2.testplugin.Constants.*;

public class HazmatSuit
{
    public static final HashMap<Integer, Integer> DAMAGE_PER_TIER = new HashMap<>() {{
        put(HAZMAT_SUIT_TIER_1, 1);
        put(HAZMAT_SUIT_TIER_2, 1);
        put(HAZMAT_SUIT_TIER_3, 0);
    }};

    private static final ArrayList<EquipmentSlot> SLOT_NUMS = new ArrayList<>(Arrays.asList(
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    ));

    // Returns should be damaged or not
    public static boolean CheckPlayerInventory(Player player)
    {
        final PlayerInventory inventory = player.getInventory();
        final ArrayList<ItemStack> armors = new ArrayList<>(Arrays.asList(
                inventory.getHelmet(), inventory.getChestplate(), inventory.getLeggings(), inventory.getBoots()
        ));

        boolean shouldDamage = false;
        for (int i = 0; i < 4; i++)
        {
            final ItemStack armor = armors.get(i);
            if (armor == null)
            {
                shouldDamage = true;
                continue;
            }

            final ItemMeta itemMeta = armor.getItemMeta();
            final int customModelData = itemMeta != null ? itemMeta.getCustomModelData() : 0;
            if ((customModelData & HAZMAT_SUIT_CUSTOM_MODEL_DATA) == 0)
            {
                shouldDamage = true;
                continue;
            }

            final Damageable damageable = (Damageable) itemMeta;
            final int newDamage = damageable.getDamage() + DAMAGE_PER_TIER.get(customModelData ^ HAZMAT_SUIT_CUSTOM_MODEL_DATA);
            if (newDamage >= armor.getType().getMaxDurability())
            {
                inventory.setItem(SLOT_NUMS.get(i), new ItemStack(Material.AIR));
                player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
            }
            else
            {
                damageable.setDamage(newDamage);
                armor.setItemMeta(itemMeta);
            }
        }

        return shouldDamage;
    }
}
