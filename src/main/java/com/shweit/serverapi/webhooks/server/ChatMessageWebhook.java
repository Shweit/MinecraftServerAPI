package com.shweit.serverapi.webhooks.server;

import com.shweit.serverapi.webhooks.Webhooks;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ChatMessageWebhook implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ChatMessageWebhook(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @OpenApi(
        path = "/webhook/server/chat",
        summary = "Chat message webhook",
        description = "Sends a webhook when a player sends a chat message.",
        tags = {"Webhooks"}
    )
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getDisplayName();
        String message = event.getMessage();
        String webhookUrl = "http://localhost:" + config.getInt("port") + "/server/chat";
        String payload = "{\"playerName\": \"" + playerName + "\", \"message\": \"" + message + "\"}";

        Webhooks.sendWebhook(webhookUrl, payload);
    }
}