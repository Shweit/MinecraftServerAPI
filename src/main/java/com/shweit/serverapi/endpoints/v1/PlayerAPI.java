package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
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

    public NanoHTTPD.Response getPlayerStats(Map<String, String> params) {
        String username = params.get("username");
        OfflinePlayer player = Bukkit.getOfflinePlayer(username);
        if (player == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        Map<String, Object> stats = new HashMap<>();

        for (Statistic stat : Statistic.values()) {
            if (!stat.isSubstatistic()) {
                stats.put(stat.name(), player.getStatistic(stat));
            }
        }

        for (Material material : Material.values()) {
            try {
                if (material.isBlock()) {
                    stats.put("MINE_BLOCK_" + material.name(), player.getStatistic(Statistic.MINE_BLOCK, material));
                }
                stats.put("USE_ITEM_" + material.name(), player.getStatistic(Statistic.USE_ITEM, material));
                stats.put("BREAK_ITEM_" + material.name(), player.getStatistic(Statistic.BREAK_ITEM, material));
                stats.put("CRAFT_ITEM_" + material.name(), player.getStatistic(Statistic.CRAFT_ITEM, material));
            } catch (IllegalArgumentException e) {
                Logger.warning("Failed to get statistic for material: " + material.name());
            }
        }

        for (EntityType entityType : EntityType.values()) {
            try {
                stats.put("KILL_ENTITY_" + entityType.name(), player.getStatistic(Statistic.KILL_ENTITY, entityType));
                stats.put("ENTITY_KILLED_BY_" + entityType.name(), player.getStatistic(Statistic.ENTITY_KILLED_BY, entityType));
            } catch (IllegalArgumentException e) {
                Logger.warning("Failed to get statistic for entity type: " + entityType.name());
            }
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", new JSONObject(stats).toString());
    }

    public NanoHTTPD.Response getPlayerAdvancements(Map<String, String> params) {
        String username = params.get("username");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

        if (offlinePlayer == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Spieler muss online sein, um Advancements abrufen zu können
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject advancementsJson = new JSONObject();

        // Durch alle Advancements iterieren und den Fortschritt des Spielers abrufen
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement advancement = it.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);

            JSONObject advancementJson = new JSONObject();
            advancementJson.put("done", progress.isDone());

            if (!progress.isDone()) {
                advancementJson.put("criteriaRemaining", progress.getRemainingCriteria());
            }

            advancementJson.put("criteriaCompleted", progress.getAwardedCriteria());

            advancementsJson.put(advancement.getKey().toString(), advancementJson);
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", advancementsJson.toString());
    }
}
