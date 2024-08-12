/**
 * MinecraftServerAPI.java
 * <p>
 *     This class is the main class of the plugin.
 *     It is responsible for starting and stopping the web server.
 *     It also creates the configuration file if it does not exist.
 * </p>
 */

package com.shweit.serverapi;

import com.shweit.serverapi.utils.ChatListener;
import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.webhooks.Webhooks;
import com.shweit.serverapi.webhooks.server.ServerStopWebhook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class MinecraftServerAPI extends JavaPlugin  {

    /**
     * The web server instance.
     */
    private WebServer app;

    /**
     * The default port for the web server.
     */
    private static final int DEFAULT_PORT = 7000;

    private ChatListener chatListener;

    @Override
    public final void onEnable() {
        this.chatListener = new ChatListener(this);
        createConfig();
        setupWebserver();
    }

    @Override
    public final void onDisable() {
        FileConfiguration config = getConfig();
        new ServerStopWebhook(this, config).sendServerStopWebhook();

        if (app != null) {
            app.stop();
        }
        Logger.info("Disabled plugin!");
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())  {
            saveResource("config.yml", false);
        }
    }

    private void setupWebserver() {
        FileConfiguration bukkitConfig = getConfig();
        app = new WebServer(bukkitConfig);
        app.start(bukkitConfig.getInt("port", DEFAULT_PORT));
        Routes.v1Routes(app, this.chatListener, bukkitConfig, this);
        Webhooks.registerWebhooks(bukkitConfig, this);
    }
}
