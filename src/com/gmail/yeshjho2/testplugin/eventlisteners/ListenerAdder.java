package com.gmail.yeshjho2.testplugin.eventlisteners;

import com.gmail.yeshjho2.testplugin.TestPlugin;
import com.gmail.yeshjho2.testplugin.tasks.TaskScheduler;
import com.gmail.yeshjho2.testplugin.tasks.ThirstTask;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ListenerAdder
{
    private final JavaPlugin plugin;

    public final HashMap<String, Listener> listeners = new HashMap<>();

    public ListenerAdder(TestPlugin plugin)
    {
        this.plugin = plugin;

        listeners.put("waterDrink", new WaterDrinkEventListener((ThirstTask) TaskScheduler.loops.get("Thirst")));
        listeners.put("NoWaterEntity", new WaterEntityNoSpawnEventListener());
        listeners.put("HazmatSuitBreak", new HazmatSuitBreakEventListener());
    }

    public void onEnable()
    {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (Listener listener : listeners.values())
        {
            pluginManager.registerEvents(listener, plugin);
        }
    }
}
