package com.shweit.serverapi.webhooks;

import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.webhooks.block.*;
import com.shweit.serverapi.webhooks.enchantment.EnchantItem;
import com.shweit.serverapi.webhooks.entity.CreatureSpawn;
import com.shweit.serverapi.webhooks.entity.CreeperPower;
import com.shweit.serverapi.webhooks.entity.EntityDeath;
import com.shweit.serverapi.webhooks.entity.EntityExplode;
import com.shweit.serverapi.webhooks.server.PluginDisable;
import com.shweit.serverapi.webhooks.server.PluginEnable;
import com.shweit.serverapi.webhooks.server.ServerStart;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
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

        new BlockBreak().register();
        Logger.debug("Registered block_break WebHook");

        new BlockPlace().register();
        Logger.debug("Registered block_place WebHook");

        new BlockBurn().register();
        Logger.debug("Registered block_burn WebHook");

        new BlockRedstone().register();
        Logger.debug("Registered block_redstone WebHook");

        new NotePlay().register();
        Logger.debug("Registered note_play WebHook");

        new SignChange().register();
        Logger.debug("Registered sign_change WebHook");

        new EnchantItem().register();
        Logger.debug("Registered enchant_item WebHook");

        new CreeperPower().register();
        Logger.debug("Registered creeper_power WebHook");

        new CreatureSpawn().register();
        Logger.debug("Registered creature_spawn WebHook");

        new EntityDeath().register();
        Logger.debug("Registered entity_death WebHook");

        new EntityExplode().register();
        Logger.debug("Registered entity_explode WebHook");
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
