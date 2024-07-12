package com.shweit.serverapi.endpoints;
import com.shweit.serverapi.models.Player;
import io.javalin.http.*;
import io.javalin.openapi.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.ArrayList;

public class PlayerAPI {

    @OpenApi(
            path = "/v1/players",
            summary = "Get all currently online players",
            description = "This endpoint returns a list of all currently online players.",
            tags = {"Player"},
            responses = {
                @OpenApiResponse(status = "200", content = @OpenApiContent(from = Player[].class))
            }
    )
    public void getPlayers(Context ctx) {
        ArrayList<Player> players = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(player -> players.add(convertBukkitPlayer(player)));

        ctx.json(players);
    }

    @OpenApi(
        path = "/v1/players/{uuid}",
        summary = "Get a specific player",
        description = "This endpoint returns a specific player by their UUID.",
        tags = {"Player"},
        pathParams = {
            @OpenApiParam(name = "uuid", type = String.class, description = "The UUID of the player")
        },
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Player.class)),
            @OpenApiResponse(status = "404", description = "Player not found")
        }
    )
    public void getPlayer(Context ctx) {
        org.bukkit.entity.Player player = Bukkit.getPlayer(ctx.pathParam("uuid"));

        if (player == null) {
            ctx.status(404).result("Player not found");
            return;
        }

        ctx.json(convertBukkitPlayer(player));
    }

    private Player convertBukkitPlayer(org.bukkit.entity.Player player) {
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

        return p;
    }

}
