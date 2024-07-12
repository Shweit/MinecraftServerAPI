package com.shweit.serverapi;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class MinecraftServerAPI extends JavaPlugin  {

    private WebServer app;

    @Override
    public void onEnable()
    {
        createConfig();
        setupWebserver();
    }

    @Override
    public void onDisable()
    {
        if (app != null)
        {
            app.stop();
        }
        getLogger().info("[MinecraftServerAPI] Disabled plugin!");
    }

    private void createConfig()
    {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())
        {
            saveResource("config.yml", false);
        }
    }

    private void setupWebserver()
    {
        FileConfiguration bukkitConfig = getConfig();
        app = new WebServer();
        app.start(bukkitConfig.getInt("port", 7000));
        Routes.v1Routes(app);
    }
}
