package com.shweit.serverapi.api.v1;

import com.shweit.serverapi.endpoints.PlayerAPI;
import com.shweit.serverapi.endpoints.WhitelistAPI;

public class ApiV1Initializer {
    private final PlayerAPI playerAPI;
    private final WhitelistAPI whitelistAPI;

    public ApiV1Initializer() {
        this.playerAPI = new PlayerAPI();
        this.whitelistAPI = new WhitelistAPI();
    }

    public PlayerAPI getPlayerAPI() {
        return playerAPI;
    }

    public WhitelistAPI getWhitelistAPI() {
        return whitelistAPI;
    }
}
