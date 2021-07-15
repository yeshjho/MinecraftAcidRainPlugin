package com.gmail.yeshjho2.testplugin.tasks;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;


public class TaskScheduler
{
    private final JavaPlugin plugin;

    public static final HashMap<String, CustomRunnable> loops = new HashMap<>();
    public static final HashMap<String, CustomRunnable> doOnce = new HashMap<>();

    private static final HashMap<String, Pair<Long, Long>> loopArguments = new HashMap<>();

    private void putLoop(String name, CustomRunnable task, long delay, long period)
    {
        task.setPeriod(period);
        loops.put(name, task);
        loopArguments.put(name, new Pair<>(delay, period));
    }

    public TaskScheduler(JavaPlugin plugin)
    {
        this.plugin = plugin;

        putLoop("RandomTick", new RandomTickTask(plugin), 0, 20);
        putLoop("WaterDamage", new WaterDamageTask(plugin), 0, 1);
        putLoop("Thirst", new ThirstTask(plugin), 0, 1);
        putLoop("Rain", new RainTask(plugin), 0, 1);
        putLoop("ItemMelt", new ItemMeltTask(plugin), 0, 3);
        putLoop("HazmatSuitMelt", new HazmatSuitMeltTask(plugin), 0, 20);
        putLoop("BoatKill", new BoatKillTask(plugin), 0, 10);

        doOnce.put("ThirstScoreboard", new CustomRunnable()
        {
            @Override
            public void run()
            {
                final Server server = plugin.getServer();
                final ConsoleCommandSender consoleSender = server.getConsoleSender();
                server.dispatchCommand(consoleSender, "scoreboard objectives add Thirst dummy");
                server.dispatchCommand(consoleSender, "scoreboard objectives setdisplay sidebar Thirst");
            }
        });
    }

    public void onEnable()
    {
        for (CustomRunnable task : doOnce.values())
        {
            task.runTask(plugin);
        }

        for (Map.Entry<String, CustomRunnable> task : loops.entrySet())
        {
            Pair<Long, Long> arguments = loopArguments.get(task.getKey());
            task.getValue().runTaskTimer(plugin, arguments.getA(), arguments.getB());
            task.getValue().onEnable();
        }
    }

    public void onDisable()
    {
        for (CustomRunnable task: loops.values())
        {
            task.cancel();
            task.onDisable();
        }
    }
}
