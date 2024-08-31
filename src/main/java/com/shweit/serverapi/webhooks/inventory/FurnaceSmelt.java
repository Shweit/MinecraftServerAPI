package com.shweit.serverapi.webhooks.inventory;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.json.JSONObject;

public final class FurnaceSmelt implements WebHook, Listener {

    private final String eventName = WebHookEnum.FURNACE_SMELT.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onFurnaceSmelt(final FurnaceSmeltEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("location", event.getBlock().getLocation().toString());
        jsonObject.put("result", event.getResult().getType().name());
        jsonObject.put("source", event.getSource().getType().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
