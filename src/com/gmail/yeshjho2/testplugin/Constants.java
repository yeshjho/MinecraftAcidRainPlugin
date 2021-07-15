package com.gmail.yeshjho2.testplugin;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashSet;

public record Constants()
{
    //Custom Model Data
    public static final int PURIFIED_WATER_CUSTOM_MODEL_DATA = 1;
    public static final int PURIFIER_CUSTOM_MODEL_DATA = 1 << 1;
    public static final int HAZMAT_SUIT_CUSTOM_MODEL_DATA = 1 << 2;
    public static final int ANTI_ACID_CUSTOM_MODEL_DATA = 1 << 3;
    public static final int HAZMAT_SUIT_TIER_1 = 1 << 4;
    public static final int HAZMAT_SUIT_TIER_2 = 1 << 5;
    public static final int HAZMAT_SUIT_TIER_3 = 1 << 6;


    public static final HashSet<Material> ACID_PROOF_MATERIALS = new HashSet<>(Arrays.asList(
            Material.AMETHYST_CLUSTER, Material.AMETHYST_BLOCK, Material.AMETHYST_SHARD, Material.BUDDING_AMETHYST,
            Material.SMALL_AMETHYST_BUD, Material.MEDIUM_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD
    ));
}
