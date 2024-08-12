package com.shweit.serverapi.webhooks.player;

import com.shweit.serverapi.webhooks.Webhooks;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiParam;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinWebhook implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public PlayerJoinWebhook(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @OpenApi(
        path = "/webhook/{username}/join",
        methods = {HttpMethod.GET},
        summary = "Player join webhook",
        description = "Sends a webhook when a player joins the server.",
        tags = {"Webhooks"},
        pathParams = {
            @OpenApiParam(
                name = "username",
                description = "The name of the player who joined the server."
            )
        }
    )
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getDisplayName();
        Webhooks.sendWebhook("http://localhost:" + config.getInt("port") + "/webhook/" + playerName + "/join", "{\"playerName\": \"" + playerName + "\"}");
    }
}
