package com.shweit.serverapi.api.v1.endpoints;

import io.javalin.http.Context;
import io.javalin.openapi.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MaintenanceAPI {
    @OpenApi(
        path = "/v1/maintenance",
        methods = {HttpMethod.GET},
        summary = "Is the server in maintenance mode?",
        description = "This endpoint returns true if the server is in maintenance mode.",
        tags = {"Maintenance"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Boolean.class))
        }
    )
    public final void getMaintenanceInfo(Context ctx) {
        boolean maintenance = Bukkit.getServer().getName().contains("Wartungsarbeiten");
        ctx.json(maintenance);
    }

    @OpenApi(
        path = "/v1/maintenance",
        methods = {HttpMethod.POST},
        summary = "Activate the server maintenance mode",
        description = "This Endpoint activates the server maintenance mode with the given time in minutes. If no time is given, the maintenance mode will be active until it is deactivated.",
        tags = {"Maintenance"},
        requestBody = @OpenApiRequestBody(
            required = false,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "time", type = "integer")
                }),
            }
        ),
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Boolean.class))
        }
    )
    public final void activeMaintenance(Context ctx) {
        String timeInMinutes = ctx.formParam("time");

        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MaintenanceAPI.class), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance on");
        });

        if (timeInMinutes != null) {
            if (!timeInMinutes.isEmpty()) {
                if (!timeInMinutes.matches("[0-9]+")) {
                    ctx.status(400).result("Invalid time");
                    return;
                }

                Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MaintenanceAPI.class), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance endtimer " + timeInMinutes);
                });
            }
        }

        ctx.status(200).result("Maintenance mode activated");
    }

    @OpenApi(
        path = "/v1/maintenance",
        methods = {HttpMethod.DELETE},
        summary = "Deactivate the server maintenance mode",
        description = "This endpoint deactivates the server maintenance mode.",
        tags = {"Maintenance"},
        requestBody = @OpenApiRequestBody(
            required = false,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "time", type = "integer")
                }),
            }
        ),
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Boolean.class))
        }
    )
    public final void deactivateMaintenance(Context ctx) {
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MaintenanceAPI.class), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maintenance off");
        });
        ctx.status(200).result("Maintenance mode deactivated");
    }
}
