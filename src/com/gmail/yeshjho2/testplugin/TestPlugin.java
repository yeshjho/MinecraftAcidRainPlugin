package com.gmail.yeshjho2.testplugin;

import com.gmail.yeshjho2.testplugin.eventlisteners.ListenerAdder;
import com.gmail.yeshjho2.testplugin.recipes.RecipeAdder;
import com.gmail.yeshjho2.testplugin.tasks.TaskScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin
{
    public final TaskScheduler taskScheduler = new TaskScheduler(this);
    public final ListenerAdder listenerAdder = new ListenerAdder(this);
    public final RecipeAdder recipeAdder = new RecipeAdder(this);

    @Override
    public void onEnable()
    {
        taskScheduler.onEnable();
        listenerAdder.onEnable();
    }

    @Override
    public void onDisable()
    {
        taskScheduler.onDisable();
    }
}
