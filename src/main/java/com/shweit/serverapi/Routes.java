/**
 * Routes.java
 * <p>
 * This class contains the routes for the API.
 * </p>
 */

package com.shweit.serverapi;

import com.shweit.serverapi.api.v1.ApiV1Initializer;
import com.shweit.serverapi.utils.ChatListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Routes {

    private Routes() { }

    /**
     * Initializes the routes for the API.
     *
     * @param app the web server
     */
    public static void v1Routes(
            final WebServer app,
            final ChatListener chatListener,
            final FileConfiguration bukkitConfig,
            final JavaPlugin plugin
    ) {
        RouteBuilder rb = new RouteBuilder("/v1", app);
        ApiV1Initializer api = new ApiV1Initializer(chatListener, bukkitConfig, plugin, app);

        // Player API
        rb.get("/players", api.getPlayerAPI()::getPlayers);
        rb.get("/players/{username}", api.getPlayerAPI()::getPlayer);
        rb.get("/players/{username}/stats", api.getPlayerAPI()::getPlayerStats);
        rb.get("/players/{username}/inventory", api.getPlayerAPI()::getPlayerInventory);
        rb.post("/players/{username}/kick", api.getPlayerAPI()::kickPlayer);
        rb.post("/players/{username}/ban", api.getPlayerAPI()::banPlayer);
        rb.post("/players/{username}/unban", api.getPlayerAPI()::unbanPlayer);
        rb.post("/players/{username}/teleport", api.getPlayerAPI()::teleportPlayer);

        // Whitelist API
        rb.get("/whitelist", api.getWhitelistAPI()::getWhitelist);
        rb.post("/whitelist", api.getWhitelistAPI()::addPlayer);
        rb.delete("/whitelist", api.getWhitelistAPI()::removePlayer);

        // Server API
        rb.get("/ping", api.getServerAPI()::pingServer);
        rb.get("/server", api.getServerAPI()::getServerInfo);
        rb.post("/server/exec", api.getServerAPI()::executeCommand);
        rb.get("/server/chat", api.getServerAPI()::getServerChat);
        rb.get("/server/logs", api.getServerAPI()::getServerLogs);

        // Geyser API
        rb.get("/geyser", api.getGeyserAPI()::getGeyserInfo);
        rb.get("/floodgate", api.getGeyserAPI()::getFloodgateInfo);
        rb.post("/floodgate/whitelist", api.getGeyserAPI()::addPlayerToWhitelist);
        rb.delete("/floodgate/whitelist", api.getGeyserAPI()::removePlayerFromWhitelist);

        // Maintenance API
        rb.get("/maintenance", api.getMaintenanceAPI()::getMaintenanceInfo);
        rb.post("/maintenance", api.getMaintenanceAPI()::activeMaintenance);
        rb.delete("/maintenance", api.getMaintenanceAPI()::deactivateMaintenance);

        // Backup API
        rb.get("/backups", api.getBackupAPI()::listBackups);
        rb.post("/backups", api.getBackupAPI()::createBackup);
        rb.delete("/backups", api.getBackupAPI()::deleteBackup);
    }
}
