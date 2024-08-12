package com.shweit.serverapi.api.v1.endpoints;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.api.v1.models.Whitelist;
import io.javalin.http.*;
import io.javalin.openapi.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.UUID;

public class WhitelistAPI {

    @OpenApi(
        path = "/v1/whitelist",
        methods = {HttpMethod.GET},
        summary = "Get the whitelist with all players",
        description = "This endpoint returns the whitelist with all players.",
        tags = {"Whitelist"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Whitelist.class)),
            @OpenApiResponse(status = "400", description = "Whitelist disabled")
        }
    )
    public void getWhitelist(Context ctx) {
        if (!Bukkit.hasWhitelist()) {
            ctx.status(400).result("Whitelist disabled");
            return;
        }

        ArrayList<Whitelist> whitelist = new ArrayList<>();
        Bukkit.getWhitelistedPlayers().forEach(player -> whitelist.add(convertBukkitPlayer(player)));

        ctx.json(whitelist);
    }

    @OpenApi(
        path = "/v1/whitelist",
        methods = {HttpMethod.POST},
        summary = "Add a player to the Java whitelist",
        description = "This endpoint adds a player to the Java whitelist. If you want to add a Floodgate player to the whitelist, use the /v1/floodgate/whitelist endpoint.",
        tags = {"Whitelist"},
        requestBody = @OpenApiRequestBody(
            required = true,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "uuid", type = "string"),
                    @OpenApiContentProperty(name = "username", type = "string")
                }),
            }),
        responses = {
            @OpenApiResponse(status = "200", description = "Player added to whitelist"),
            @OpenApiResponse(status = "404", description = "Player not found"),
            @OpenApiResponse(status = "400", description = "Whitelist disabled, or missing parameters")
        }
    )
    public void addPlayer(Context ctx) {
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

        OfflinePlayer player;

        if (uuidString == null || uuidString.isEmpty()) {
            Logger.info("Adding player to whitelist: " + username);

            UUID playerUUID = convertUsernameToUUID(username);

            if (playerUUID == null) {
                Logger.warning("Failed to convert username to UUID: " + username);
                ctx.status(404).result("Player not found");
                return;
            }

            player = Bukkit.getOfflinePlayer(playerUUID);
        } else {
            if (uuidString.length() == 32) {
                Logger.info("Converting UUID to standard format");
                uuidString = uuidString.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            }

            player = Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
        }

        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(WhitelistAPI.class), () -> {
            player.setWhitelisted(true);
            Bukkit.reloadWhitelist();
        });

        ctx.status(200).result("Player added to whitelist");
    }

    @OpenApi(
        path = "/v1/whitelist",
        methods = {HttpMethod.DELETE},
        summary = "Remove a player from the Java whitelist",
        description = "This endpoint removes a player from the Java whitelist. If you want to remove a Floodgate player from the whitelist, use the /v1/floodgate/whitelist endpoint.",
        tags = {"Whitelist"},
        requestBody = @OpenApiRequestBody(
            required = true,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "uuid", type = "string"),
                    @OpenApiContentProperty(name = "username", type = "string")
                }),
            }),
        responses = {
            @OpenApiResponse(status = "200", description = "Player removed from whitelist"),
            @OpenApiResponse(status = "404", description = "Player not found"),
            @OpenApiResponse(status = "400", description = "Whitelist disabled, or missing parameters")
        }
    )
    public void removePlayer(Context ctx) {
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

        OfflinePlayer player;

        if (uuidString == null || uuidString.isEmpty()) {
            Logger.info("Removing player from whitelist: " + username);

            UUID playerUUID = convertUsernameToUUID(username);

            if (playerUUID == null) {
                ctx.status(404).result("Player not found");
                return;
            }

            player = Bukkit.getOfflinePlayer(playerUUID);
        } else {
            if (uuidString.length() == 32) {
                Logger.info("Converting UUID to standard format");
                uuidString = uuidString.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            }

            player = Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
        }

        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(WhitelistAPI.class), () -> {
            player.setWhitelisted(false);
            Bukkit.reloadWhitelist();
        });

        ctx.status(200).result("Player removed from whitelist");
    }

    private Whitelist convertBukkitPlayer(OfflinePlayer player) {
        Whitelist p = new Whitelist();
        p.setUuid(player.getUniqueId().toString());
        p.setUsername(player.getName());
        return p;
    }

    private UUID convertUsernameToUUID(String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .build();
        HttpResponse<String> response;

        try {
            response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            return UUID.fromString(jsonObject.get("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            Logger.warning("Failed to convert username to UUID: " + username);
            return null;
        }
    }
}
