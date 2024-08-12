package com.shweit.serverapi.webhooks.player;

import com.shweit.serverapi.webhooks.Webhooks;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiParam;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLocationWebhook {
    @OpenApi(
        path = "/webhook/{username}/location",
        summary = "Get player location",
        description = "Get the location of a player. The location will be updated every 10 seconds. The location will not be updated if the player is sneaking, invisible, or in spectator mode.",
        tags = {"Webhooks"},
        pathParams = {
            @OpenApiParam(
                name = "username",
                description = "The Username of the player"
            )
        }
    )
    public void registerWebhook(final FileConfiguration config, final JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (
                        !player.isSneaking() &&
                        !player.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY) &&
                        player.getGameMode() != org.bukkit.GameMode.SPECTATOR
                    ) {
                        Webhooks.sendWebhook(
                        "localhost:" + config.getInt("port") + "/v1/players/" + player.getDisplayName() + "/location",
                        "{\"x\": " + player.getLocation().getX() + ", \"y\": " + player.getLocation().getY() + ", \"z\": " + player.getLocation().getZ() + "}"
                        );
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 200);
    }
}
