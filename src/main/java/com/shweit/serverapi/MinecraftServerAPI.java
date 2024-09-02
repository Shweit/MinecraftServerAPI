/**
 * MinecraftServerAPI.java
 * <p>
 *     This class is the main class of the plugin.
 *     It is responsible for starting and stopping the web server.
 *     It also creates the configuration file if it does not exist.
 * </p>
 */

package com.shweit.serverapi;

import com.shweit.serverapi.commands.RegisterCommands;
import com.shweit.serverapi.endpoints.RegisterEndpoints;
import com.shweit.serverapi.utils.CheckForUpdate;
import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.server.ServerStop;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.shweit.serverapi.listeners.PlayerLoginListener;

import java.io.File;

public class MinecraftServerAPI extends JavaPlugin  {

    private static final int DEFAULT_PORT = 7000;
    private WebServer server;
    public static FileConfiguration config;
    public static String pluginName = "MinecraftServerAPI";
    private static MinecraftServerAPI instance;

    private static boolean blockNewConnections = false;
    private static String blockNewConnectionsMessage;

    public static boolean isPluginInstalled(final String string) {
        return Bukkit.getPluginManager().isPluginEnabled(string);
    }

    @Override
    public final void onEnable() {
        // Check for Updates
        getServer().getPluginManager().registerEvents(new CheckForUpdate(), this);

        registerEvents();
        createConfig();

        config = getConfig();
        instance = this;

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

        new RegisterEndpoints(server).registerEndpoints();

        new RegisterWebHooks().registerWebHooks();

        new RegisterCommands(this).register();

        try {
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Logger.info("Web server started on port " + port);
        } catch (Exception e) {
            Logger.error("Failed to start web server: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public final void onDisable() {
        new ServerStop().register();

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

    public static MinecraftServerAPI getInstance() {
        return instance;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
    }

    public static void setBlockNewConnections(final boolean block, final String message) {
        blockNewConnections = block;
        if (block) {
            blockNewConnectionsMessage = message;
        }
    }

    public static boolean isBlockNewConnections() {
        return blockNewConnections;
    }

    public static String getBlockNewConnectionsMessage() {
        return blockNewConnectionsMessage;
    }
}
