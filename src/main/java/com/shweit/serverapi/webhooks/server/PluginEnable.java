package com.shweit.serverapi.webhooks.server;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.json.JSONObject;

public final class PluginEnable implements WebHook, Listener {

    private final String eventName = WebHookEnum.PLUGIN_ENABLE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onPluginEnable(final PluginEnableEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("plugin_name", event.getPlugin().getName());
        jsonObject.put("plugin_version", event.getPlugin().getDescription().getVersion());
        jsonObject.put("plugin_author", event.getPlugin().getDescription().getAuthors().get(0));
        jsonObject.put("plugin_description", event.getPlugin().getDescription().getDescription());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
