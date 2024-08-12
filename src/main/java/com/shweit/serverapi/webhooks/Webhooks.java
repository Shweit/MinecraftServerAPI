package com.shweit.serverapi.webhooks;

import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.webhooks.player.PlayerDeathWebhook;
import com.shweit.serverapi.webhooks.player.PlayerJoinWebhook;
import com.shweit.serverapi.webhooks.player.PlayerLeaveWebhook;
import com.shweit.serverapi.webhooks.player.PlayerLocationWebhook;
import com.shweit.serverapi.webhooks.server.ChatMessageWebhook;
import com.shweit.serverapi.webhooks.server.ServerStartWebhook;
import com.shweit.serverapi.webhooks.server.ServerStopWebhook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Webhooks {
    public static void registerWebhooks(final FileConfiguration config, final JavaPlugin plugin) {
        if (config.getBoolean("webhooks.player_location")) {
            new PlayerLocationWebhook().registerWebhook(config, plugin);
        }

        if (config.getBoolean("webhooks.player_join")) {
            new PlayerJoinWebhook(plugin, config);
        }

        if (config.getBoolean("webhooks.player_leave")) {
            new PlayerLeaveWebhook(plugin, config);
        }

        if (config.getBoolean("webhooks.player_death")) {
            new PlayerDeathWebhook(plugin, config);
        }

        if (config.getBoolean("webhooks.server_start")) {
            new ServerStartWebhook(plugin, config);
        }

        if (config.getBoolean("webhooks.server_stop")) {
            new ServerStopWebhook(plugin, config);
        }

        if (config.getBoolean("webhooks.server_chat")) {
            new ChatMessageWebhook(plugin, config);
        }

        Logger.info("Webhooks registered");
    }

    public static void sendWebhook(String url_string, String message) {
        try {
            URL url = new URL(url_string);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"content\": \"" + message + "\"}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Logger.info("Webhook sent: " + response.toString());
            }
        } catch (Exception e) {
            Logger.error("Failed to send webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
