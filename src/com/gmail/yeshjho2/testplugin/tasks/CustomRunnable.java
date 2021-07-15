package com.gmail.yeshjho2.testplugin.tasks;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class CustomRunnable extends BukkitRunnable
{
    private long period = 1;
    public long getPeriod() { return period; }
    public void setPeriod(long p) { period = p; }

    public void onEnable()
    {}

    public void onDisable()
    {}
}
