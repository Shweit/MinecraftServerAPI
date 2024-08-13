/**
 * MinecraftServerAPI.java
 * <p>
 *     This class is the main class of the plugin.
 *     It is responsible for starting and stopping the web server.
 *     It also creates the configuration file if it does not exist.
 * </p>
 */

package com.shweit.serverapi;

import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class MinecraftServerAPI extends JavaPlugin  {

    private static final int DEFAULT_PORT = 7000;
    private NanoHTTPD server;

    @Override
    public final void onEnable() {
        createConfig();

        boolean authEnabled = getConfig().getBoolean("authentication.enabled", true);
        String authKey = getConfig().getString("authentication.key", "CHANGE_ME");

        if (!authEnabled) {
            Logger.warning("Authentication is disabled. This is not recommended.");
        } else if ("CHANGE_ME".equals(authKey)) {
            Logger.error("Please change the authKey in the config.yml file.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        int port = getConfig().getInt("port", DEFAULT_PORT);
        server = new WebServer(port, authEnabled, authKey);

        try {
            server.start();
            Logger.info("Web server started on port " + port);
        } catch (Exception e) {
            Logger.error("Failed to start web server: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public final void onDisable() {
        if (server != null) {
            server.stop();
            Logger.info("Web server stopped.");
        }
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())  {
            saveResource("config.yml", false);
        }
    }
}
