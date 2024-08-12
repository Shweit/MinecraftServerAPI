package com.shweit.serverapi.webhooks.server;

import com.shweit.serverapi.webhooks.Webhooks;
import io.javalin.openapi.OpenApi;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ServerStopWebhook {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ServerStopWebhook(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @OpenApi(
        path = "/webhook/server/stop",
        summary = "Server stop webhook",
        description = "Sends a webhook when the server stops.",
        tags = {"Webhooks"}
    )
    public void sendServerStopWebhook() {
        String webhookUrl = "http://localhost:" + config.getInt("port") + "/webhook/server/stop";
        String payload = "{\"message\": \"Server has stopped.\"}";

        Webhooks.sendWebhook(webhookUrl, payload);
    }
}