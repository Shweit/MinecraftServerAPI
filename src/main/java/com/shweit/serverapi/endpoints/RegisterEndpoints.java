package com.shweit.serverapi.endpoints;

import com.shweit.serverapi.WebServer;
import com.shweit.serverapi.endpoints.v1.PlayerAPI;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;

public class RegisterEndpoints {
    private final WebServer server;

    private final PlayerAPI playerAPI;

    public RegisterEndpoints(WebServer server) {
        this.server = server;
        this.playerAPI = new PlayerAPI();
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
    }
}
