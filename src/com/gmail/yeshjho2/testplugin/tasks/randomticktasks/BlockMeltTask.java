package com.gmail.yeshjho2.testplugin.tasks.randomticktasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

public class BlockMeltTask implements Function<Block, Void>
{
    private static final HashSet<Material> NOT_MELTING_BLOCKS = new HashSet<>(Arrays.asList(
            Material.AIR, Material.CAVE_AIR, Material.BEDROCK, Material.OBSIDIAN, Material.TINTED_GLASS,
            Material.AMETHYST_BLOCK, Material.AMETHYST_CLUSTER, Material.BUDDING_AMETHYST, Material.LARGE_AMETHYST_BUD,
            Material.MEDIUM_AMETHYST_BUD, Material.SMALL_AMETHYST_BUD, Material.PRISMARINE,
            Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_BRICK_STAIRS, Material.PRISMARINE_BRICKS,
            Material.PRISMARINE_SLAB, Material.PRISMARINE_STAIRS, Material.PRISMARINE_WALL,
            Material.DARK_PRISMARINE, Material.DARK_PRISMARINE_SLAB, Material.DARK_PRISMARINE_STAIRS,
            Material.SEA_LANTERN, Material.SPONGE, Material.WET_SPONGE, Material.CRYING_OBSIDIAN, Material.WATER,
            Material.LAVA
    ));
    private static final ArrayList<Tag<Material>> NOT_MELTING_BLOCK_TAGS = new ArrayList<>(Arrays.asList(

    ));

    private static final HashSet<Material> BLOCKS_WATER_INSTEAD_OF_AIR_WHEN_MELTED = new HashSet<>(Arrays.asList(
            Material.ICE, Material.PACKED_ICE, Material.BLUE_ICE, Material.FROSTED_ICE, Material.WATER_CAULDRON
    ));

    @Override
    public Void apply(Block block)
    {
        final Material blockType = block.getType();

        boolean isNotMeltingBlock = NOT_MELTING_BLOCKS.contains(block.getType());
//        if (!isNotMeltingBlock)
//        {
//            for (Tag<Material> tag : NOT_MELTING_BLOCK_TAGS)
//            {
//                if (tag.isTagged(blockType))
//                {
//                    isNotMeltingBlock = true;
//                    break;
//                }
//            }
//        }

        if (!isNotMeltingBlock)
        {
            final Location loc = block.getLocation();
            final World world = block.getWorld();
            final int x = loc.getBlockX();
            final int y = loc.getBlockY();
            final int z = loc.getBlockZ();

            final Block upperBlock = world.getBlockAt(x, y + 1, z);
            final BlockData upperBlockData = upperBlock.getState().getBlockData();
			final BlockData blockData = block.getState().getBlockData();
            if ((upperBlock.getType() == Material.WATER) ||
				  (upperBlockData instanceof Waterlogged && ((Waterlogged) upperBlockData).isWaterlogged()) ||
				  (blockData instanceof Waterlogged && ((Waterlogged) blockData).isWaterlogged()))
            {
                block.setType(Material.WATER);
            }
			else if (world.hasStorm() && block.getLocation().getY() == world.getHighestBlockYAt(block.getLocation()))
			{
				if (BLOCKS_WATER_INSTEAD_OF_AIR_WHEN_MELTED.contains(blockType))
				{
					block.setType(Material.WATER);
				}
				else
				{
					block.setType(Material.AIR);
				}
			}
        }

        return null;
    }
}
