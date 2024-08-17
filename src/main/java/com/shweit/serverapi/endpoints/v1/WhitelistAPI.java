package com.shweit.serverapi.endpoints.v1;


import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Helper;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WhitelistAPI {
    public NanoHTTPD.Response getWhitelist(Map<String, String> ignoredParams) {
        Set<OfflinePlayer> whitelist = Bukkit.getWhitelistedPlayers();

        JSONObject whitelistJson = new JSONObject();
        for (OfflinePlayer player : whitelist) {
            whitelistJson.put(player.getUniqueId().toString(), player.getName());
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", whitelistJson.toString());
    }

    public NanoHTTPD.Response postWhitelist(Map<String, String> params) {
        String playerName = params.get("username");
        if (playerName == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{error: 'username parameter is required'}");
        }

        UUID uuid = Helper.usernameToUUID(playerName);
        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.isWhitelisted()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        }

        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> player.setWhitelisted(true));
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response deleteWhitelist(Map<String, String> params) {
        String playerName = params.get("username");
        if (playerName == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{error: 'username parameter is required'}");
        }

        UUID uuid = Helper.usernameToUUID(playerName);
        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (!player.isWhitelisted()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        }

        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> player.setWhitelisted(false));
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }
}
