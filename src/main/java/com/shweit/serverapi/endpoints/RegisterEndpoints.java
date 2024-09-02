package com.shweit.serverapi.endpoints;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.WebServer;
import com.shweit.serverapi.endpoints.v1.*;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;

public final class RegisterEndpoints {
    private final WebServer server;

    private final PlayerAPI playerAPI;
    private final ServerAPI serverAPI;
    private final WhitelistAPI whitelistAPI;
    private final PluginAPI pluginAPI;
    private final WorldAPI worldAPI;
    private final BackupAPI backupAPI;
    private final MaintenanceAPI maintenanceAPI;

    public RegisterEndpoints(final WebServer webServer) {
        this.server = webServer;

        this.playerAPI = new PlayerAPI();
        this.serverAPI = new ServerAPI();
        this.whitelistAPI = new WhitelistAPI();
        this.pluginAPI = new PluginAPI();
        this.worldAPI = new WorldAPI();
        this.backupAPI = new BackupAPI();
        this.maintenanceAPI = new MaintenanceAPI();
    }

    public void registerEndpoints() {
        server.addRoute(NanoHTTPD.Method.GET, "/v1/players", playerAPI::getPlayers);
        Logger.debug("Registered GET /v1/players");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/banned-players", playerAPI::getBannedPlayers);
        Logger.debug("Registered GET /v1/banned-players");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/offline-players", playerAPI::getOfflinePlayers);
        Logger.debug("Registered GET /v1/offline-players");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}", playerAPI::getPlayer);
        Logger.debug("Registered GET /v1/players/{username}");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/stats", playerAPI::getPlayerStats);
        Logger.debug("Registered GET /v1/players/{username}/stats");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/advancements", playerAPI::getPlayerAdvancements);
        Logger.debug("Registered GET /v1/players/{username}/advancements");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/inventory", playerAPI::getPlayerInventory);
        Logger.debug("Registered GET /v1/players/{username}/inventory");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/inventory/{slot}", playerAPI::getPlayerInventorySlot);
        Logger.debug("Registered GET /v1/players/{username}/inventory/{slot}");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/kick", playerAPI::kickPlayer);
        Logger.debug("Registered POST /v1/players/{username}/kick");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/ban", playerAPI::banPlayer);
        Logger.debug("Registered POST /v1/players/{username}/ban");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/pardon", playerAPI::pardonPlayer);
        Logger.debug("Registered POST /v1/players/{username}/pardon");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/location", playerAPI::getPlayerLocation);
        Logger.debug("Registered GET /v1/players/{username}/location");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/location", playerAPI::setPlayerLocation);
        Logger.debug("Registered POST /v1/players/{username}/location");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/ping", serverAPI::ping);
        Logger.debug("Registered GET /v1/ping");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server", serverAPI::serverInfo);
        Logger.debug("Registered GET /v1/server");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/health", serverAPI::getServerHealth);
        Logger.debug("Registered GET /v1/server/heath");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/tps", serverAPI::tps);
        Logger.debug("Registered GET /v1/server/tps");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/uptime", serverAPI::uptime);
        Logger.debug("Registered GET /v1/server/uptime");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/properties", serverAPI::getServerProperties);
        Logger.debug("Registered GET /v1/server/properties");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/properties", serverAPI::updateServerProperties);
        Logger.debug("Registered POST /v1/server/properties");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/exec", serverAPI::execCommand);
        Logger.debug("Registered POST /v1/server/exec");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/reload", serverAPI::reload);
        Logger.debug("Registered POST /v1/server/reload");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/reboot", serverAPI::reboot);
        Logger.debug("Registered POST /v1/server/reboot");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/shutdown", serverAPI::shutdown);
        Logger.debug("Registered POST /v1/server/shutdown");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/broadcast", serverAPI::broadcast);
        Logger.debug("Registered POST /v1/server/broadcast");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/chat", serverAPI::getChat);
        Logger.debug("Registered GET /v1/server/chat");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/log", serverAPI::getLog);
        Logger.debug("Registered GET /v1/server/log");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/whitelist", whitelistAPI::getWhitelist);
        Logger.debug("Registered GET /v1/whitelist");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/whitelist", whitelistAPI::postWhitelist);
        Logger.debug("Registered POST /v1/whitelist");

