package com.shweit.serverapi.endpoints;

import com.shweit.serverapi.WebServer;
import com.shweit.serverapi.endpoints.v1.PlayerAPI;
import com.shweit.serverapi.utils.Logger;

public class RegisterEndpoints {
    private final WebServer server;

    private final PlayerAPI playerAPI;

    public RegisterEndpoints(WebServer server) {
        this.server = server;
        this.playerAPI = new PlayerAPI();
    }

    public void registerEndpoints() {
        server.get("/v1/players", playerAPI::getPlayers);
        Logger.info("Registered GET /v1/players");

        server.get("/v1/banned-players", playerAPI::getBannedPlayers);
        Logger.info("Registered GET /v1/banned-players");
    }
}
