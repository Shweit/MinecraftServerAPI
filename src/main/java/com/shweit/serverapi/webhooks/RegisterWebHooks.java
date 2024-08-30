package com.shweit.serverapi.webhooks;

import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.webhooks.server.PluginDisable;
import com.shweit.serverapi.webhooks.server.PluginEnable;
import com.shweit.serverapi.webhooks.server.ServerStart;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public final class RegisterWebHooks {
    private static List<String> urls;
    private static FileConfiguration configuration;

    public void registerWebHooks(final FileConfiguration config) {

        urls = config.getStringList("webhooks.urls");
        configuration = config;

        if (urls.isEmpty()) {
            Logger.warning("No WebHook URL's found in config.yml");
        }

        // Register all webhooks
        new ServerStart().register();
        Logger.debug("Registered server_start WebHook");

        // ServerStop is not registered because it is triggered when the plugin gets disabled
        Logger.debug("Registered server_stop WebHook");

        new PluginDisable().register();
        Logger.debug("Registered plugin_disable WebHook");

        new PluginEnable().register();
        Logger.debug("Registered plugin_enable WebHook");
    }

    public static void sendToAllUrls(final JSONObject jsonObject) {
        if (urls == null || urls.isEmpty()) {
            Logger.warning("No WebHook URL's found in config.yml");
            return;
        }

        for (String url : urls) {
            try {
                URL webhookUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) webhookUrl.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Logger.debug("WebHook sent successfully to " + url);
                } else {
                    Logger.warning("Failed to send WebHook to " + url + ". Response code: " + responseCode);
                }
            } catch (Exception e) {
                Logger.error("Error sending WebHook to " + url + ": " + e.getMessage());
            }
        }
    }

    public static boolean doActivateWebhook(final String eventName) {
        String eventPath = "webhooks." + eventName;

        return configuration.getBoolean(eventPath, true);
    }
}
