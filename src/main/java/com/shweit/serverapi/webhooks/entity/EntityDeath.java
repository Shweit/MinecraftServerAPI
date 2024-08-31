package com.shweit.serverapi.webhooks.entity;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.json.JSONObject;

public final class EntityDeath implements WebHook, Listener {

    private final String eventName = WebHookEnum.ENTITY_DEATH.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("entity", event.getEntity().getType().name());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("drops", event.getDrops().toString());
        jsonObject.put("xp", event.getDroppedExp());

        if (event.getEntity().getKiller() != null) {
            jsonObject.put("killedBy", event.getEntity().getKiller().getName());
        } else {
            jsonObject.put("killedBy", "null");
        }

        if (event.getEntity().getLastDamageCause() != null) {
            jsonObject.put("cause", event.getEntity().getLastDamageCause().getCause().name());
        }

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
