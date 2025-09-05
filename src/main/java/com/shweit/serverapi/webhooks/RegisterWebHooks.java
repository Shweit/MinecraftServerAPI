package com.shweit.serverapi.webhooks;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.webhooks.block.*;
import com.shweit.serverapi.webhooks.enchantment.EnchantItem;
import com.shweit.serverapi.webhooks.entity.*;
import com.shweit.serverapi.webhooks.inventory.Brew;
import com.shweit.serverapi.webhooks.inventory.CraftItem;
import com.shweit.serverapi.webhooks.inventory.FurnaceBurn;
import com.shweit.serverapi.webhooks.inventory.FurnaceSmelt;
import com.shweit.serverapi.webhooks.player.*;
import com.shweit.serverapi.webhooks.server.PluginDisable;
import com.shweit.serverapi.webhooks.server.PluginEnable;
import com.shweit.serverapi.webhooks.server.ServerStart;
import com.shweit.serverapi.webhooks.weather.LightningStrike;
import com.shweit.serverapi.webhooks.weather.ThunderChange;
import com.shweit.serverapi.webhooks.weather.WeatherChange;
import com.shweit.serverapi.webhooks.world.WorldLoad;
import com.shweit.serverapi.webhooks.world.WorldSave;
import com.shweit.serverapi.webhooks.world.WorldUnload;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class RegisterWebHooks {
    private static List<String> urls;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public void registerWebHooks() {

        urls = MinecraftServerAPI.config.getStringList("webhooks.urls");

        if (urls.isEmpty()) {
            Logger.warning("No WebHook URL's found in config.yml");
        }

        // Register all webhooks
        new ServerStart().register();
        Logger.debug("Registered server_start WebHook");

        // ServerStop is not registered here, because it is triggered when the plugin gets disabled
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

        new EntityShootBow().register();
        Logger.debug("Registered entity_shot_bow WebHook");

        new EntityTame().register();
        Logger.debug("Registered entity_tame WebHook");

        new ExplosionPrime().register();
        Logger.debug("Registered explosion_prime WebHook");

        new PlayerDeath().register();
        Logger.debug("Registered player_death WebHook");

        new Brew().register();
        Logger.debug("Registered brew WebHook");

        new CraftItem().register();
        Logger.debug("Registered craft_item WebHook");

        new FurnaceBurn().register();
        Logger.debug("Registered furnace_burn WebHook");

        new FurnaceSmelt().register();
        Logger.debug("Registered furnace_smelt WebHook");

        new PlayerChat().register();
        Logger.debug("Registered player_chat WebHook");

        new PlayerLogin().register();
        Logger.debug("Registered player_login WebHook");

        new PlayerCommand().register();
        Logger.debug("Registered player_command WebHook");

        new PlayerGamemodeChange().register();
        Logger.debug("Registered player_gamemode_change WebHook");

        new PlayerItemBreak().register();
        Logger.debug("Registered player_item_break WebHook");

        new PlayerKick().register();
        Logger.debug("Registered player_kick WebHook");

        new PlayerQuit().register();
        Logger.debug("Registered player_quit WebHook");

        new PlayerRespawn().register();
        Logger.debug("Registered player_respawn WebHook");

        new LightningStrike().register();
        Logger.debug("Registered lightning_strike WebHook");

        new WeatherChange().register();
        Logger.debug("Registered weather_change WebHook");

        new ThunderChange().register();
        Logger.debug("Registered thunder_change WebHook");

        new WorldLoad().register();
        Logger.debug("Registered world_load WebHook");

        new WorldSave().register();
        Logger.debug("Registered world_save WebHook");

        new WorldUnload().register();
        Logger.debug("Registered world_unload WebHook");
    }

    public static void sendToAllUrls(final JSONObject jsonObject) {
        if (urls == null || urls.isEmpty()) {
            Logger.warning("Keine WebHook-URLs in der config.yml gefunden");
            return;
        }

        for (String url : urls) {
            sendWebHook(url, jsonObject);
        }
    }

    private static void sendWebHook(final String url, final JSONObject jsonObject) {
        try {
            URI uri = URI.create(url);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(uri);
            
            // Check for Basic Authentication in URL
            String userInfo = uri.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {
                // Extract username and password from URL
                String[] credentials = userInfo.split(":", 2);
                String username = credentials[0];
                String password = credentials[1];
                
                // Create Basic Auth header
                String auth = username + ":" + password;
                String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                requestBuilder.header("Authorization", "Basic " + encodedAuth);
                
                // Rebuild URI without auth info
                String cleanUrl = url.replaceFirst(userInfo + "@", "");
                uri = URI.create(cleanUrl);
                requestBuilder.uri(uri);
            }
            
            // Check if only message should be sent
            boolean onlyMessage = MinecraftServerAPI.config.getBoolean("webhooks.onlyMessage", false);
            String bodyContent;
            
            if (onlyMessage && jsonObject.has("message")) {
                // Send only the message content as plain text
                bodyContent = jsonObject.getString("message");
                requestBuilder.header("Content-Type", "text/plain; charset=UTF-8");
            } else {
                // Send full JSON object
                bodyContent = jsonObject.toString();
                requestBuilder.header("Content-Type", "application/json");
            }
            
            HttpRequest request = requestBuilder
                    .POST(HttpRequest.BodyPublishers.ofString(bodyContent))
                    .build();

            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            response.thenAccept(httpResponse -> {
                if (httpResponse.statusCode() == 200) {
                    Logger.debug("WebHook '" + jsonObject.get("event") + "' erfolgreich an " + url + "gesendet");
                } else {
                    Logger.warning("Error while sending webhook event '" + jsonObject.get("event") + "' to " + url + ". Responde Code: " + httpResponse.statusCode());
                    Logger.warning(httpResponse.body());
                }
            }).exceptionally(e -> {
                Logger.error("Error while sending webhook event " + jsonObject.get("event") + " to " + url + ": " + e.getMessage());
                return null;
            });

        } catch (Exception e) {
            Logger.error("Error while sending webhook event " + jsonObject.get("event") + " to " + url + ": " + e.getMessage());
        }
    }

    public static boolean doActivateWebhook(final String eventName) {
        String eventPath = "webhooks." + eventName;

        return MinecraftServerAPI.config.getBoolean(eventPath, true);
    }
}