        server.addRoute(NanoHTTPD.Method.DELETE, "/v1/whitelist", whitelistAPI::deleteWhitelist);
        Logger.debug("Registered DELETE /v1/whitelist");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/whitelist/activate", whitelistAPI::activateWhitelist);
        Logger.debug("Registered POST /v1/whitelist/activate");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/whitelist/deactivate", whitelistAPI::deactivateWhitelist);
        Logger.debug("Registered POST /v1/whitelist/deactivate");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/plugins", pluginAPI::getPlugins);
        Logger.debug("Registered GET /v1/plugins");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/plugins", pluginAPI::postPlugin);
        Logger.debug("Registered POST /v1/plugins");

        server.addRoute(NanoHTTPD.Method.DELETE, "/v1/plugins", pluginAPI::deletePlugin);
        Logger.debug("Registered DELETE /v1/plugins");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/plugins/activate", pluginAPI::activatePlugin);
        Logger.debug("Registered POST /v1/plugins/activate");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/plugins/deactivate", pluginAPI::deactivatePlugin);
        Logger.debug("Registered POST /v1/plugins/deactivate");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/plugins/{name}", pluginAPI::getPlugin);
        Logger.debug("Registered GET /v1/plugins/{name}");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/worlds", worldAPI::getWorlds);
        Logger.debug("Registered GET /v1/worlds");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/worlds", worldAPI::createWorld);
        Logger.debug("Registered POST /v1/worlds");

        server.addRoute(NanoHTTPD.Method.DELETE, "/v1/worlds", worldAPI::deleteWorld);
        Logger.debug("Registered DELETE /v1/worlds");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/worlds/{world}", worldAPI::getWorld);
        Logger.debug("Registered GET /v1/worlds/{world}");

        server.addRoute(NanoHTTPD.Method.PUT, "/v1/worlds/{world}", worldAPI::updateWorld);
        Logger.debug("Registered PUT /v1/worlds/{world}");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/backups", backupAPI::getBackups);
        Logger.debug("Registered GET /v1/backups");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/backups", backupAPI::createBackup);
        Logger.debug("Registered POST /v1/backups");

        server.addRoute(NanoHTTPD.Method.DELETE, "/v1/backups", backupAPI::deleteBackup);
        Logger.debug("Registered DELETE /v1/backups");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/backups/status", backupAPI::getStatus);
        Logger.debug("Registered GET /v1/backups/status");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/backups/{name}", backupAPI::getBackup);
        Logger.debug("Registered GET /v1/backups/{name}");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/backups/{name}/download", backupAPI::downloadBackup);
        Logger.debug("Registered GET /v1/backups/{name}/download");

        if (MinecraftServerAPI.isPluginInstalled("Maintenance")) {
            server.addRoute(NanoHTTPD.Method.GET, "/v1/maintenance", maintenanceAPI::getMaintenanceStatus);
            Logger.debug("Registered GET /v1/maintenance");

            server.addRoute(NanoHTTPD.Method.POST, "/v1/maintenance", maintenanceAPI::enableMaintenance);
            Logger.debug("Registered POST /v1/maintenance");

            server.addRoute(NanoHTTPD.Method.DELETE, "/v1/maintenance", maintenanceAPI::disableMaintenance);
            Logger.debug("Registered DELETE /v1/maintenance");

            server.addRoute(NanoHTTPD.Method.GET, "/v1/maintenance/whitelist", maintenanceAPI::getMaintenanceWhitelist);
            Logger.debug("Registered GET /v1/maintenance/whitelist");

            server.addRoute(NanoHTTPD.Method.POST, "/v1/maintenance/whitelist", maintenanceAPI::addPlayerToMaintenanceWhitelist);
            Logger.debug("Registered POST /v1/maintenance/whitelist");

            server.addRoute(NanoHTTPD.Method.DELETE, "/v1/maintenance/whitelist", maintenanceAPI::removePlayerFromMaintenanceWhitelist);
            Logger.debug("Registered DELETE /v1/maintenance/whitelist");
        }
    }
}
