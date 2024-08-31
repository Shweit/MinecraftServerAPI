package com.shweit.serverapi.webhooks.weather;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.json.JSONObject;

public final class ThunderChange implements WebHook, Listener {

    private final String eventName = WebHookEnum.THUNDER_CHANGE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onThunderChange(final ThunderChangeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("world", event.getWorld().getName());
        jsonObject.put("thunder", event.toThunderState());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
