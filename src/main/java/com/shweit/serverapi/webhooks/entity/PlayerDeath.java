package com.shweit.serverapi.webhooks.entity;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.json.JSONObject;
import org.bukkit.event.Listener;

public final class PlayerDeath implements WebHook, Listener {

    private final String eventName = WebHookEnum.PLAYER_DEATH.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("player", event.getEntity().getName());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("cause", event.getDeathMessage());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
