package com.shweit.serverapi.endpoints;

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

    public RegisterEndpoints(final WebServer webServer) {
        this.server = webServer;
        this.playerAPI = new PlayerAPI();
        this.serverAPI = new ServerAPI();
        this.whitelistAPI = new WhitelistAPI();
        this.pluginAPI = new PluginAPI();
        this.worldAPI = new WorldAPI();
    }

    public void registerEndpoints() {
        server.addRoute(NanoHTTPD.Method.GET, "/v1/players", playerAPI::getPlayers);
        Logger.info("Registered GET /v1/players");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/banned-players", playerAPI::getBannedPlayers);
        Logger.info("Registered GET /v1/banned-players");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/offline-players", playerAPI::getOfflinePlayers);
        Logger.info("Registered GET /v1/offline-players");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}", playerAPI::getPlayer);
        Logger.info("Registered GET /v1/players/{username}");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/stats", playerAPI::getPlayerStats);
        Logger.info("Registered GET /v1/players/{username}/stats");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/advancements", playerAPI::getPlayerAdvancements);
        Logger.info("Registered GET /v1/players/{username}/advancements");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/inventory", playerAPI::getPlayerInventory);
        Logger.info("Registered GET /v1/players/{username}/inventory");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/inventory/{slot}", playerAPI::getPlayerInventorySlot);
        Logger.info("Registered GET /v1/players/{username}/inventory/{slot}");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/kick", playerAPI::kickPlayer);
        Logger.info("Registered POST /v1/players/{username}/kick");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/ban", playerAPI::banPlayer);
        Logger.info("Registered POST /v1/players/{username}/ban");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/pardon", playerAPI::pardonPlayer);
        Logger.info("Registered POST /v1/players/{username}/pardon");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/players/{username}/location", playerAPI::getPlayerLocation);
        Logger.info("Registered GET /v1/players/{username}/location");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/players/{username}/location", playerAPI::setPlayerLocation);
        Logger.info("Registered POST /v1/players/{username}/location");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/ping", serverAPI::ping);
        Logger.info("Registered GET /v1/ping");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server", serverAPI::serverInfo);
        Logger.info("Registered GET /v1/server");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/health", serverAPI::getServerHealth);
        Logger.info("Registered GET /v1/server/heath");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/tps", serverAPI::tps);
        Logger.info("Registered GET /v1/server/tps");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/uptime", serverAPI::uptime);
        Logger.info("Registered GET /v1/server/uptime");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/properties", serverAPI::getServerProperties);
        Logger.info("Registered GET /v1/server/properties");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/properties", serverAPI::updateServerProperties);
        Logger.info("Registered POST /v1/server/properties");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/exec", serverAPI::execCommand);
        Logger.info("Registered POST /v1/server/exec");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/reboot", serverAPI::reload);
        Logger.info("Registered POST /v1/server/reload");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/reboot", serverAPI::reboot);
        Logger.info("Registered POST /v1/server/reboot");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/shutdown", serverAPI::shutdown);
        Logger.info("Registered POST /v1/server/shutdown");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/server/broadcast", serverAPI::broadcast);
        Logger.info("Registered POST /v1/server/broadcast");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/chat", serverAPI::getChat);
        Logger.info("Registered GET /v1/server/chat");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/server/log", serverAPI::getLog);
        Logger.info("Registered GET /v1/server/log");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/whitelist", whitelistAPI::getWhitelist);
        Logger.info("Registered GET /v1/whitelist");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/whitelist", whitelistAPI::postWhitelist);
        Logger.info("Registered POST /v1/whitelist");

        server.addRoute(NanoHTTPD.Method.DELETE, "/v1/whitelist", whitelistAPI::deleteWhitelist);
        Logger.info("Registered DELETE /v1/whitelist");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/whitelist/activate", whitelistAPI::activateWhitelist);
        Logger.info("Registered POST /v1/whitelist/activate");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/whitelist/deactivate", whitelistAPI::deactivateWhitelist);
        Logger.info("Registered POST /v1/whitelist/deactivate");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/plugins", pluginAPI::getPlugins);
        Logger.info("Registered GET /v1/plugins");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/plugins", pluginAPI::postPlugin);
        Logger.info("Registered POST /v1/plugins");

        server.addRoute(NanoHTTPD.Method.DELETE, "/v1/plugins", pluginAPI::deletePlugin);
        Logger.info("Registered DELETE /v1/plugins");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/plugins/activate", pluginAPI::activatePlugin);
        Logger.info("Registered POST /v1/plugins/activate");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/plugins/deactivate", pluginAPI::deactivatePlugin);
        Logger.info("Registered POST /v1/plugins/deactivate");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/plugins/{name}", pluginAPI::getPlugin);
        Logger.info("Registered GET /v1/plugins/{name}");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/worlds", worldAPI::getWorlds);
        Logger.info("Registered GET /v1/worlds");

        server.addRoute(NanoHTTPD.Method.POST, "/v1/worlds", worldAPI::createWorld);
        Logger.info("Registered POST /v1/worlds");

        server.addRoute(NanoHTTPD.Method.DELETE, "/v1/worlds", worldAPI::deleteWorld);
        Logger.info("Registered DELETE /v1/worlds");

        server.addRoute(NanoHTTPD.Method.GET, "/v1/worlds/{worldName}", worldAPI::getWorld);
        Logger.info("Registered GET /v1/worlds/{worldName}");
    }
}
