package com.shweit.serverapi.webhooks.entity;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.json.JSONObject;

public final class CreatureSpawn implements WebHook, Listener {

    private final String eventName = "creature_spawn";

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("entity", event.getEntity().getType().name());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("spawnReason", event.getSpawnReason().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
