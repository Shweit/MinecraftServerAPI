package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Helper;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.util.*;

public final class PlayerAPI {

    public NanoHTTPD.Response getPlayers(final Map<String, String> ignoredParams) {
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

    public NanoHTTPD.Response getBannedPlayers(final Map<String, String> ignoredParams) {
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

    public NanoHTTPD.Response getOfflinePlayers(final Map<String, String> ignoredParams) {
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

    public NanoHTTPD.Response getPlayer(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject playerJson = new JSONObject();
        JSONObject locationJson = new JSONObject();
        JSONObject lastDeathLocationJson = new JSONObject();

        locationJson.put("world", offlinePlayer.getLocation().getWorld().getName());
        locationJson.put("x", offlinePlayer.getLocation().getX());
        locationJson.put("y", offlinePlayer.getLocation().getY());
        locationJson.put("z", offlinePlayer.getLocation().getZ());
        locationJson.put("yaw", offlinePlayer.getLocation().getYaw());
        locationJson.put("pitch", offlinePlayer.getLocation().getPitch());

        if (offlinePlayer.getLastDeathLocation() != null) {
            lastDeathLocationJson.put("world", offlinePlayer.getLastDeathLocation().getWorld().getName());
            lastDeathLocationJson.put("x", offlinePlayer.getLastDeathLocation().getX());
            lastDeathLocationJson.put("y", offlinePlayer.getLastDeathLocation().getY());
            lastDeathLocationJson.put("z", offlinePlayer.getLastDeathLocation().getZ());
            lastDeathLocationJson.put("yaw", offlinePlayer.getLastDeathLocation().getYaw());
            lastDeathLocationJson.put("pitch", offlinePlayer.getLastDeathLocation().getPitch());
        }

        playerJson.put("name", offlinePlayer.getName());
        playerJson.put("uuid", offlinePlayer.getUniqueId().toString());
        playerJson.put("firstPlayed", offlinePlayer.getFirstPlayed());
        playerJson.put("lastPlayed", offlinePlayer.getLastPlayed());
        playerJson.put("isOnline", offlinePlayer.isOnline());
        playerJson.put("isBanned", offlinePlayer.isBanned());
        playerJson.put("isWhitelisted", offlinePlayer.isWhitelisted());
        playerJson.put("isOp", offlinePlayer.isOp());
        playerJson.put("location", locationJson);
        playerJson.put("lastDeathLocation", lastDeathLocationJson);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", playerJson.toString());
    }

    public NanoHTTPD.Response getPlayerStats(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        Map<String, Object> stats = new HashMap<>();

        for (Statistic stat : Statistic.values()) {
            if (!stat.isSubstatistic()) {
                stats.put(stat.name(), offlinePlayer.getStatistic(stat));
            }
        }

        for (Material material : Material.values()) {
            try {
                if (material.isBlock()) {
                    stats.put("MINE_BLOCK_" + material.name(), offlinePlayer.getStatistic(Statistic.MINE_BLOCK, material));
                }
                stats.put("USE_ITEM_" + material.name(), offlinePlayer.getStatistic(Statistic.USE_ITEM, material));
                stats.put("BREAK_ITEM_" + material.name(), offlinePlayer.getStatistic(Statistic.BREAK_ITEM, material));
                stats.put("CRAFT_ITEM_" + material.name(), offlinePlayer.getStatistic(Statistic.CRAFT_ITEM, material));
            } catch (IllegalArgumentException e) {
                Logger.warning("Failed to get statistic for material: " + material.name());
            }
        }

        for (EntityType entityType : EntityType.values()) {
            try {
                stats.put("KILL_ENTITY_" + entityType.name(), offlinePlayer.getStatistic(Statistic.KILL_ENTITY, entityType));
                stats.put("ENTITY_KILLED_BY_" + entityType.name(), offlinePlayer.getStatistic(Statistic.ENTITY_KILLED_BY, entityType));
            } catch (IllegalArgumentException e) {
                Logger.warning("Failed to get statistic for entity type: " + entityType.name());
            }
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", new JSONObject(stats).toString());
    }

    public NanoHTTPD.Response getPlayerAdvancements(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Spieler muss online sein, um Advancements abrufen zu können
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject advancementsJson = new JSONObject();

        // Durch alle Advancements iterieren und den Fortschritt des Spielers abrufen
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext();) {
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

    public NanoHTTPD.Response getPlayerInventory(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Player needs to be online to be able to get inventory
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject inventoryJson = new JSONObject();

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) {
                continue;
            }

            JSONObject itemJson = new JSONObject();
            itemJson.put("type", player.getInventory().getItem(i).getType().name());
            itemJson.put("amount", player.getInventory().getItem(i).getAmount());
            itemJson.put("durability", player.getInventory().getItem(i).getDurability());
            itemJson.put("displayName", player.getInventory().getItem(i).getItemMeta().getDisplayName());
            itemJson.put("lore", player.getInventory().getItem(i).getItemMeta().getLore());
            itemJson.put("enchantments", player.getInventory().getItem(i).getEnchantments());
            itemJson.put("attributes", player.getInventory().getItem(i).getItemMeta().getAttributeModifiers());
            itemJson.put("flags", player.getInventory().getItem(i).getItemMeta().getItemFlags());
            itemJson.put("unbreakable", player.getInventory().getItem(i).getItemMeta().isUnbreakable());
            if (player.getInventory().getItem(i).getItemMeta().hasCustomModelData()) {
                itemJson.put("customModelData", player.getInventory().getItem(i).getItemMeta().getCustomModelData());
            }
            itemJson.put("itemFlags", player.getInventory().getItem(i).getItemMeta().getItemFlags());
            itemJson.put("itemMeta", player.getInventory().getItem(i).getItemMeta());
            inventoryJson.put(String.valueOf(i), itemJson);
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", inventoryJson.toString());
    }

    public NanoHTTPD.Response getPlayerInventorySlot(final Map<String, String> params) {
        String username = params.get("username");
        int i = Integer.parseInt(params.get("slot"));
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Spieler muss online sein, um Inventar abrufen zu können
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject itemJson = new JSONObject();
        itemJson.put("type", player.getInventory().getItem(i).getType().name());
        itemJson.put("amount", player.getInventory().getItem(i).getAmount());
        itemJson.put("durability", player.getInventory().getItem(i).getDurability());
        itemJson.put("displayName", player.getInventory().getItem(i).getItemMeta().getDisplayName());
        itemJson.put("lore", player.getInventory().getItem(i).getItemMeta().getLore());
        itemJson.put("enchantments", player.getInventory().getItem(i).getEnchantments());
        itemJson.put("attributes", player.getInventory().getItem(i).getItemMeta().getAttributeModifiers());
        itemJson.put("flags", player.getInventory().getItem(i).getItemMeta().getItemFlags());
        itemJson.put("unbreakable", player.getInventory().getItem(i).getItemMeta().isUnbreakable());
        if (player.getInventory().getItem(i).getItemMeta().hasCustomModelData()) {
            itemJson.put("customModelData", player.getInventory().getItem(i).getItemMeta().getCustomModelData());
        }
        itemJson.put("itemFlags", player.getInventory().getItem(i).getItemMeta().getItemFlags());
        itemJson.put("itemMeta", player.getInventory().getItem(i).getItemMeta());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", itemJson.toString());
    }

    public NanoHTTPD.Response kickPlayer(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Player need to be online to be able to kick
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Get reason from request
        String reason = params.get("reason");
        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
            player.kickPlayer(reason != null ? reason : "You have been kicked from the server.");
        });

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response banPlayer(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Get reason from request
        String reason = params.get("reason");
        Duration duration = params.containsKey("duration") ? Duration.parse("PT" + params.get("duration") + "S") : null;
        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
            offlinePlayer.ban(reason != null ? reason : "You have been banned from the server.", duration, null);
        });

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response pardonPlayer(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        if (offlinePlayer.isBanned()) {
            Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
                Bukkit.getBanList(BanList.Type.NAME).pardon(offlinePlayer.getName());
            });
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response getPlayerLocation(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject locationJson = new JSONObject();

        locationJson.put("world", offlinePlayer.getLocation().getWorld().getName());
        locationJson.put("x", offlinePlayer.getLocation().getX());
        locationJson.put("y", offlinePlayer.getLocation().getY());
        locationJson.put("z", offlinePlayer.getLocation().getZ());
        locationJson.put("yaw", offlinePlayer.getLocation().getYaw());
        locationJson.put("pitch", offlinePlayer.getLocation().getPitch());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", locationJson.toString());
    }

    public NanoHTTPD.Response setPlayerLocation(final Map<String, String> params) {
        String username = params.get("username");
        UUID uuid = Helper.usernameToUUID(username);

        if (uuid == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Player needs to be online to be able to set location
        Player player = offlinePlayer.getPlayer();
        if (player == null || !player.isOnline()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        // Get location from request
        String worldName = params.get("world");
        double x = Double.parseDouble(params.get("x"));
        double y = Double.parseDouble(params.get("y"));
        double z = Double.parseDouble(params.get("z"));
        float yaw = params.get("yaw") != null ? Float.parseFloat(params.get("yaw")) : player.getLocation().getYaw();
        float pitch = params.get("pitch") != null ? Float.parseFloat(params.get("pitch")) : player.getLocation().getPitch();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        Location location = new Location(world, x, y, z, yaw, pitch);
        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
            player.teleport(location);
        });

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }
}
