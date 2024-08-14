package com.shweit.serverapi.endpoints.v1;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class PlayerAPI {
    public NanoHTTPD.Response getPlayers(Map<String, String> params) {
        JSONArray playersArray = new JSONArray();

        for (Player player : Bukkit.getOnlinePlayers()) {
            JSONObject playerJson = new JSONObject();
            playerJson.put("name", player.getName());
            playerJson.put("uuid", player.getUniqueId().toString());
            playersArray.put(playerJson);
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("onlinePlayers", playersArray);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", responseJson.toString());
    }

    public NanoHTTPD.Response getBannedPlayers(Map<String, String> params) {
        JSONArray bannedPlayersArray = new JSONArray();

        for (OfflinePlayer player : Bukkit.getBannedPlayers()) {
            JSONObject playerJson = new JSONObject();
            playerJson.put("name", player.getName());
            playerJson.put("uuid", player.getUniqueId().toString());
            bannedPlayersArray.put(playerJson);
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("bannedPlayers", bannedPlayersArray);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", responseJson.toString());
    }

    public NanoHTTPD.Response getOfflinePlayers(Map<String, String> params) {
        JSONArray offlinePlayersArray = new JSONArray();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            JSONObject playerJson = new JSONObject();
            playerJson.put("name", player.getName());
            playerJson.put("uuid", player.getUniqueId().toString());
            offlinePlayersArray.put(playerJson);
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("offlinePlayers", offlinePlayersArray);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", responseJson.toString());
    }

    public NanoHTTPD.Response getPlayer(Map<String, String> params) {
        String username = params.get("username");
        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        if (player == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject playerJson = new JSONObject();
        JSONObject locationJson = new JSONObject();
        JSONObject lastDeathLocationJson = new JSONObject();

        locationJson.put("world", player.getLocation().getWorld().getName());
        locationJson.put("x", player.getLocation().getX());
        locationJson.put("y", player.getLocation().getY());
        locationJson.put("z", player.getLocation().getZ());
        locationJson.put("yaw", player.getLocation().getYaw());
        locationJson.put("pitch", player.getLocation().getPitch());

        if (player.getLastDeathLocation() != null) {
            lastDeathLocationJson.put("world", player.getLastDeathLocation().getWorld().getName());
            lastDeathLocationJson.put("x", player.getLastDeathLocation().getX());
            lastDeathLocationJson.put("y", player.getLastDeathLocation().getY());
            lastDeathLocationJson.put("z", player.getLastDeathLocation().getZ());
            lastDeathLocationJson.put("yaw", player.getLastDeathLocation().getYaw());
            lastDeathLocationJson.put("pitch", player.getLastDeathLocation().getPitch());
        }

        playerJson.put("name", player.getName());
        playerJson.put("uuid", player.getUniqueId().toString());
        playerJson.put("firstPlayed", player.getFirstPlayed());
        playerJson.put("lastPlayed", player.getLastPlayed());
        playerJson.put("isOnline", player.isOnline());
        playerJson.put("isBanned", player.isBanned());
        playerJson.put("isWhitelisted", player.isWhitelisted());
        playerJson.put("isOp", player.isOp());
        playerJson.put("location", locationJson);
        playerJson.put("lastDeathLocation", lastDeathLocationJson);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", playerJson.toString());
    }
}
