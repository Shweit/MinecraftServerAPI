package com.shweit.serverapi.api.v1.endpoints;

import com.shweit.serverapi.utils.Logger;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GeyserAPI {

    @OpenApi(
        path = "/v1/geyser",
        methods = {HttpMethod.GET},
        summary = "Is Geyser installed?",
        description = "This endpoint returns true if Geyser is installed.",
        tags = {"Geyser"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Boolean.class))
        }
    )
    public final void getGeyserInfo(Context ctx) {
        boolean installed = Bukkit.getPluginManager().getPlugin("Geyser-spigot") != null;
        ctx.json(installed);
    }

    @OpenApi(
        path = "/v1/floodgate",
        methods = {HttpMethod.GET},
        summary = "Is Floodgate installed?",
        description = "This endpoint returns true if Floodgate is installed.",
        tags = {"Floodgate"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Boolean.class))
        }
    )
    public final void getFloodgateInfo(Context ctx) {
        boolean installed = Bukkit.getPluginManager().getPlugin("Floodgate") != null;
        ctx.json(installed);
    }

    @OpenApi(
        path = "/v1/floodgate/whitelist",
        methods = {HttpMethod.POST},
        summary = "Add a player to the Floodgate whitelist",
        description = "This endpoint adds a player to the Floodgate whitelist. If you want to add a Java player to the whitelist, use the /v1/whitelist endpoint.",
        tags = {"Floodgate", "Whitelist"},
        requestBody = @OpenApiRequestBody(
            required = true,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "uuid", type = "string"),
                    @OpenApiContentProperty(name = "username", type = "string")
                }),
            }
        ),
        responses = {
            @OpenApiResponse(status = "200", description = "Player removed from whitelist"),
            @OpenApiResponse(status = "400", description = "Whitelist disabled, or missing parameters")
        }
    )
    public final void addPlayerToWhitelist(Context ctx) {
        if (!Bukkit.hasWhitelist()) {
            ctx.status(400).result("Whitelist disabled");
            return;
        }

        String uuidString = ctx.formParam("uuid");
        String username = ctx.formParam("username");

        Logger.info("Adding player to whitelist");

        // First check if the UUID or username is provided, if not return 400
        if ((uuidString == null || uuidString.isEmpty()) && (username == null || username.isEmpty())) {
            Logger.warning("Missing parameters");
            ctx.status(400).result("Missing parameters");
            return;
        }

        if (uuidString == null || uuidString.isEmpty()) {
            Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(WhitelistAPI.class), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fwhitelist add " + username);
                Bukkit.reloadWhitelist();
            });
        } else {
            Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(WhitelistAPI.class), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fwhitelist add " + uuidString);
                Bukkit.reloadWhitelist();
            });
        }

        ctx.status(200).result("Player added to whitelist");
    }

    @OpenApi(
        path = "/v1/floodgate/whitelist",
        methods = {HttpMethod.DELETE},
        summary = "Remove a player from the Floodgate whitelist",
        description = "This endpoint removes a player from the Floodgate whitelist. If you want to remove a Java player from the whitelist, use the /v1/whitelist endpoint.",
        tags = {"Floodgate", "Whitelist"},
        requestBody = @OpenApiRequestBody(
            required = true,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "uuid", type = "string"),
                    @OpenApiContentProperty(name = "username", type = "string")
                }),
            }
        ),
        responses = {
            @OpenApiResponse(status = "200", description = "Player removed from whitelist"),
            @OpenApiResponse(status = "400", description = "Whitelist disabled, or missing parameters")
        }
    )
    public final void removePlayerFromWhitelist(Context ctx) {
        if (!Bukkit.hasWhitelist()) {
            ctx.status(400).result("Whitelist disabled");
            return;
        }

        String uuidString = ctx.formParam("uuid");
        String username = ctx.formParam("username");

        Logger.info("Removing player from whitelist");

        // First check if the UUID or username is provided, if not return 400
        if ((uuidString == null || uuidString.isEmpty()) && (username == null || username.isEmpty())) {
            Logger.warning("Missing parameters");
            ctx.status(400).result("Missing parameters");
            return;
        }

        if (uuidString == null || uuidString.isEmpty()) {
            Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(WhitelistAPI.class), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fwhitelist remove " + username);
                Bukkit.reloadWhitelist();
            });
        } else {
            Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(WhitelistAPI.class), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fwhitelist remove " + uuidString);
                Bukkit.reloadWhitelist();
            });
        }

        ctx.status(200).result("Player removed from whitelist");
    }
}
