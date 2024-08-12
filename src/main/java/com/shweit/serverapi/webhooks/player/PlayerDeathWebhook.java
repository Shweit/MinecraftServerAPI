package com.shweit.serverapi.webhooks.player;

import com.shweit.serverapi.webhooks.Webhooks;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiParam;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerDeathWebhook implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public PlayerDeathWebhook(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @OpenApi(
        path = "/webhook/{username}/death",
        methods = {HttpMethod.GET},
        summary = "Player death webhook",
        description = "Sends a webhook when a player dies.",
        tags = {"Webhooks"},
        pathParams = {
            @OpenApiParam(
                name = "username",
                description = "The name of the player who died."
            )
        }
    )
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getDisplayName();
        String deathMessage = event.getDeathMessage();
        String webhookUrl = "http://localhost:" + config.getInt("port") + "/webhook/" + playerName + "/death";
        String payload = "{\"playerName\": \"" + playerName + "\", \"deathMessage\": \"" + deathMessage + "\"}";

        Webhooks.sendWebhook(webhookUrl, payload);
    }
}