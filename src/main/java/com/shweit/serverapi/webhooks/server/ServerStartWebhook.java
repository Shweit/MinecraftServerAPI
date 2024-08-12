package com.shweit.serverapi.webhooks.server;

import com.shweit.serverapi.webhooks.Webhooks;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ServerStartWebhook {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ServerStartWebhook(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        sendServerStartWebhook();
    }

    @OpenApi(
        path = "/webhook/server/start",
        summary = "Server start webhook",
        description = "Sends a webhook when the server starts.",
        tags = {"Webhooks"}
    )
    private void sendServerStartWebhook() {
        String webhookUrl = "http://localhost:" + config.getInt("port") + "/webhook/server/start";
        String payload = "{\"message\": \"Server has started.\"}";

        Webhooks.sendWebhook(webhookUrl, payload);
    }
}