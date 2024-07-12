package com.shweit.serverapi;

import com.shweit.serverapi.api.v1.ApiV1Initializer;

public class Routes {

    private Routes() {}

    public static void v1Routes(WebServer app) {
        RouteBuilder rb = new RouteBuilder("/v1", app);
        ApiV1Initializer api = new ApiV1Initializer();

        // Player API
        rb.get("/players", api.getPlayerAPI()::getPlayers);
        rb.get("/players/{uuid}", api.getPlayerAPI()::getPlayer);

        // Whitelist API
        rb.get("/whitelist", api.getWhitelistAPI()::getWhitelist);
        rb.post("/whitelist", api.getWhitelistAPI()::addPlayer);
        rb.delete("/whitelist/{uuid}", api.getWhitelistAPI()::removePlayer);
    }
}
