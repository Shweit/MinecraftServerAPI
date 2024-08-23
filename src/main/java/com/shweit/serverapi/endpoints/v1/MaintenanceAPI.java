package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.MinecraftServerAPI;
import eu.kennytv.maintenance.api.Maintenance;
import eu.kennytv.maintenance.api.MaintenanceProvider;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.util.Map;

public class MaintenanceAPI {
    public final static Maintenance maintenancePlugin = MaintenanceProvider.get();

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

    public NanoHTTPD.Response enableMaintenance(Map<String, String> params) {
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

            if (params.get("endtimer") != null) {
                Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance endtimer " + params.get("endtimer"));
                });

                response.put("endTimer", params.get("endtimer"));
            }

            response.put("status", "enabled");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("error", "Failed to enable maintenance mode");
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", response.toString());
        }
    }

    public NanoHTTPD.Response disableMaintenance(Map<String, String> ignoredParams) {
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
}
