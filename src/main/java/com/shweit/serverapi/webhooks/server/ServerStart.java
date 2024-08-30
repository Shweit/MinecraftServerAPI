package com.shweit.serverapi.webhooks.server;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.json.JSONObject;

public final class ServerStart implements WebHook, Listener {

    private final String eventName = WebHookEnum.SERVER_START.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onServerLoad(final ServerLoadEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);

        String message;
        if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
            message = "Server has started";
        } else {
            message = "Server has reloaded";
        }

        jsonObject.put("message", message);
        jsonObject.put("load_type", event.getType().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
