package com.shweit.serverapi.webhooks.entity;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.json.JSONObject;
import com.shweit.serverapi.webhooks.WebHookEnum;

public final class EntityShootBow implements WebHook, Listener {

    private final String eventName = WebHookEnum.ENTITY_SHOOT_BOW.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onEntityShotBow(final EntityShootBowEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("entity", event.getEntity().getType().name());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("projectile", event.getProjectile().getType().name());
        jsonObject.put("force", event.getForce());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
