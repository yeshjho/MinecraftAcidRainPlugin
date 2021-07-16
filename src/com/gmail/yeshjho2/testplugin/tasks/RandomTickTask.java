package com.gmail.yeshjho2.testplugin.tasks;

import com.gmail.yeshjho2.testplugin.tasks.randomticktasks.BlockMeltTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;

public class RandomTickTask extends CustomRunnable
{
    private final JavaPlugin plugin;

    private final HashMap<String, Function<Block, Void>> randomTickTasks = new HashMap<>();

    public RandomTickTask(JavaPlugin plugin)
    {
        this.plugin = plugin;

        randomTickTasks.put("StoneMelt", new BlockMeltTask());
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

            for (Chunk chunk : world.getLoadedChunks())
            {
                for (int i = 0; i < Optional.ofNullable(world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED)).orElse(3) * getPeriod(); ++i)
                {
                    final Block block = chunk.getBlock(random.nextInt(16), random.nextInt(256), random.nextInt(16));
                    for (Function<Block, Void> function : randomTickTasks.values())
                    {
                        function.apply(block);
                    }
                }
            }
        }
    }
}
