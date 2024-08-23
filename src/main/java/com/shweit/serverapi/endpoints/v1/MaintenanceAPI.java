package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Helper;
import eu.kennytv.maintenance.api.Maintenance;
import eu.kennytv.maintenance.api.MaintenanceProvider;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

public final class MaintenanceAPI {
    public final Maintenance maintenancePlugin = MaintenanceProvider.get();

    public NanoHTTPD.Response getMaintenanceStatus(final Map<String, String> ignoredParams) {
        if (maintenancePlugin.isMaintenance()) {
            JSONObject response = new JSONObject();
            response.put("status", "enabled");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        } else {
            JSONObject response = new JSONObject();
            response.put("status", "disabled");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        }
    }

    public NanoHTTPD.Response enableMaintenance(final Map<String, String> params) {
        try {
            JSONObject response = new JSONObject();
            if (params.get("startTimer") != null) {
                Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance starttimer " + params.get("startTimer"));
                });

                response.put("startTimer", params.get("startTimer"));
            } else {
                Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance on");
                });
            }

            if (params.get("endTimer") != null) {
                Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance endtimer " + params.get("endTimer"));
                });

                response.put("endTimer", params.get("endTimer"));
            }

            response.put("status", "enabled");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", "Failed to enable maintenance mode");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", response.toString());
        }
    }

    public NanoHTTPD.Response disableMaintenance(final Map<String, String> ignoredParams) {
        try {
            JSONObject response = new JSONObject();
            Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance off");
            });

            response.put("status", "disabled");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", "Failed to disable maintenance mode");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", response.toString());
        }
    }

    public NanoHTTPD.Response getMaintenanceWhitelist(final Map<String, String> ignoredParams) {
        JSONObject response = new JSONObject();

        for (Map.Entry<UUID, String> entry : maintenancePlugin.getSettings().getWhitelistedPlayers().entrySet()) {
            response.put(entry.getKey().toString(), entry.getValue());
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    public NanoHTTPD.Response addPlayerToMaintenanceWhitelist(final Map<String, String> params) {
        try {
            if (!params.containsKey("name")) {
                JSONObject response = new JSONObject();
                response.put("error", "Missing player name");
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", response.toString());
            }

            String playerName = params.get("name");
            UUID playerUUID = Helper.usernameToUUID(playerName);

            boolean added = maintenancePlugin.getSettings().addWhitelistedPlayer(playerUUID, playerName);

            if (!added) {
                JSONObject response = new JSONObject();
                response.put("error", "The player already is in the maintenance whitelist");
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.CONFLICT, "application/json", response.toString());
            }

            JSONObject response = new JSONObject();
            response.put("uuid", playerUUID.toString());
            response.put("name", playerName);
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", "Failed to add the player to the maintenance whitelist");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", response.toString());
        }
    }

    public NanoHTTPD.Response removePlayerFromMaintenanceWhitelist(final Map<String, String> params) {
        try {
            if (!params.containsKey("name")) {
                JSONObject response = new JSONObject();
                response.put("error", "Missing player name");
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", response.toString());
            }

            String playerName = params.get("name");
            UUID playerUUID = Helper.usernameToUUID(playerName);

            if (!maintenancePlugin.getSettings().isWhitelisted(playerUUID)) {
                JSONObject response = new JSONObject();
                response.put("error", "The player is not in the maintenance whitelist");
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.CONFLICT, "application/json", response.toString());
            }

            maintenancePlugin.getSettings().removeWhitelistedPlayer(playerUUID);

            JSONObject response = new JSONObject();
            response.put("uuid", playerUUID.toString());
            response.put("name", playerName);
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", "Failed to remove the player from the maintenance whitelist");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", response.toString());
        }
    }
}
