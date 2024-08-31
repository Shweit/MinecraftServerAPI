package com.shweit.serverapi.webhooks.entity;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.json.JSONObject;
import com.shweit.serverapi.webhooks.WebHookEnum;

public final class ExplosionPrime implements WebHook, Listener {

    private final String eventName = WebHookEnum.EXPLOSION_PRIME.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onExplosionPrime(final EntityExplodeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("entity", event.getEntity().getType().name());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("radius", event.getYield());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
