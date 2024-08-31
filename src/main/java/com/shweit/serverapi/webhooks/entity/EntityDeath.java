package com.shweit.serverapi.webhooks.entity;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.json.JSONObject;

import java.util.Objects;

public class EntityDeath implements WebHook, Listener {

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
        jsonObject.put("killedBy", Objects.requireNonNull(event.getEntity().getKiller()).getName());
        jsonObject.put("cause", Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
