package com.shweit.serverapi.endpoints.v1;


import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Helper;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class WhitelistAPI {
    public NanoHTTPD.Response getWhitelist(final Map<String, String> ignoredParams) {
        if (!Bukkit.hasWhitelist()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        Set<OfflinePlayer> whitelist = Bukkit.getWhitelistedPlayers();

        JSONObject whitelistJson = new JSONObject();
        for (OfflinePlayer player : whitelist) {
            whitelistJson.put(player.getUniqueId().toString(), player.getName());
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", whitelistJson.toString());
    }

    public NanoHTTPD.Response postWhitelist(final Map<String, String> params) {
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

    public NanoHTTPD.Response deleteWhitelist(final Map<String, String> params) {
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

    public NanoHTTPD.Response activateWhitelist(final Map<String, String> params) {
        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> Bukkit.setWhitelist(true));

        if (Objects.equals(params.get("kickPlayers"), "true")) {
            Logger.debug("Kicking all players not on the whitelist");
            // Kick all players not on the whitelist
            Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.isWhitelisted())
                .forEach(player -> player.kickPlayer("Whitelist activated. As you are not on the whitelist, you have been kicked."));
        }
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response deactivateWhitelist(final Map<String, String> ignoredParams) {
        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> Bukkit.setWhitelist(false));
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }
}
