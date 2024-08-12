package com.shweit.serverapi.api.v1.endpoints;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shweit.serverapi.api.v1.models.Player;
import com.shweit.serverapi.api.v1.models.StatisticModel;
import com.shweit.serverapi.utils.Logger;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.geysermc.geyser.api.GeyserApi;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

/**
 * The PlayerAPI class provides endpoints to retrieve player information.
 * <p>
 *
 * @author Shweit
 * @version 1.0
 * @since 1.0
 */
public class PlayerAPI {
    /**
     * Retrieves all currently online players.
     *
     * @param ctx the context of the request
     */
    @OpenApi(
            path = "/v1/players",
            summary = "Get all currently online players",
            description = "This endpoint returns a list of all currently online players.",
            tags = {"Player"},
            responses = {
                @OpenApiResponse(status = "200", content = @OpenApiContent(from = Player[].class))
            }
    )
    public final void getPlayers(Context ctx) {
        ArrayList<Player> players = new ArrayList<>();

        // Check for every online Player if they are Floodgate players
        Bukkit.getOnlinePlayers().forEach(player -> players.add(convertBukkitPlayer(player, GeyserApi.api().isBedrockPlayer(player.getUniqueId()))));

        ctx.json(players);
    }

    /**
     * Retrieves a specific player by their UUID.
     *
     * @param ctx the context of the request
     */
    @OpenApi(
        path = "/v1/players/{username}",
        summary = "Get a specific player",
        description = "This endpoint returns a specific player by their Username.",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(
                    name = "username",
                    description = "The Username of the player"
            )
        },
        responses = {
            @OpenApiResponse(
                    status = "200",
                    content = @OpenApiContent(
                            from = Player.class
                    )
            ),
            @OpenApiResponse(
                    status = "404",
                    description = "Player not found"
            )
        }
    )
    public final void getPlayer(Context ctx) {
        org.bukkit.entity.Player player = convertUsernameToPlayer(ctx.pathParam("username"));

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
        }

        ctx.json(convertBukkitPlayer(player, GeyserApi.api().isBedrockPlayer(player.getUniqueId())));
    }

    @OpenApi(
        path = "/v1/players/{username}/stats",
        summary = "Get the stats of a specific player",
        description = "This endpoint returns the stats of a specific player by their Username.",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(
                    name = "username",
                    description = "The username of the player"
            )
        },
        responses = {
            @OpenApiResponse(
                    status = "200",
                    content = @OpenApiContent(
                            from = Player.class
                    )
            ),
            @OpenApiResponse(
                    status = "404",
                    description = "Player not found"
            )
        }
    )
    public final void getPlayerStats(Context ctx) {
        org.bukkit.entity.Player player = convertUsernameToPlayer(ctx.pathParam("username"));

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
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

        List<StatisticModel> formattedStats = new ArrayList<>();

        stats.forEach((key, value) -> {
            String unit = "";
            String readableName = key.toLowerCase().replace("_", " ");
            StatisticModel stat = new StatisticModel(readableName, value, unit);
            formattedStats.add(stat);
        });

        ctx.status(200).json(formattedStats);
    }

    @OpenApi(
        path = "/v1/players/{username}/inventory",
        methods = {HttpMethod.GET},
        summary = "Get the inventory of a specific player",
        description = "This endpoint returns the inventory of a specific player by their Username.",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(
                    name = "username",
                    description = "The username of the player"
            )
        },
        responses = {
            @OpenApiResponse(
                    status = "200",
                    content = @OpenApiContent(
                            from = Player.class
                    )
            ),
            @OpenApiResponse(
                    status = "404",
                    description = "Player not found"
            )
        }
    )
    public final void getPlayerInventory(Context ctx) {
        org.bukkit.entity.Player player = convertUsernameToPlayer(ctx.pathParam("username"));

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
        }

        ctx.json(player.getInventory().getContents());
    }

    @OpenApi(
        path = "/v1/players/{username}/kick",
        summary = "Kick a specific player",
        methods = {HttpMethod.POST},
        description = "This endpoint kicks a specific player by their Username.",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(
                name = "username",
                description = "The username of the player"
            )
        },
        requestBody = @OpenApiRequestBody(
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "reason", type = "string"),
                }),
            }),
        responses = {
            @OpenApiResponse(
                status = "200",
                content = @OpenApiContent(
                    from = Boolean.class
                )
            ),
            @OpenApiResponse(
                status = "404",
                description = "Player not found"
            )
        }
    )
    public final void kickPlayer(Context ctx) {
        org.bukkit.entity.Player player = Bukkit.getPlayer(ctx.pathParam("username"));

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
        }

        String reason = ctx.formParam("reason");
        player.kickPlayer(reason == null || reason.isEmpty() ? "You have been kicked from the server." : reason);
        ctx.status(200).json(true);
    }

    @OpenApi(
        path = "/v1/players/{username}/ban",
        summary = "Ban a specific player",
        methods = {HttpMethod.POST},
        description = "This endpoint bans a specific player by their Username.",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(
                name = "username",
                description = "The username of the player"
            )
        },
        requestBody = @OpenApiRequestBody(
        content = {
            @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                @OpenApiContentProperty(name = "reason", type = "string"),
                @OpenApiContentProperty(name = "duration", type = "string"),
            }),
        }),
        responses = {
            @OpenApiResponse(
                status = "200",
                content = @OpenApiContent(
                    from = Boolean.class
                )
            ),
            @OpenApiResponse(
                status = "404",
                description = "Player not found"
            )
        }
    )
    public final void banPlayer(Context ctx) {
        org.bukkit.entity.Player player = convertUsernameToPlayer(ctx.pathParam("username"));

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
        }

        String reason = ctx.formParam("reason");
        String durationStr = ctx.formParam("duration");
        int durationDays = 0;
        if (durationStr != null && !durationStr.isEmpty()) {
            try {
                durationDays = Integer.parseInt(durationStr);
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid duration format");
                return;
            }
        }

        reason = reason == null || reason.isEmpty() ? "You have been banned from the server." : reason;
        Date duration = null;
        if (durationStr != null && !durationStr.isEmpty()) {
            duration = Date.from(Instant.now().plusSeconds(durationDays * 86400L));
        }

        player.ban(reason, duration, "console", true);
        ctx.status(200).json(true);
    }

    @OpenApi(
        path = "/v1/players/{username}/unban",
        summary = "Unban a specific player",
        methods = {HttpMethod.POST},
        description = "This endpoint unbans a specific player by their Username.",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(
                name = "username",
                description = "The username of the player"
            )
        },
        responses = {
            @OpenApiResponse(
                status = "200",
                content = @OpenApiContent(
                    from = Boolean.class
                )
            ),
            @OpenApiResponse(
                status = "404",
                description = "Player not found"
            )
        }
    )
    public final void unbanPlayer(Context ctx) {
        String username = ctx.pathParam("username");
        org.bukkit.entity.Player player = convertUsernameToPlayer(username);

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
        }

        if (!player.isBanned()) {
            ctx.status(400).result("Player is not banned");
            return;
        }

        boolean success = Bukkit.getServer().getBannedPlayers().remove(player);
        ctx.status(200).json(success);
    }

    @OpenApi(
        path = "/v1/players/{username}/teleport",
        summary = "Teleport a Player to a Specific Location",
        methods = {HttpMethod.POST},
        description = "This endpoint will teleport the player to another player or to a specific location",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(
                name = "username",
                description = "The username of the player"
            )
        },
        requestBody = @OpenApiRequestBody(
            required = true,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "targetUsername", type = "string"),
                    @OpenApiContentProperty(name = "x", type = "double"),
                    @OpenApiContentProperty(name = "y", type = "double"),
                    @OpenApiContentProperty(name = "z", type = "double"),
                }),
            }),
        responses = {
            @OpenApiResponse(
                status = "200",
                content = @OpenApiContent(
                    from = Boolean.class
                )
            ),
            @OpenApiResponse(
                status = "404",
                description = "Player not found"
            )
        }
    )
    public final void teleportPlayer(Context ctx) {
        String username = ctx.pathParam("username");
        org.bukkit.entity.Player player = Bukkit.getPlayer(username);

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
        }

        String targetUsername = ctx.formParam("targetUsername");
        if (targetUsername != null && !targetUsername.isEmpty()) {
            org.bukkit.entity.Player targetPlayer = Bukkit.getPlayer(targetUsername);
            if (targetPlayer == null) {
                ctx.status(404).result("Target player not found");
                return;
            }
            player.teleport(targetPlayer.getLocation());
        } else {
            double x = Double.parseDouble(ctx.formParam("x"));
            double y = Double.parseDouble(ctx.formParam("y"));
            double z = Double.parseDouble(ctx.formParam("z"));
            player.teleport(new org.bukkit.Location(player.getWorld(), x, y, z));
        }

        ctx.status(200).json(true);
    }

    /**
     * Converts a Bukkit player to a Player model.
     *
     * @param player the Bukkit player to convert
     * @return the converted Player model
     */
    private Player convertBukkitPlayer(org.bukkit.entity.Player player, boolean isBedrockPlayer) {
        Player p = new Player();
        p.setUuid(player.getUniqueId().toString());
        p.setDisplayName(player.getDisplayName());

        p.setAddress(player.getAddress().getHostName());
        p.setPort(player.getAddress().getPort());

        p.setExhaustion(player.getExhaustion());
        p.setExp(player.getExp());
        p.setExpLevel(player.getLevel());

        p.setWhitelisted(player.isWhitelisted());
        p.setBanned(player.isBanned());
        p.setOp(player.isOp());

        p.setHunger(player.getFoodLevel());
        p.setHealth(player.getHealth());
        p.setSaturation(player.getSaturation());

        p.setDimension(player.getWorld().getEnvironment());

        Location playerLocation = player.getLocation();
        Double[] convertedLocation = new Double[3];
        convertedLocation[0] = playerLocation.getX();
        convertedLocation[1] = playerLocation.getY();
        convertedLocation[2] = playerLocation.getZ();

        p.setLocation(convertedLocation);

        p.setGamemode(player.getGameMode());

        p.setLastPlayed(player.getLastPlayed());

        p.setIsBedrockPlayer(isBedrockPlayer);

        return p;
    }

    private org.bukkit.entity.Player convertUsernameToPlayer(String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .build();
        HttpResponse<String> response;

        UUID playerUUID = null;

        try {
            response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            playerUUID = UUID.fromString(jsonObject.get("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            Logger.warning("Failed to convert username to UUID: " + username);
            return null;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

        return player.getPlayer();
    }
}
