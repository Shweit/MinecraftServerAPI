package com.shweit.serverapi.endpoints;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shweit.serverapi.models.Whitelist;
import io.javalin.http.*;
import io.javalin.openapi.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WhitelistAPI {

    @OpenApi(
        path = "/v1/whitelist",
        methods = {HttpMethod.GET},
        summary = "Get the whitelist with all players",
        description = "This endpoint returns the whitelist with all players.",
        tags = {"Whitelist"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Whitelist.class)),
            @OpenApiResponse(status = "400", description = "Whitelist is not enabled")
        }
    )
    public void getWhitelist(Context ctx) {
        if (!Bukkit.hasWhitelist()) {
            ctx.status(400).result("Whitelist is not enabled");
            return;
        }

        ArrayList<Whitelist> whitelist = new ArrayList<>();
        Bukkit.getWhitelistedPlayers().forEach(player -> whitelist.add(convertBukkitPlayer(player)));

        ctx.json(whitelist);
    }

    @OpenApi(
        path = "/v1/whitelist",
        methods = {HttpMethod.POST},
        summary = "Add a player to the whitelist",
        description = "This endpoint adds a player to the whitelist.",
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
            @OpenApiResponse(status = "400", description = "Whitelist is not enabled, or missing parameters")
        }
    )
    public void addPlayer(Context ctx) {
        if (!Bukkit.hasWhitelist()) {
            ctx.status(400).result("Whitelist is not enabled");
            return;
        }

        String uuidString = ctx.formParam("uuid");
        String username = ctx.formParam("username");

        if (Objects.equals(uuidString, "")) {
            if (Objects.equals(username, "")) {
                ctx.status(400).result("Missing parameters");
                return;
            }
            uuidString = convertUsernameToUUID(username);
            if (uuidString == null) {
                ctx.status(404).result("Invalid username");
                return;
            }
        }

        Bukkit.getLogger().log(Level.INFO, "[MinecraftServerAPI] Adding player to whitelist: " + uuidString);

        UUID uuid = UUID.fromString(uuidString);
        Bukkit.getOfflinePlayer(uuid).setWhitelisted(true);
        ctx.status(200).result("Player added to whitelist");
    }

    @OpenApi(
        path = "/v1/whitelist",
        methods = {HttpMethod.DELETE},
        summary = "Remove a player from the whitelist",
        description = "This endpoint removes a player from the whitelist.",
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
            @OpenApiResponse(status = "400", description = "Whitelist is not enabled, or missing parameters")
        }
    )
    public void removePlayer(Context ctx) {
        if (!Bukkit.hasWhitelist()) {
            ctx.status(400).result("Whitelist disabled");
            return;
        }

        String uuidString = ctx.formParam("uuid");
        String username = ctx.formParam("username");

        if (Objects.equals(uuidString, "")) {
            if (Objects.equals(username, "")) {
                ctx.status(400).result("Missing parameters");
                return;
            }
            uuidString = convertUsernameToUUID(username);
            if (uuidString == null) {
                ctx.status(404).result("Invalid username");
                return;
            }
        }
        Bukkit.getLogger().log(Level.INFO, "[MinecraftServerAPI] Removing player from whitelist: " + uuidString);

        UUID uuid = UUID.fromString(uuidString);
        Bukkit.getOfflinePlayer(uuid).setWhitelisted(false);
        ctx.status(200).result("Player removed from whitelist");
    }

    private Whitelist convertBukkitPlayer(OfflinePlayer player) {
        Whitelist p = new Whitelist();
        p.setUuid(player.getUniqueId().toString());
        p.setUsername(player.getName());
        return p;
    }

    private String convertUsernameToUUID(String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .build();
        HttpResponse<String> response;

        Bukkit.getLogger().log(Level.INFO, "[MinecraftServerAPI] Converting username to UUID: " + username);

        try {
            response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            return jsonObject.get("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "[MinecraftServerAPI] Failed to convert username to UUID: " + username);
            return null;
        }
    }
}
