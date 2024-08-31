package com.shweit.serverapi.webhooks.weather;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.json.JSONObject;

public final class LightningStrike implements WebHook, Listener {

    private final String eventName = WebHookEnum.LIGHTNING_STRIKE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onLightningStrike(final LightningStrikeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("location", event.getLightning().getLocation().toString());
        jsonObject.put("cause", event.getCause().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
