package com.shweit.serverapi.endpoints;

import com.shweit.serverapi.WebServer;
import com.shweit.serverapi.endpoints.v1.PlayerAPI;
import com.shweit.serverapi.endpoints.v1.ServerAPI;
import com.shweit.serverapi.endpoints.v1.WhitelistAPI;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;

public final class RegisterEndpoints {
    private final WebServer server;

    private final PlayerAPI playerAPI;
    private final ServerAPI serverAPI;
    private final WhitelistAPI whitelistAPI;

    public RegisterEndpoints(final WebServer webServer) {
        this.server = webServer;
        this.playerAPI = new PlayerAPI();
        this.serverAPI = new ServerAPI();
        this.whitelistAPI = new WhitelistAPI();
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
    }
}
