package com.gmail.yeshjho2.testplugin.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BlockMeltTask extends CustomRunnable
{
    private static final HashSet<Material> ACID_BLOCKS = new HashSet<>(Arrays.asList(
            Material.WATER, Material.KELP_PLANT, Material.SEAGRASS, Material.TALL_SEAGRASS
    ));

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

    private final JavaPlugin plugin;

    private final HashMap<Integer, Long> worldSimulatedSecond = new HashMap<>();
    private final HashMap<Integer, Long> worldRainSecond = new HashMap<>();
    private final HashMap<Integer, Long> chunkSimulatedSecond = new HashMap<>();
    private final HashMap<Integer, Long> chunkRainSecond = new HashMap<>();

    public BlockMeltTask(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        final Random random = new Random();
        for (World world : plugin.getServer().getWorlds())
        {
            if (world.getEnvironment() != World.Environment.NORMAL)
            {
                continue;
            }

            final boolean isRaining = world.hasStorm();

            final int worldHashCode = world.hashCode();
            worldSimulatedSecond.put(worldHashCode, worldSimulatedSecond.getOrDefault(worldHashCode, 0L) + 1);
            final long thisWorldSimulatedSecond = worldSimulatedSecond.getOrDefault(worldHashCode, 0L);
            if (isRaining)
            {
                worldRainSecond.put(worldHashCode, worldRainSecond.getOrDefault(worldHashCode, 0L) + 1);
            }
            final long thisWorldRainSecond = worldRainSecond.getOrDefault(worldHashCode, 0L);

            for (Chunk chunk : world.getLoadedChunks())
            {
                final int chunkHashCode = chunk.hashCode();
                final long thisChunkSimulatedSecond = chunkSimulatedSecond.getOrDefault(chunkHashCode, 0L);
                final long thisChunkRainSecond = chunkRainSecond.getOrDefault(chunkHashCode, 0L);

                final int defaultBlockCountPerSec = Optional.ofNullable(world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED)).orElse(3) * 20;
                final long blocksToSelect = defaultBlockCountPerSec * (thisWorldSimulatedSecond - thisChunkSimulatedSecond);
                final long blocksToSelectWhileRaining = defaultBlockCountPerSec * (thisWorldRainSecond - thisChunkRainSecond);

                for (long i = 0; i < blocksToSelect; ++i)
                {
                    final Block block = chunk.getBlock(random.nextInt(16), random.nextInt(256), random.nextInt(16));
                    tryMeltBlock(block, i < blocksToSelectWhileRaining);
                }

                chunkSimulatedSecond.put(chunkHashCode, thisWorldSimulatedSecond);
                chunkRainSecond.put(chunkHashCode, thisWorldRainSecond);
            }
        }
    }

    public void tryMeltBlock(Block block, boolean isRaining)
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
            if (ACID_BLOCKS.contains(upperBlock.getType()) ||
				(upperBlockData instanceof Waterlogged && ((Waterlogged) upperBlockData).isWaterlogged()) ||
				(blockData instanceof Waterlogged && ((Waterlogged) blockData).isWaterlogged()))
            {
                block.setType(Material.WATER);
                final Levelled data = (Levelled) Material.WATER.createBlockData();
                data.setLevel(8);
                block.setBlockData(data);
            }
			else if (isRaining && block.getLocation().getY() == world.getHighestBlockYAt(block.getLocation()))
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
    }

    @Override
    public void onEnable()
    {
        try
        {
            JSONObject dataFile = (JSONObject) new JSONParser().parse(new FileReader("data/blockMelt.json"));

            JSONObject worldSimulatedSecondData = (JSONObject) dataFile.get("worldSimulatedSecond");
            for (Object key : worldSimulatedSecondData.keySet())
            {
                worldSimulatedSecond.put(Integer.valueOf((String) key), (long) worldSimulatedSecondData.get(key));
            }

            JSONObject worldRainSecondData = (JSONObject) dataFile.get("worldRainSecond");
            for (Object key : worldRainSecondData.keySet())
            {
                worldRainSecond.put(Integer.valueOf((String) key), (long) worldRainSecondData.get(key));
            }

            JSONObject chunkSimulatedSecondData = (JSONObject) dataFile.get("chunkSimulatedSecond");
            for (Object key : chunkSimulatedSecondData.keySet())
            {
                chunkSimulatedSecond.put(Integer.valueOf((String) key), (long) chunkSimulatedSecondData.get(key));
            }

            JSONObject chunkRainSecondData = (JSONObject) dataFile.get("chunkRainSecond");
            for (Object key : chunkRainSecondData.keySet())
            {
                chunkRainSecond.put(Integer.valueOf((String) key), (long) chunkRainSecondData.get(key));
            }
        }
        catch (IOException | ParseException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onDisable()
    {
        File dataFile = new File("data/blockMelt.json");
        try
        {
            dataFile.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            return;
        }

        try
        {
            JsonWriter writer = new JsonWriter(new FileWriter(dataFile, false));
            Gson builder = new GsonBuilder().create();

            writer.beginObject();

            writer.name("worldSimulatedSecond");
            builder.toJson(worldSimulatedSecond, HashMap.class, writer);

            writer.name("worldRainSecond");
            builder.toJson(worldRainSecond, HashMap.class, writer);

            writer.name("chunkSimulatedSecond");
            builder.toJson(chunkSimulatedSecond, HashMap.class, writer);

            writer.name("chunkRainSecond");
            builder.toJson(chunkRainSecond, HashMap.class, writer);

            writer.endObject();
            writer.flush();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
